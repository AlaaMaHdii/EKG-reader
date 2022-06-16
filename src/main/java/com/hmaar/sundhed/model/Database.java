package com.hmaar.sundhed.model;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public Connection conn;

    public String schema = "s190434"; // Database navn

    public String getSchema() throws SQLException {
        if(!conn.isClosed()){
            return conn.getSchema();
        }else{
            return schema;
        }
    }
    public void setSchema(String schema) throws SQLException {
        if(!conn.isClosed()){
            closeConnection();
            this.schema = schema;
            connectToDb();
        }else{
            this.schema = schema;
        }
    }
    public void connectToDb(){
        try{
            conn = DriverManager.getConnection(
                    "jdbc:mysql://mysql-db.caprover.diplomportal.dk:3306/" + schema,"s190434","YvpU8sXPtIeRg7HwZR1Gr");
        }catch(SQLException e){
            System.out.println(e);
        }
    }

    public void closeConnection(){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public AuthenticatedUser loginAsStaff(String cpr) throws SQLException {
        PreparedStatement pstmt = this.conn.prepareStatement("SELECT * FROM staff WHERE cpr = ? LIMIT 1");
        pstmt.setString(1, cpr);
        ResultSet result = pstmt.executeQuery();
        while(result.next()) {   // Move the cursor to the next row
            int id = result.getInt(1);
            //int cpr = result.getInt(2); Bruges ikke da vi allerede har CPR i parameter.
            String fullName = result.getString(3);
            String role = result.getString(4);
            return new AuthenticatedUser(id, cpr, fullName, role, this);
        }

        return null;
    }

    public AuthenticatedUser loginAsPatient(String cpr) throws SQLException {
        PreparedStatement pstmt = this.conn.prepareStatement("SELECT * FROM patients WHERE cpr = ? LIMIT 1");
        pstmt.setString(1, cpr);
        ResultSet result = pstmt.executeQuery();
        while(result.next()) {   // Move the cursor to the next row
            int id = result.getInt(1);
            //int cpr = result.getInt(2); Bruges ikke da vi allerede har CPR i parameter.
            String fullName = result.getString(3);
            return new AuthenticatedUser(id, cpr, fullName, "Patient", this);
        }

        return null;
    }


    public Patient retreivePatient(String cpr) throws SQLException {
        PreparedStatement pstmt = this.conn.prepareStatement("SELECT * FROM patients WHERE cpr = ? LIMIT 1");
        pstmt.setString(1, cpr);
        ResultSet result = pstmt.executeQuery();
        while(result.next()) {   // Move the cursor to the next row
            int id = result.getInt(1);
            String fullName = result.getString(3);
            return new Patient(id, cpr, fullName, this);
        }
        return null;
    }

    public boolean updateComment(int id, String newComment) throws SQLException {
        PreparedStatement pstmt = this.conn.prepareStatement("UPDATE warnings SET comment = ? WHERE id = ?;");
        pstmt.setString(1, newComment);
        pstmt.setInt(2, id);
        return pstmt.execute();
    }

    public ArrayList<Comments> getWarningsByPatientId(int patientId) throws SQLException {
        PreparedStatement pstmt = this.conn.prepareStatement("SELECT * FROM warnings WHERE patientId = ?");
        pstmt.setInt(1, patientId);
        ResultSet result = pstmt.executeQuery();


        ArrayList<Comments> warnings = new ArrayList<Comments>();

        while(result.next()) {   // Move the cursor to the next row
            int id  = result.getInt(1);
            //int patientId  = result.getInt(2);
            int staffWhoLogged  = result.getInt(3);
            String warning = result.getString(4);
            String comment = result.getString(5);
            Timestamp timestamp = result.getTimestamp(6);
            double value = result.getDouble(7);

            warnings.add(new Comments(id, patientId, staffWhoLogged, comment, warning, value, timestamp, this));
        }

        return warnings;
    }


    public ArrayList<Log> getDataByPatientId(int patientId, java.util.Date startDate, java.util.Date endDate) throws SQLException {
        PreparedStatement pstmt = this.conn.prepareStatement("SELECT * FROM logging WHERE patientId = ? AND timestamp BETWEEN ? AND ?");
        pstmt.setInt(1, patientId);
        pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
        pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
        ResultSet result = pstmt.executeQuery();

        ArrayList<Log> logs = new ArrayList<Log>();

        while(result.next()) {   // Move the cursor to the next row
            int id  = result.getInt(1);
            //int patientId  = result.getInt(2);
            int staffWhoLogged  = result.getInt(3);
            String type = result.getString(4);
            double value = result.getDouble(5);
            Timestamp timestamp = result.getTimestamp(6);

            logs.add(new Log(id, patientId, staffWhoLogged, type, value, timestamp));
        }

        return logs;
    }


    public boolean uploadWarning(int patientId, AuthenticatedUser user, String warning, String comment, double value){
        PreparedStatement pstmt = null;
        try {
            pstmt = this.conn.prepareStatement("INSERT INTO warnings (patientId, staffWhoLogged, warning, comment, timestamp, value) VALUES (?, ?, ?, ?, ?, ?);");
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, user.id);
            pstmt.setString(3, warning);
            pstmt.setString(4, comment);
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmt.setDouble(6, value);
            return pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean uploadLog(int patientId, AuthenticatedUser user, String type, double value){
        PreparedStatement pstmt = null;
        try {
            pstmt = this.conn.prepareStatement("INSERT INTO logging (patientId, staffWhoLogged, type, value, timestamp) VALUES (?, ?, ?, ?, ?);");
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, user.id);
            pstmt.setString(3, type);
            pstmt.setDouble(4, value);
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            return pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
