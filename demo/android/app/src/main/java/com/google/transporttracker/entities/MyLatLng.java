package com.google.transporttracker.entities;

/**
 * Created by sm on 06/02/18.
 */

public class MyLatLng {
    private Double longitude;
    private Double latitude;

    public MyLatLng(double lat, double lng) {
        latitude = lat;
        longitude = lng;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
