package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.SQLData;
import com.hmaar.sundhed.model.interfaces.TempData;
// @Author Alaa Mahdi

public class Temp implements TempData, SQLData {
    private double temp;
    private long time;
    public final String type = "Temp";

    public Temp(double temp, long time){
        this.temp = temp;
        this.time = time;
    }

    @Override
    public double getTemp() {
        return temp;
    }

    @Override
    public void setTemp(double temp) {
        this.temp = temp;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public double getSensorValue() {
        return getTemp();
    }

    @Override
    public void setSensorValue(double sensorValue) {
        setTemp(sensorValue);
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
