package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.SpO2Data;

public class SpO2 implements SpO2Data {
    private double spO2;
    private long time;

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
    public void setTime(long time) {
        this.time = time;
    }
}
