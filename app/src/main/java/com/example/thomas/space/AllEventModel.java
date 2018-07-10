package com.example.thomas.space;

import java.util.ArrayList;

/**
 * This class is for offline uses
 */
public class AllEventModel {

    private String eventName, startDate, startTime, endDate, endTime, location, detail, creator;
    private ArrayList<String> peopleList;
    // Constructor
    // the StringArray from the MainPageActivity will pass it in this EventModel and it will create the cardView
    public AllEventModel(String eventName, String startDate, String startTime, String endDate, String endTime, String location, String creator) {
        peopleList = new ArrayList<>();
        this.eventName = eventName;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = location;
        this.creator = creator;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ArrayList<String> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(ArrayList<String> peopleList) {
        this.peopleList = peopleList;
    }
}
