package com.khan.serviceprovider.Models;

public class DesksideManicuresModel {

    String uId,date,shift,price,serviceType;

    public DesksideManicuresModel (){}

    public DesksideManicuresModel(String uId, String date, String shift, String price, String serviceType) {
        this.uId = uId;
        this.date = date;
        this.shift = shift;
        this.price = price;
        this.serviceType = serviceType;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}
