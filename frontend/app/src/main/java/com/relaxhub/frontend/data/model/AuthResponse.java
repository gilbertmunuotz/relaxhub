package com.relaxhub.frontend.data.model;

public class AuthResponse {

    private String token;
    private String tokenType;
    private long expiresInMs;
    private UserResponse user;

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public UserResponse getUser() {
        return user;
    }
}
