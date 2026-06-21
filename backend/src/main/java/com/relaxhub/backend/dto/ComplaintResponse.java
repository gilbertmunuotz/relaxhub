package com.relaxhub.backend.dto;

import java.time.LocalDateTime;

public class ComplaintResponse {

    private Long id;
    private String subject;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public ComplaintResponse() {
    }

    public ComplaintResponse(
            Long id,
            String subject,
            String description,
            String status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
