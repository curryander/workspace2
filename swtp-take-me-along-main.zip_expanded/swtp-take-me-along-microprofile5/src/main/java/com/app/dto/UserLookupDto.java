package com.app.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserLookupDto implements Serializable {
    String username;

    public UserLookupDto() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
