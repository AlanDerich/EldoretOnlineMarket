package com.rayson.eldoretonlinemarket.resources;

public class Orders {
    private String productNames,productAmount,orderId,username,totalAmount,productImage;
    private int status;

    public Orders(String productNames, String productAmount, String orderId, String username, String totalAmount, String productImage, int status) {
        this.productNames = productNames;
        this.productAmount = productAmount;
        this.orderId = orderId;
        this.username = username;
        this.totalAmount = totalAmount;
        this.productImage = productImage;
        this.status = status;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Orders() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(String productAmount) {
        this.productAmount = productAmount;
    }

    public String getOrderId() {
        return orderId;
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
