package com.hmaar.sundhed.controller;

import com.hmaar.sundhed.model.implementation.EKG;
import com.hmaar.sundhed.model.implementation.Puls;
import com.hmaar.sundhed.model.interfaces.EKGData;

import java.util.LinkedList;
import java.util.List;

/*
    Der bruges klassen EKG i stedet DataDTO.
    Problemet er fundamental.
    Optimalt har det været at lave en interface for alle sensor-datatyper, som har 3 variabler, timestamp, data og type.
    I stedet for at have 4 forskellige interfaces, og implementation som fremvist af eksempelkoderne.
 */

public class EkgConsumer implements Runnable{
    private static final int MAX_SIZE = 1500;

    private static final double THRESHOLD = 0.0055; //bpm
    private final LinkedList<EKG> dataList = new LinkedList<>();
    private LinkedList<EKG> dataListForBpm = new LinkedList<>();
    private final Object emptyLock = new Object();
    private DataController dc;
    private int firstBpm = 0;
    private int secondBpm = 0;

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
            }else{
                //System.out.println("Dropping data...");
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
            if(dataListForBpm.get(i).getVoltage() < THRESHOLD){
                if(firstBpm == 0) {
                    firstBpm = i;
                }else{
                    secondBpm = i;
                    double bpm = 60/((secondBpm - firstBpm) * 0.001200); // 0.001200 er delay
                    dc.setPulsData(new Puls(bpm, dataListForBpm.get(firstBpm).getTime()));
                    dataListForBpm.clear();
                    return;
                }
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

            // for bpm
            dataListForBpm.addAll(listCopy);
            calculateBPM();
        }
    }
}