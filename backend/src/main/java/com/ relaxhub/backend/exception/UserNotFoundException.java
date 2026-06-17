package com.relaxhub.backend.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("No account found for this email");
    }
}
