package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.EKGData;

public class EKG implements EKGData {
    private double voltage;
    private double time;

    public EKG(double voltage, double time) {
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
    public double getTime() {
        return time;
    }

    @Override
    public void setTime(double time) {
        this.time = time;
    }
}
