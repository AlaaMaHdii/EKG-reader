package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.PulsData;

public class Puls implements PulsData {
    private double puls;
    private long time;

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
    public void setTime(long time) {
        this.time = time;
    }

}
