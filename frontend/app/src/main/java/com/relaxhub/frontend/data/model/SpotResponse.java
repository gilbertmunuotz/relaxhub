package com.relaxhub.frontend.data.model;

public class SpotResponse {

    private long id;
    private String name;
    private String type;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private String phone;
    private Double distanceKm;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }
}
