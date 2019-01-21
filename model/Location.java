package com.example.vmann.mapbox.model;

public class Location extends UberModel {

    /**
     * Human-readable address.
     */
    String address;
    /**
     * Latitude component of location.
     */
    double latitude;
    /**
     * Longitude component of location.
     */
    double longitude;

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}