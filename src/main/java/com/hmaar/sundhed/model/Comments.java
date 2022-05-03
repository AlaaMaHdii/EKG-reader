package com.hmaar.sundhed.model;


import java.sql.Date;

public class Comments {
    int id;
    int patientId;
    int staffWhoLogged;
    String comment;
    String type;

    double value;
    Date timestamp;
    Database db;


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Comments(int id, int patientId, int staffWhoLogged, String comment, String type, double value, Date timestamp, Database db) {
        this.id = id;
        this.patientId = patientId;
        this.staffWhoLogged = staffWhoLogged;
        this.comment = comment;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
        this.db = db;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getStaffWhoLogged() {
        return staffWhoLogged;
    }

    public void setStaffWhoLogged(int staffWhoLogged) {
        this.staffWhoLogged = staffWhoLogged;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
