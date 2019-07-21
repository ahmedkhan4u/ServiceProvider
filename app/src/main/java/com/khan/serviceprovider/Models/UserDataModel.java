package com.khan.serviceprovider.Models;

public class UserDataModel {
    private String name,email,contactNumber,uId,designation,imageUrl;

    public UserDataModel(){}

    public UserDataModel
            (String name, String email, String contactNumber, String uId, String designation, String imageUrl) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.uId = uId;
        this.designation = designation;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
