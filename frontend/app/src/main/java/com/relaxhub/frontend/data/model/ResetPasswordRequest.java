package com.relaxhub.frontend.data.model;

public class ResetPasswordRequest {

    private final String email;
    private final String newPassword;

    public ResetPasswordRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
