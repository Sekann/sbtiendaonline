package com.tienda.app.dtos.auth;

public class UserDetailRequest {

    private String username;

    public UserDetailRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
