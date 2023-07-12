package com.app.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserLoginDto implements Serializable {
    String username;
    String password;

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

    @Override
    public String toString() {
        return "UserLoginDto [username=" + username + ", password=" + password + "]";
    }
}
