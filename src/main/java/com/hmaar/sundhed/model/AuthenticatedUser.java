package com.hmaar.sundhed.model;

import java.util.Objects;

public class AuthenticatedUser {
    private Database db;
    private int id;
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

    public AuthenticatedUser(int id, String cpr, String fullName, String role, Database db){
        this.id = id;
        this.cpr = cpr;
        this.fullName = fullName;
        this.role = role;
        this.isStaff = !this.role.equals("Patient");
        this.db = db;
    }
}
