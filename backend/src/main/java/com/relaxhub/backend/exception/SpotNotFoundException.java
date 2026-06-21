package com.relaxhub.backend.exception;

public class SpotNotFoundException extends RuntimeException {

    public SpotNotFoundException() {
        super("Spot not found");
    }
}
