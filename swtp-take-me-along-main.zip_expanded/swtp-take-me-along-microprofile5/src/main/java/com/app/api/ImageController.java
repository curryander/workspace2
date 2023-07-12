package com.app.api;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.app.api.AuthManager;
import com.app.dao.ImageDao;
import com.app.dao.UserDao;
import com.app.model.Image;
import com.app.model.User;

@Path("/testst")
public class ImageController {
	/*
	 * @Inject
	 * private ImageDao _imageDao;
	 * 
	 * @Inject
	 * private AuthManager _authManager;
	 * 
	 * @Inject
	 * private UserDao _userDao;
	 * 
	 * @POST
	 * 
	 * @Transactional
	 * 
	 * @Produces(MediaType.APPLICATION_JSON)
	 * 
	 * @Consumes({ "image/jpeg" })
	 * public Response uploadImage(InputStream inputStream, @QueryParam("token")
	 * UUID uuid) {
	 * if (_authManager.isAuthorized(uuid) == false) {
	 * throw new RuntimeException("ERROR: Access not granted");
	 * }
	 * try {
	 * byte[] image = inputStream.readAllBytes();
	 * String username = _authManager.getUsername(uuid);
	 * int imageId = _imageDao.createImage(image, "image/jpeg", username);
	 * User user = _userDao.findUser(username).get();
	 * 
	 * user.setImageId(imageId);
	 * _userDao.update(user);
	 * 
	 * return Response.ok().build();
	 * } catch (Exception e) {
	 * return Response.status(404).build();
	 * }
	 * }
	 * 
	 * @GET
	 * 
	 * @Path("/{imageId}")
	 * 
	 * @Produces({ "image/jpeg" })
	 * public Response getImage(@PathParam("imageId") int
	 * imageId, @QueryParam("token") UUID uuid) {
	 * System.out.println(imageId + " Token: " + uuid);
	 * if (!_authManager.isAuthorized(uuid)) {
	 * throw new RuntimeException("ERROR: Access not granted");
	 * }
	 * 
	 * Optional<Image> optImage = _imageDao.getById(imageId);
	 * System.out.println(optImage.isPresent());
	 * if (optImage.isPresent()) {
	 * Image image = optImage.get();
	 * return Response.ok().entity(image.getImageData()).type("image/jpeg").build();
	 * } else {
	 * System.err.println("ERROR: Image not found!");
	 * throw new RuntimeException("ERROR: Image not found!");
	 * }
	 * }
	 */
}
