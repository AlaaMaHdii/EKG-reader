package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.TempData;

public class Temp implements TempData {
    private double temp;
    private double time;

    public Temp(double temp, double time){
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
    public double getTime() {
        return time;
    }

    @Override
    public void setTime(double time) {
        this.time = time;
    }
}
