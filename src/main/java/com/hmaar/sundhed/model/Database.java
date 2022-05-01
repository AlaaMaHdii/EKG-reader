package com.hmaar.sundhed.model;

import java.sql.*;

public class Database {
    Connection conn;

    public void connectToDb(){
        try{
            conn = DriverManager.getConnection(
                    "jdbc:mysql://mysql-db.caprover.diplomportal.dk:3306/s190434","s190434","YvpU8sXPtIeRg7HwZR1Gr");
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
}
