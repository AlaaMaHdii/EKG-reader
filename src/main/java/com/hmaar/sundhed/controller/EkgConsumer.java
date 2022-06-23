package com.hmaar.sundhed.controller;

import com.hmaar.sundhed.model.implementation.EKG;
import com.hmaar.sundhed.model.implementation.Puls;
import com.hmaar.sundhed.model.interfaces.EKGData;

import java.util.LinkedList;
import java.util.List;
// @Author Alaa Mahdi

public class EkgConsumer implements Runnable{
    private static final int MAX_SIZE = 1500;

    private double threshold = 0.0055; //I millivolt
    private final LinkedList<EKG> dataList = new LinkedList<>();
    private LinkedList<EKG> dataListForBpm = new LinkedList<>();
    private int timeElapsed = 0;
    private double accumlatedVoltage = 0;
    private int freqeuncyVoltage = 0;
    private final Object emptyLock = new Object();
    private DataController dc;
    private EKG firstQRS;
    private int firstQRSTimeElapsed = 0;
    private int secondQRSTimeElapsed = 0;
    private EKG secondQRS;
    private boolean captureSecond = false;

    public EkgConsumer(DataController dc) {
        this.dc = dc;
    }

    public void enqueue(EKGData ekgData){
        synchronized (dataList){
            // In case buffer is overrun, we just drop data -
            // This is instead of Pausing the producer if the queue is full. (fullLock)
            //ekgdata kan være null pga observer-pattern.
            if (dataList.size()<MAX_SIZE & ekgData != null) {
                dataList.add((EKG) ekgData);
            }
            if (dataListForBpm.size()<MAX_SIZE & ekgData != null) {
                dataListForBpm.add((EKG) ekgData);
            }
        }
    }

    public void waitOnEmpty() throws InterruptedException {
        synchronized (emptyLock){
            emptyLock.wait();
        }
    }
    public void notifyOnEmpty(){
        synchronized (emptyLock){
            emptyLock.notifyAll();
        }
    }


    public void calculateBPM(){
        // so we don't get BPM hits right away
        for (int i = 0; i < dataListForBpm.size(); i++) {
            accumlatedVoltage += dataListForBpm.get(i).getVoltage();
            freqeuncyVoltage++;
        }
        threshold = 0.0055;
        // we need atleast 1 seconds data
        if(dataListForBpm.size() < 800){
            return;
        }
        for(int i = 0; i<dataListForBpm.size();i++){
            if(dataListForBpm.get(i).getVoltage() > threshold && firstQRS == null){
                firstQRS = dataListForBpm.get(i);
                firstQRSTimeElapsed = timeElapsed;
            }
            if(captureSecond && dataListForBpm.get(i).getVoltage() > threshold) {
                System.out.println(threshold);
                secondQRS = dataListForBpm.get(i);
                secondQRSTimeElapsed = timeElapsed;
                double bpm = Math.round(60/((secondQRSTimeElapsed-firstQRSTimeElapsed) * 0.001200));
                dc.setPulsData(new Puls(bpm, firstQRS.getTime()));

                // retrain
                captureSecond = false;
                firstQRS = null;
                secondQRS = null;
                timeElapsed = 0;
            }

            if(dataListForBpm.get(i).getVoltage() < threshold && firstQRS != null && !captureSecond){
                captureSecond = true;
            }
        }
        dataListForBpm.clear();
    }

    @Override
    public void run() {
        while(true){
            if (dataList.isEmpty()){
                try {
                    //This makes the Thread pause until the producer wakes it up
                    waitOnEmpty();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            List<EKG> listCopy = null;
            synchronized (dataList) {
                //Take a copy of list and empty it;
                listCopy = new LinkedList<>(dataList);
                dataList.clear();
            }
            // Process data
            dc.setEkgData(listCopy);
            //find bpm
            //calculateBPM();
        }
    }
}