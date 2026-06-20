package com.relaxhub.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReceiptResponse {

    private Long id;
    private String placeName;
    private LocalDate visitDate;
    private LocalTime visitTime;
    private BigDecimal amount;
    private String notes;
    private LocalDateTime createdAt;

    public ReceiptResponse() {
    }

    public ReceiptResponse(
            Long id,
            String placeName,
            LocalDate visitDate,
            LocalTime visitTime,
            BigDecimal amount,
            String notes,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.placeName = placeName;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.amount = amount;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public LocalTime getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(LocalTime visitTime) {
        this.visitTime = visitTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
