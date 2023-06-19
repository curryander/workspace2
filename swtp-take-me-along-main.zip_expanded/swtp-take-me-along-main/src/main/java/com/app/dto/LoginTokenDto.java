package com.app.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoginTokenDto implements Serializable {
    public String token;

    public LoginTokenDto(String token) {
        this.token = token;
    }

    public LoginTokenDto() {

    }
}
