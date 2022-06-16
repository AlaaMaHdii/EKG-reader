package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.TempData;

public class Temp implements TempData {
    private double temp;
    private long time;

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
    public void setTime(long time) {
        this.time = time;
    }

}
