package com.example.alerter.Model;

public class Emergencies {
    private String EmergencyTitle, Location, Description, Time, Date, PostedTime;

    public Emergencies() {

    }

    public Emergencies(String emergencyTitle, String location, String description,  String date, String postedTime) {
        EmergencyTitle = emergencyTitle;
        Location = location;
        Description = description;
        Date = date;
        PostedTime  = postedTime;
    }

    public String getEmergencyTitle() {
        return EmergencyTitle;
    }

    public void setEmergencyTitle(String emergencyTitle) {
        EmergencyTitle = emergencyTitle;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPostedTime() {
        return PostedTime;
    }

    public void setPostedTime(String postedTime) {
        PostedTime = postedTime;
    }
}
