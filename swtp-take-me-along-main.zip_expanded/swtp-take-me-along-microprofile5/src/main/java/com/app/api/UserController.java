package com.app.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.transaction.Transactional;

import com.app.dto.LoginTokenDto;
import com.app.dto.TimetableDto;
import com.app.dto.UserDto;
import com.app.dto.CreateUserDto;
import com.app.dao.ImageDao;
import com.app.dao.TimetableDao;
import com.app.dao.UserDao;
import com.app.model.Image;
import com.app.model.Timetable;
import com.app.model.TimetablePk;
import com.app.model.User;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    private UserDao _userDao;
    @Inject
    private ImageDao _imageDao;
    @Inject
    private TimetableDao _timetableDao;

    @Inject
    private AuthManager _authManager;

    @GET
    @Path("/{username}")
    public Response getUserForName(@PathParam("username") String name, @QueryParam("checkExists") Boolean checkExists,
            @QueryParam("token") UUID token) {
        // TODO: rate limit
        if (!checkExists && (token == null || !_authManager.isAuthorized(token)))
            return Response.status(Status.UNAUTHORIZED).build();

        System.out.println("getUserForName: " + name);
        var user = _userDao.findUser(name);

        if (user.isPresent())
            return Response.ok(checkExists ? null : new UserDto(user.get())).build();

        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    public UserDto getUser(@QueryParam("token") UUID uuid) {
        if (!_authManager.isAuthorized(uuid))
            throw new RuntimeException("ERROR: Access not granted");

        String username = _authManager.getUsername(uuid);
        Optional<User> optUser = _userDao.findUser(username);
        if (optUser.isPresent()) {
            UserDto userDto = new UserDto(optUser.get());
            return userDto;
        } else
            throw new RuntimeException("ERROR: User not found");
    }

    @DELETE
    @Transactional
    public boolean deleteUser(@QueryParam("token") UUID uuid) {
        if (!_authManager.isAuthorized(uuid)) {
            throw new RuntimeException("ERROR: Access not granted");
        }

        String username = _authManager.getUsername(uuid);

        if (_userDao.deleteUser(username)) {
            _authManager.deauth(username);
            return true;
        } else {
            return false;
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponseSchema(value = UserDto.class, responseDescription = "Search results", responseCode = "200")
    public Response findUsersNearby(@QueryParam("distance") int distance,
            @QueryParam("journeyType") int journeyType, @QueryParam("starttime") String startTime,
            @QueryParam("endtime") String endTime, @QueryParam("weekday") int weekday, @QueryParam("token") UUID uuid) {

        System.out.println("Getting nearby users");

        if (!_authManager.isAuthorized(uuid))
            Response.status(Status.UNAUTHORIZED).build();

        var username = _authManager.getUsername(uuid);
        var user = _userDao.findUser(username).get();

        List<User> users = _userDao.findUserNearBy(user.getPosition(), distance, journeyType, startTime, endTime,
                weekday,
                user.getUserId());

        return Response.ok(users.stream().map(UserDto::new).collect(Collectors.toList())).build();
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponseSchema(value = LoginTokenDto.class, responseDescription = "Generated auth token", responseCode = "200")
    public Response register(CreateUserDto user) {

        _userDao.createUser(user.getUsername(), user.getPassword(), user.getFirstname(),
                user.getLastname(), user.getEmail(), user.getStreet(), user.getStreetNumber(), user.getZip(),
                user.getCity());

        UUID uuid = _authManager.auth(user.getUsername(), user.getPassword());
        System.out.println("User registered, token: " + uuid.toString());

        return Response.ok(new LoginTokenDto(uuid)).build();
    }

    @GET
    @Path("/{username}/timetable/{weekday}")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponseSchema(value = TimetableDto.class, responseDescription = "Timetable for day", responseCode = "200")
    public Response getTimetable(@PathParam("username") String username, @PathParam("weekday") int weekday,
            @QueryParam("token") UUID token) {
        System.out.println("Getting timetable " + weekday + " for user " + username + " with token: " + token);

        if (!_authManager.isAuthorized(token))
            return Response.status(Status.UNAUTHORIZED).entity("Unauthorized").build();

        if (weekday < 0 || weekday > 7)
            return Response.status(Status.BAD_REQUEST).entity("Invalid weekday").build();

        Optional<User> user = _userDao.findUser(username);

        // dont tell client what exactly was not found
        if (!user.isPresent())
            return Response.status(Status.NOT_FOUND).entity("Resource does not exist").build();

        if (weekday == 0) {
            List<Integer> weekdays = _timetableDao.getWeekdaysForUserId(user.get().getUserId());
            List<TimetableDto> timetablelist = new ArrayList<>();

            for (int i : weekdays) {
                Timetable timetable = _timetableDao.getById(new TimetablePk(user.get().getUserId(), i)).get();
                TimetableDto timetableDto = new TimetableDto(timetable);

                timetablelist.add(timetableDto);
            }
            return Response.ok().entity(timetablelist).type(MediaType.APPLICATION_JSON).build();

        } else {
            Optional<Timetable> tt = _timetableDao.getById(new TimetablePk(user.get().getUserId(), weekday));

            if (!tt.isPresent())
                return Response.status(404).entity("Resource does not exist").build();

            var ttDto = new TimetableDto(tt.get());
            return Response.ok().entity(ttDto).type(MediaType.APPLICATION_JSON).build();
        }
    }

    @POST
    @Path("/{username}/timetable/{weekday}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadTimetable(List<TimetableDto> timetable, @PathParam("username") String username,
            @PathParam("weekday") int weekday,
            @QueryParam("token") UUID token) {
        System.out.println("Setting timetable " + weekday + " for user " + username + " with token: " + token);

        if (!_authManager.isAuthorized(token) || !username.equals(_authManager.getUsername(token)))
            return Response.status(Status.UNAUTHORIZED).entity("Unauthorized").build();

        if (weekday < 0 || weekday > 7)
            return Response.status(Status.BAD_REQUEST).entity("Invalid weekday").build();

        Optional<User> user = _userDao.findUser(username);
        // dont tell client what exactly was not found
        if (!user.isPresent())
            return Response.status(Status.NOT_FOUND).entity("Resource does not exist").build();

        for (var tt : timetable) {
            if (tt.getStartTime().isAfter(tt.getEndTime()))
                return Response.status(Status.BAD_REQUEST).entity("End time cannot be earlier than start time").build();
        }

        if (weekday == 0) {
            for (var day = 1; day <= 7; day++) {
                var pk = new TimetablePk(user.get().getUserId(), day);

                // dumb af java closures
                final var d = day;
                var req = timetable.stream().filter(o -> o.getWeekday() == d).findFirst();
                if (!req.isPresent()) {
                    var old = _timetableDao.getById(pk);
                    if (old.isPresent()) {
                        _timetableDao.delete(old.get());
                    }

                    continue;
                }

                System.out.println("Setting timetable for day " + d + ": " + req.get() + " with pk: " + pk);
                var newTt = new Timetable(pk, req.get().getStartTime(), req.get().getEndTime());
                if (_timetableDao.getById(pk).isPresent())
                    _timetableDao.update(newTt);
                else
                    _timetableDao.insert(newTt);
            }

            return Response.ok().build();

        } else {

            // TODO: update single entry
            Optional<Timetable> tt = _timetableDao.getById(new TimetablePk(user.get().getUserId(), weekday));

            if (!tt.isPresent())
                return Response.status(404).entity("Resource does not exist").build();

            var ttDto = new TimetableDto(tt.get());
            return Response.ok().entity(ttDto).type(MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Path("/{username}/image")
    @Consumes()
    @Produces({ "image/jpeg", "image/png", "image/gif", "image/bmp" })
    @APIResponseSchema(value = Image.class, responseDescription = "Raw image data", responseCode = "200")
    public Response getImage(@PathParam("username") String username, @QueryParam("token") UUID token) {
        System.out.println("Getting image for user " + username + " with token: " + token);

        if (!_authManager.isAuthorized(token))
            return Response.status(Status.UNAUTHORIZED).build();

        Optional<User> user = _userDao.findUser(username);

        // dont tell client what exactly was not found
        if (!user.isPresent())
            return Response.status(Status.NOT_FOUND).entity("Resource does not exist").build();

        Optional<Image> img = _imageDao.getById(user.get().getImageId());

        if (!img.isPresent())
            return Response.status(Status.NOT_FOUND).entity("Resource does not exist").build();

        return Response.ok().entity(img.get().getImageData()).type(img.get().getContentType()).build();
    }

    @POST
    @Path("/{username}/image")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({ "image/jpeg", "image/png" })
    public Response uploadImage(InputStream inputStream, @PathParam("username") String username,
            @QueryParam("token") UUID uuid) {

        System.out.println("Uploading image for user " + username + " with token: " + uuid);

        if (!_authManager.isAuthorized(uuid))
            return Response.status(Status.UNAUTHORIZED).build();

        var userNameForToken = _authManager.getUsername(uuid);

        if (!username.equals(userNameForToken))
            return Response.status(Status.UNAUTHORIZED).build();

        try {
            byte[] image = inputStream.readAllBytes();

            int imageid = _imageDao.createImage(image, "image/jpeg", username);
            User user = _userDao.findUser(username).get();

            user.setImageId(imageid);
            _userDao.update(user);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
