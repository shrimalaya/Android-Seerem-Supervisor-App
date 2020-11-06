package com.example.supervisor_seerem.model;

public class ModelLocation {
    private double latitude;
    private double longitude;

    public ModelLocation(Object o) {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public ModelLocation (ModelLocation loc) {
        this.latitude = loc.getLatitude();
        this.longitude = loc.getLongitude();
    }

    public ModelLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return latitude + ", " + longitude;
    }
}
