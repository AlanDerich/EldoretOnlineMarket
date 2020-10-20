package com.rayson.eldoretonlinemarket.resources;

import android.os.Parcel;
import android.os.Parcelable;

public class Products1 implements Parcelable {
    private String name, Image, description, price, menuId;

    public Products1() {
    }

    public Products1(String name, String image) {
        this.name = name;
        Image = image;
        this.description = description;
        this.price = price;
        this.menuId = menuId;
    }

    public Products1(String name, String image, String description, String price, String menuId) {
        this.name = name;
        Image = image;
        this.description = description;
        this.price = price;
        this.menuId = menuId;
    }

    protected Products1(Parcel in) {
        name = in.readString();
        Image = in.readString();
        description = in.readString();
        price = in.readString();
        menuId = in.readString();
    }

    public static final Creator<Products1> CREATOR = new Creator<Products1>() {
        @Override
        public Products1 createFromParcel(Parcel in) {
            return new Products1(in);
        }

        @Override
        public Products1[] newArray(int size) {
            return new Products1[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(Image);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeString(menuId);
    }
}
