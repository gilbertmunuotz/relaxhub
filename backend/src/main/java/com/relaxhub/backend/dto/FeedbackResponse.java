package com.relaxhub.backend.dto;

import java.time.LocalDateTime;

public class FeedbackResponse {

    private Long id;
    private Integer rating;
    private String message;
    private LocalDateTime createdAt;

    public FeedbackResponse() {
    }

    public FeedbackResponse(Long id, Integer rating, String message, LocalDateTime createdAt) {
        this.id = id;
        this.rating = rating;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
