package com.hmaar.sundhed.controller;

import com.hmaar.sundhed.model.implementation.EKG;
import com.hmaar.sundhed.model.implementation.Puls;
import com.hmaar.sundhed.model.interfaces.EKGData;

import java.util.LinkedList;
import java.util.List;

public class EkgConsumer implements Runnable{
    private static final int MAX_SIZE = 1500;

    private static final double THRESHOLD = 0.00150; //I millivolt
    private final LinkedList<EKG> dataList = new LinkedList<>();
    private LinkedList<EKG> dataListForBpm = new LinkedList<>();
    private int timeElapsed = 0;
    private final Object emptyLock = new Object();
    private DataController dc;
    private EKG firstBpm;
    private EKG secondBpm;

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
        for(int i = 0; i<dataListForBpm.size();i++){
            if(dataListForBpm.get(i).getVoltage() > THRESHOLD){
                if(firstBpm == null) {
                    firstBpm = dataListForBpm.get(i);
                }else{
                    if(timeElapsed < 200){
                        timeElapsed += 1;
                        return;
                    }
                    secondBpm = dataListForBpm.get(i);
                    double bpm = Math.round(60/((timeElapsed) * 0.001200)); // 0.001200 er delay
                    // Somehow this code sometimes get negative
                    if(bpm < 0){
                        return;
                    }
                    System.out.println(bpm);
                    dc.setPulsData(new Puls(bpm, firstBpm.getTime()));
                    dataListForBpm.clear();
                    timeElapsed = 0;
                    firstBpm = null;
                    secondBpm = null;
                    return;
                }
            }else{
                timeElapsed += 1;
            }
        }
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
            long start = System.currentTimeMillis();
            // Process data
            if(listCopy != null) {
                dc.setEkgData(listCopy);
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;
                System.out.println("Cleared " + listCopy.size() + " data in " + timeElapsed + "ms");
            }
            //find bpm
            calculateBPM();
        }
    }
}