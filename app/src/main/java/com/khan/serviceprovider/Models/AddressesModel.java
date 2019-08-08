package com.khan.serviceprovider.Models;

public class AddressesModel {
    String id;
    private String uid;
    private String business_name;
    private String address_name;
    private String state_name;
    private String zip_code;

    public AddressesModel() {

    }

    public AddressesModel(String uid, String business_name, String address_name, String state_name, String zip_code) {
        this.uid = uid;
        this.business_name = business_name;
        this.address_name = address_name;
        this.state_name = state_name;
        this.zip_code = zip_code;
    }

    public AddressesModel(String id, String uid, String business_name, String address_name, String state_name, String zip_code) {
        this.id = id;
        this.uid = uid;
        this.business_name = business_name;
        this.address_name = address_name;
        this.state_name = state_name;
        this.zip_code = zip_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }
}
