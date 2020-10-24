package com.rayson.eldoretonlinemarket.resources;

public class CartDetails {
    private String name, Image, amount, price, menuId,username,owner_name;

    public CartDetails() {
    }

    public CartDetails(String name, String image, String amount, String price, String menuId, String username, String owner_name) {
        this.name = name;
        Image = image;
        this.amount = amount;
        this.price = price;
        this.menuId = menuId;
        this.username = username;
        this.owner_name = owner_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
