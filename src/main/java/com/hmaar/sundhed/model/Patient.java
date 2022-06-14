package com.hmaar.sundhed.model;

import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;

public class Patient {
    public String cpr;
    public int id;
    public String fullName;

    private Database db;


    public Patient(int id, String cpr, String fullName, Database db){
        this.id = id;
        this.cpr = cpr;
        this.fullName = fullName;
        this.db = db;
    }

    public String getCpr() {
        return cpr;
    }


    public String getCprFormatted() {
        return cpr.substring(0, 6) + "-" + cpr.substring(6);
    }

    private LocalDate convertToLocalDate(Date dato) {
        return LocalDate.ofInstant(dato.toInstant(),
                ZoneId.systemDefault()); // brug default;
    }

    public int getAge() {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("ddMMyy").parse(cpr.substring(0, 6));
        } catch (ParseException e) {
            throw new RuntimeException(e);

        }
        Date date2 = new Date();
        Long between = ChronoUnit.YEARS.between(convertToLocalDate(date1), convertToLocalDate(date2));
        return between.intValue();
    }

    public void setCpr(String cpr) {
        this.cpr = cpr.replace("-", "");
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public ArrayList<Comments> getWarnings(){
        try {
            return db.getWarningsByPatientId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    public ArrayList<Log> getLogs(Date startDate, Date endDate){
        try {
            return db.getDataByPatientId(id, startDate, endDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
