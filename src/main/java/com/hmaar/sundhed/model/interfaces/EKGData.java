package com.hmaar.sundhed.model.interfaces;

public interface EKGData {
    void setVoltage(double voltage);
    double getVoltage();
    void setTime(long time);
    long getTime();
    void setId(int id);
    int getId();
}
