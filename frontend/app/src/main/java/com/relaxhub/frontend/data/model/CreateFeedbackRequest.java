package com.relaxhub.frontend.data.model;

public class CreateFeedbackRequest {

    private final int rating;
    private final String message;

    public CreateFeedbackRequest(int rating, String message) {
        this.rating = rating;
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public String getMessage() {
        return message;
    }
}
