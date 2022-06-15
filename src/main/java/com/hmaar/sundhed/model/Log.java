package com.hmaar.sundhed.model;

import com.hmaar.sundhed.model.implementation.EKG;
import com.hmaar.sundhed.model.implementation.Puls;
import com.hmaar.sundhed.model.implementation.SpO2;
import com.hmaar.sundhed.model.implementation.Temp;
import com.hmaar.sundhed.model.interfaces.EKGData;
import com.hmaar.sundhed.model.interfaces.PulsData;
import com.hmaar.sundhed.model.interfaces.SpO2Data;
import com.hmaar.sundhed.model.interfaces.TempData;

import java.sql.Date;
import java.sql.Timestamp;

public class Log {
    public int id;
    public int patientId;
    public int staffWhoLogged;
    public String type;
    public double value;
    public Timestamp timestamp;


    public Log(int id, int patientId, int staffWhoLogged, String type, double value, Timestamp timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.staffWhoLogged = staffWhoLogged;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public SpO2 generateSpO2(){
        return new SpO2(value, timestamp.getTime());
    }

    public EKG generateEkg(){
        return new EKG(value, timestamp.getTime());
    }

    public Temp generateTemp(){
        return new Temp(value, timestamp.getTime());
    }

    public Puls generatePuls(){
        return new Puls(value, timestamp.getTime());
    }
}
