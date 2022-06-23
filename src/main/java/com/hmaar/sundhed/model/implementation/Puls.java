package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.PulsData;
import com.hmaar.sundhed.model.interfaces.SQLData;
// @Author Alaa Mahdi

public class Puls implements PulsData, SQLData {
    private double puls;
    private long time;
    public final String type = "Puls";
    public Puls(double puls, long time){
        this.puls = puls;
        this.time = time;
    }

    @Override
    public double getPuls() {
        return puls;
    }

    @Override
    public void setPuls(double puls) {
        this.puls = puls;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public double getSensorValue() {
        return getPuls();
    }

    @Override
    public void setSensorValue(double sensorValue) {
        setPuls(sensorValue);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

}
