package com.khan.serviceprovider.Models;

import com.khan.serviceprovider.ConferenceRoom;

public class ConferenceRoomDateModel {
    private String uId,roomNo,dateAndTime;

    public ConferenceRoomDateModel(){}

    public ConferenceRoomDateModel(String uId, String roomNo, String dateAndTime) {
        this.uId = uId;
        this.roomNo = roomNo;
        this.dateAndTime = dateAndTime;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
