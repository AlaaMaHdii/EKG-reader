package com.hmaar.sundhed.tests;

import com.hmaar.sundhed.model.AuthenticatedUser;
import com.hmaar.sundhed.model.Database;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;

import java.sql.SQLException;

class ConnectionToDatabase {
    public Database db = new Database();

    // Mål om forbindelsen til Databasen tager mindre end 300ms
    @Order(1)
    @org.junit.jupiter.api.Test
    void connectToDb() throws SQLException {
        long start = System.currentTimeMillis();
        db.connectToDb();
        long slut = System.currentTimeMillis();
        long timeElapsed = slut - start;
        Assertions.assertFalse(db.conn.isClosed());
        Assertions.assertTrue(timeElapsed < 300);
        System.out.println("Connection to MySQL server took: " + timeElapsed + "ms");
    }

    // Den sidste test
    @Order(2)
    @org.junit.jupiter.api.Test
    void closeConnection() throws SQLException {
        db.connectToDb();
        db.closeConnection();
        Assertions.assertTrue(db.conn.isClosed());
    }

}

class DatabaseCRUDTest {
    public Database db = new Database();

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws SQLException {
        db.connectToDb();
        // Det var ikke muligt at oprette en test db
        //db.setSchema("test");
    }

    @org.junit.jupiter.api.AfterEach
     void tearDown() throws SQLException {
        db.closeConnection();
    }


    // Maks tage 30 sekunder
    @org.junit.jupiter.api.Test
    void loginAsStaff() throws SQLException {
        long start = System.currentTimeMillis();
        AuthenticatedUser user = db.loginAsStaff("0704041234");
        long slut = System.currentTimeMillis();
        long timeElapsed = slut - start;
        Assertions.assertTrue(timeElapsed < 30000);
        System.out.println("Log ind som sundhedsprofessionel " + user.getFullName() + " tog " + timeElapsed + "ms");
    }

    @org.junit.jupiter.api.Test
    void loginAsPatient() throws SQLException {
        long start = System.currentTimeMillis();
        AuthenticatedUser user = db.loginAsPatient("0909992664");
        long slut = System.currentTimeMillis();
        long timeElapsed = slut - start;
        Assertions.assertTrue(timeElapsed < 30000);
        System.out.println("Log ind som patient " + user.getFullName() + " tog " + timeElapsed + "ms");
    }

    @org.junit.jupiter.api.Test
    void retrievePatient() throws SQLException {
        AuthenticatedUser user = db.loginAsPatient("0909992664");
        Assertions.assertEquals("John Hansen", user.getFullName());
        Assertions.assertEquals("Patient", user.getRole());
        Assertions.assertEquals("0909992664", user.getCpr());
    }

    @org.junit.jupiter.api.Test
    void retrieveStaff() throws SQLException {
        AuthenticatedUser user = db.loginAsStaff("0704041234");
        Assertions.assertEquals("Birthe Olsen", user.getFullName());
        Assertions.assertEquals("Læge", user.getRole());
        Assertions.assertEquals("0704041234", user.getCpr());
    }

}