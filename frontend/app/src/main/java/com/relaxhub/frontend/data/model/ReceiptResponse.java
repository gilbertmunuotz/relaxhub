package com.relaxhub.frontend.data.model;

public class ReceiptResponse {

    private long id;
    private String placeName;
    private String visitDate;
    private String visitTime;
    private Double amount;
    private String notes;

    public long getId() {
        return id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public Double getAmount() {
        return amount;
    }

    public String getNotes() {
        return notes;
    }
}
