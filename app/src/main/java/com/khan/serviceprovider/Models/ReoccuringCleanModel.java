package com.khan.serviceprovider.Models;

public class ReoccuringCleanModel {

    private String sizeOfSuite,date,price,uId;

    public ReoccuringCleanModel(){}

    public ReoccuringCleanModel(String sizeOfSuite, String date, String price, String uId) {
        this.sizeOfSuite = sizeOfSuite;
        this.date = date;
        this.price = price;
        this.uId = uId;
    }

    public String getSizeOfSuite() {
        return sizeOfSuite;
    }

    public void setSizeOfSuite(String sizeOfSuite) {
        this.sizeOfSuite = sizeOfSuite;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}

