package com.hmaar.sundhed.model;

import com.hmaar.sundhed.controller.DataController;
import com.hmaar.sundhed.model.implementation.EKG;
import com.hmaar.sundhed.model.implementation.Puls;
import com.hmaar.sundhed.model.implementation.SpO2;
import com.hmaar.sundhed.model.implementation.Temp;
import com.hmaar.sundhed.model.interfaces.SQLData;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AuthenticatedUser {
    private Database db;
    public int id;
    public String cpr;

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String fullName;
    public String role;

    public boolean isStaff;
    public String lastWarning = "";
    public Long lastWarningPulsDate;
    public Long lastWarningTempDate;
    public Long lastWarningSpO2Date;

    public boolean uploadWarning(int patientId, String warning, String comment, double value, long time, DataController dataController){
        if(lastWarningPulsDate == null || warning.contains("Puls") && System.currentTimeMillis()  >= ( lastWarningPulsDate + (30*1000))){
            lastWarningPulsDate = System.currentTimeMillis();
            boolean result =  this.db.uploadWarning(patientId, this, warning, comment, value, time);
            dataController.loadDataToTable();
            return result;
        } else if(lastWarningTempDate == null || warning.contains("Temp") && System.currentTimeMillis()  >= ( lastWarningTempDate + (30*1000))){
            lastWarningTempDate = System.currentTimeMillis();
            boolean result =  this.db.uploadWarning(patientId, this, warning, comment, value, time);
            dataController.loadDataToTable();
            return result;
        }else if(lastWarningSpO2Date == null || warning.contains("SpO2") && System.currentTimeMillis()  >= ( lastWarningSpO2Date + (30*1000))){
            lastWarningSpO2Date = System.currentTimeMillis();
            boolean result =  this.db.uploadWarning(patientId, this, warning, comment, value, time);
            dataController.loadDataToTable();
            return result;
        }

        return false;
    }

    public boolean uploadLog(int patientId, SQLData log){
        return this.db.uploadLog(patientId, this, log);
    }

    public boolean uploadLog(int patientId, List<SQLData> logs){
        return this.db.uploadLog(patientId, this, logs);
    }


    public AuthenticatedUser(int id, String cpr, String fullName, String role, Database db){
        this.id = id;
        this.cpr = cpr;
        this.fullName = fullName;
        this.role = role;
        this.isStaff = !this.role.equals("Patient");
        this.db = db;
    }
}
