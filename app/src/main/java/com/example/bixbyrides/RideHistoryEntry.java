package com.example.bixbyrides;

public class RideHistoryEntry {
    private String startLocation;
    private String endLocation;
    private double distance;
    private double price;
    private String timestamp;

    public RideHistoryEntry(String startLocation, String endLocation, double distance, double price, String timestamp) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.distance = distance;
        this.price = price;
        this.timestamp = timestamp;
    }

    // Getters
    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public double getDistance() {
        return distance;
    }

    public double getPrice() {
        return price;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
