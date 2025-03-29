package com.moutamid.uchannelboostadmin.models;

public class Subscription {
    private String id;
    private String duration;
    private double price;
    private int discount;

    public Subscription() {
        // Default constructor required for Firebase
    }

    public Subscription(String id, String duration, double price, int discount) {
        this.id = id;
        this.duration = duration;
        this.price = price;
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
