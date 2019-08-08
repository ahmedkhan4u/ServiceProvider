package com.khan.serviceprovider.Models;

public class DryCleanModel {
    private String date, price, uId;

    public DryCleanModel(){}

    public DryCleanModel(String date, String price, String uId) {
        this.date = date;
        this.price = price;
        this.uId = uId;
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
