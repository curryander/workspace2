package com.app.dto;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public class LoginTokenDto implements Serializable {
    private String token;

    public LoginTokenDto(String token) {
        this.token = token;
    }

    public LoginTokenDto() {

    }

    public LoginTokenDto(UUID uuid) {
        this.token = uuid.toString();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
