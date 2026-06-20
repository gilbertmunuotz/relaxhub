package com.relaxhub.frontend.data.model;

public class CreateComplaintRequest {

    private final String subject;
    private final String description;

    public CreateComplaintRequest(String subject, String description) {
        this.subject = subject;
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }
}
