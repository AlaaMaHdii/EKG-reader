package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.EKGData;

public class EKG implements EKGData {
    private double voltage;
    private long time;

    public EKG(double voltage, long time) {
        this.time=time;
        this.voltage=voltage;

    }

    @Override
    public double getVoltage() {
        return voltage;
    }

    @Override
    public void setVoltage(double voltage) {
        this.voltage = voltage;
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
