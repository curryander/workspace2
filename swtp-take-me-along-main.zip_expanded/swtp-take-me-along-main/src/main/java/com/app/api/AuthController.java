package com.app.api;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.app.dto.LoginTokenDto;
import com.app.dto.UserLoginDto;
import com.app.model.User;
import com.app.model.UserManager;

@Path("/auth")
@Singleton
public class AuthController {
    @Inject
    AccessManager accessManager;

    @Inject
    UserManager userManager;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Login(UserLoginDto user) {
        System.out.println("Login User " + user.getUsername().length());
        try {
            Optional<User> optUser = this.userManager.lookupUser(user.getUsername());
            if (optUser.isPresent()) {
                // Check Password
                if (user.getPassword().equals(optUser.get().getPassword()) == false) {
                    System.err.println("Wrong Password");
                    throw new RuntimeException("Wrong Password");
                }

                // Login
                UUID uuid = this.accessManager.login(user.getUsername());
                LoginToken token = new LoginToken(uuid.toString());
                return Response.ok().entity(token).build();
            } else {
                System.err.println("User " + user.getUsername() + " not known");
                throw new RuntimeException("User not known");
            }
        } catch (Exception exce) {
            System.out.println("ERROR " + exce.getMessage());
            return Response.status(404).build();
        }
    }

    @DELETE
    @Path("/{userId}")
    public Response Logout(@PathParam("userId") String loginToken) {
        try {
            System.out.println("Logout: " + this.accessManager.getLoginName(UUID.fromString(loginToken)));

            this.accessManager.logout(UUID.fromString(loginToken));
            return Response.ok().build();
        } catch (Exception exce) {
            System.out.println("ERROR " + exce.getMessage());
            return Response.status(404).build();
        }
    }
}
