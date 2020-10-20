package com.rayson.eldoretonlinemarket;

public class UserDestinationInfo {
    public String longitude;
    public String latitude;
    public String userName;
    public String destinationNickName;
    public UserDestinationInfo(){
    }

    public UserDestinationInfo(String latitude, String longitude, String userName, String destinationNickName) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.userName = userName;
        this.destinationNickName = destinationNickName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDestinationNickName() {
        return destinationNickName;
    }

    public void setDestinationNickName(String destinationNickName) {
        this.destinationNickName = destinationNickName;
    }
}
