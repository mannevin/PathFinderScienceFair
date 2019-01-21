package com.example.vmann.mapbox.model;

import android.location.Location;

public class History extends UberModel {

    /**
     * Unique user identifier.
     */
    String uuid;
    /**
     * Unix timestamp of trip request time.
     */
    long request_time;
    /**
     * Unique identifier representing a specific product for a given latitude & longitude. For
     * example, uberX in San Francisco will have a different product_id than uberX in Los Angeles.
     */
    String product_id;
    /**
     * Status of the trip. Only returns completed for now.
     */
    String status;
    /**
     * Length of trip in miles.
     */
    float distance;
    /**
     * Unix timestamp of trip start time.
     */
    long start_time;
    /**
     * Latitude, longitude & address of the start location.
     */
    Location start_location;
    /**
     * Unix timestamp of trip end time.
     */
    long end_time;
    /**
     * Latitude, longitude & address of the end location.
     */
    Location end_location;

    public String getUUID() {
        return uuid;
    }

    public long getRequestTime() {
        return request_time;
    }

    public String getProductId() {
        return product_id;
    }

    public String getStatus() {
        return status;
    }

    public float getDistance() {
        return distance;
    }

    public long getStart_time() {
        return start_time;
    }

    public Location getStartLocation() {
        return start_location;
    }

    public long getEndTime() {
        return end_time;
    }

    public Location getEndLocation() {
        return end_location;
    }

}
