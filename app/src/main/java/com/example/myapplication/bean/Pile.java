package com.example.myapplication.bean;

public class Pile {
    private double latitude;
    private double longitude;
    private String type;
    private String status;
    private String id;

    public Pile(double latitude, double longitude, String type, String status, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.status = status;
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pile{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
