package com.hmaar.sundhed.model.implementation;

import com.hmaar.sundhed.model.interfaces.EKGData;
import com.hmaar.sundhed.model.interfaces.SQLData;

public class EKG implements EKGData, SQLData {
    private final boolean ADCConverted;
    private double voltage;
    private long time;
    private int id;
    public final String type = "EKG";
    public EKG(double voltage, long time, boolean ADCConverted) {
        this.time=time;
        this.voltage=voltage;
        this.ADCConverted = ADCConverted;
    }

    @Override
    public double getVoltage() {
        if(!ADCConverted){
            //return ((voltage / 4095)*5); // 12bit adc (2**12-1) og 5v
            return ((voltage / 4095)*5)/835; // 12bit adc (2**12-1) og 5v med gain
        }
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
    public double getSensorValue() {
        return getVoltage();
    }

    @Override
    public void setSensorValue(double sensorValue) {
        setVoltage(sensorValue);
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
