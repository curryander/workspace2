package com.app.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserDto implements Serializable {
    private String username;
    private String password;
    private String profileImage;

    public UserDto() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
