package com.hmaar.sundhed.model;

import com.hmaar.sundhed.controller.DataController;
import com.hmaar.sundhed.controller.Observer;
import com.hmaar.sundhed.model.interfaces.EKGData;
import com.hmaar.sundhed.model.interfaces.PulsData;
import com.hmaar.sundhed.model.interfaces.SpO2Data;
import com.hmaar.sundhed.model.interfaces.TempData;
import com.hmaar.sundhed.model.recorders.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
// @Author Alaa Mahdi

public class DataPublisher implements Subject {

    private final ArrayList<Observer> observers = new ArrayList<>(); // kommer dog kun til at indeholde en observer...
    public DataController dc;

    // Data
    private EKGData ekgData;
    private PulsData pulsData;
    private SpO2Data spO2Data;
    private TempData tempData;


    // recorders
    private EKGRecorder ekgRecorder;
    private PulsRecorder pulsRecorder;
    private SpO2Recorder spO2Recorder;
    private TempRecorder tempRecorder;
    private SensorRecorder sensorRecorder;
    private Thread sensorThread;

    public DataPublisher(DataController dc) {
        this.dc = dc;
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void unregisterObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers){
            o.update(ekgData, pulsData, tempData, spO2Data);
        }
    }

    public void record(){
        sensorRecorder = new SensorRecorder();
        sensorRecorder.setSubject(this);
        sensorRecorder.setDataController(dc);
        sensorThread = new Thread(sensorRecorder);
        sensorThread.start();

        /* Ikke med i semesterprojekt 2

        //ekg
        ekgRecorder = new EKGRecorder();
        ekgRecorder.setSubject(this);
        ekgRecorder.record();

        // puls
        pulsRecorder = new PulsRecorder();
        pulsRecorder.setSubject(this);
        pulsRecorder.record();

        // temp
        tempRecorder = new TempRecorder();
        tempRecorder.setSubject(this);
        tempRecorder.record();

        // spo2
        spO2Recorder = new SpO2Recorder();
        spO2Recorder.setSubject(this);
        spO2Recorder.record();

         */
    }


    // Getter og setter

    public EKGData getEkgData() {
        return ekgData;
    }

    public void setEkgData(EKGData ekgData) {
        this.ekgData = ekgData;
        notifyObservers();
    }

    public PulsData getPulsData() {
        return pulsData;
    }

    public void setPulsData(PulsData pulsData) {
        this.pulsData = pulsData;
        notifyObservers();
    }

    public SpO2Data getSpO2Data() {
        return spO2Data;
    }

    public void setSpO2Data(SpO2Data spO2Data) {
        this.spO2Data = spO2Data;
        notifyObservers();
    }

    public TempData getTempData() {
        return tempData;
    }

    public void setTempData(TempData tempData) {
        this.tempData = tempData;
        notifyObservers();
    }



}
