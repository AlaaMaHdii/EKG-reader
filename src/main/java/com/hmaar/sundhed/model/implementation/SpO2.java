package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.SQLData;
import com.hmaar.sundhed.model.interfaces.SpO2Data;

public class SpO2 implements SpO2Data, SQLData {
    private double spO2;
    private long time;
    public final String type = "SpO2";

    public SpO2(double spO2, long time){
        this.spO2 = spO2;
        this.time = time;
    }

    @Override
    public double getSpO2() {
        return spO2;
    }

    @Override
    public void setSpO2(double spO2) {
        this.spO2 = spO2;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public double getSensorValue() {
        return getSpO2();
    }

    @Override
    public void setSensorValue(double sensorValue) {
        setSpO2(sensorValue);
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }
    @Override
    public String getType() {
        return type;
    }

}
