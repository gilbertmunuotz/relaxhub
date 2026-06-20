package com.relaxhub.frontend.data.model;

public class CreateReceiptRequest {

    private final String placeName;
    private final String visitDate;
    private final String visitTime;
    private final Double amount;
    private final String notes;

    public CreateReceiptRequest(
            String placeName,
            String visitDate,
            String visitTime,
            Double amount,
            String notes
    ) {
        this.placeName = placeName;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.amount = amount;
        this.notes = notes;
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
