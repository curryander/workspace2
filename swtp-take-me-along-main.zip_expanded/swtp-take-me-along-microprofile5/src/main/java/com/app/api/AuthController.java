package com.app.api;

import java.util.Optional;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.app.dao.UserDao;
import com.app.dto.LoginTokenDto;
import com.app.dto.UserLoginDto;
import com.app.model.User;

@Path("/auth")
@Singleton
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    private AuthManager _authManager;
    @Inject
    private UserDao _userDao;

    @POST
    public Response login(UserLoginDto loginDto) {
        System.out.println("Trying to log in user " + loginDto);

        try {
            Optional<User> optUser = _userDao.findUser(loginDto.getUsername());
            if (!optUser.isPresent()) {
                System.err.println("User " + loginDto.getUsername() + " not known");
                throw new RuntimeException("ERROR: User not found");
            }
            // Login
            UUID uuid = _authManager.auth(loginDto.getUsername(), loginDto.getPassword());

            System.out.println("authorizing : " + loginDto.getUsername());
            System.out.println(" -> " + uuid);

            LoginTokenDto token = new LoginTokenDto(uuid);
            return Response.ok().entity(token).build();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            // TODO: sanitize exception message
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @DELETE
    public Response logout(@QueryParam("token") String loginToken) {
        System.out.println("Logout for token: " + loginToken);
        try {
            System.out.println("Logging out: " +
                    _authManager.getUsername(UUID.fromString(loginToken)));

            return _authManager.deauth(UUID.fromString(loginToken)) ? Response.ok().build()
                    : Response.status(400).entity("Unknown token").build();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            return Response.status(404).entity(e.getMessage()).build();
        }
    }
}
