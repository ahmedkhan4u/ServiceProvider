package com.khan.serviceprovider.Models;

public class CarWashModel {
    private String uId,washType,price,dateAndTime;

    public CarWashModel (){}

    public CarWashModel(String uId, String washType, String price, String dateAndTime) {
        this.uId = uId;
        this.washType = washType;
        this.price = price;
        this.dateAndTime = dateAndTime;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getWashType() {
        return washType;
    }

    public void setWashType(String washType) {
        this.washType = washType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
