package com.rayson.eldoretonlinemarket.resources;

public class OrderIds {
    String orderId,username,dateAndTime,latitude,longitude,date,ownerEmail;
    int totalAmount,status;
    public OrderIds() {
    }

    public OrderIds(String orderId, String username, String dateAndTime, String latitude, String longitude, String date, String ownerEmail, int totalAmount, int status) {
        this.orderId = orderId;
        this.username = username;
        this.dateAndTime = dateAndTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.ownerEmail = ownerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
