package com.hmaar.sundhed.model;


import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
// @Author Alaa Mahdi

public class Comments {
    int id;
    int patientId;
    int staffWhoLogged;
    String comment;
    String warning;

    double value;
    Timestamp timestamp;
    Database db;


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        try {
            db.updateComment(id, comment);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.comment = comment;
    }

    public Comments(int id, int patientId, int staffWhoLogged, String comment, String warning, double value, Timestamp timestamp, Database db) {
        this.id = id;
        this.patientId = patientId;
        this.staffWhoLogged = staffWhoLogged;
        this.comment = comment;
        this.warning = warning;
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

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
