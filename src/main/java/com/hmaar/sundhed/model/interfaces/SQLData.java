package com.hmaar.sundhed.model.interfaces;
// @Author Alaa Mahdi


public interface SQLData {
    void setTime(long time);
    long getTime();

    double getSensorValue();
    void setSensorValue(double sensorValue);
    String getType();
}
