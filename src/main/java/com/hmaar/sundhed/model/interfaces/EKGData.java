package com.hmaar.sundhed.model.interfaces;
// @Author Alaa Mahdi

public interface EKGData {
    void setVoltage(double voltage);
    double getVoltage();
    void setTime(long time);
    long getTime();
}
