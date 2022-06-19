package com.hmaar.sundhed.controller;

import com.hmaar.sundhed.model.implementation.EKG;
import com.hmaar.sundhed.model.interfaces.EKGData;

import java.util.LinkedList;
import java.util.List;

/*
    Der bruges klassen EKG i stedet DataDTO.
    Problemet er fundamental.
    Optimalt har det været at lave en interface for alle sensor-datatyper, som har 3 variabler, timestamp, data og type.
    I stedet for at have 4 forskellige interfaces, og implementationer.
 */

public class EkgConsumer implements Runnable{
    private static final int MAX_SIZE = 1500;
    private final LinkedList<EKG> dataList = new LinkedList<>();
    private final Object emptyLock = new Object();
    private DataController dc;

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
                System.out.println("Dropping data...");
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
            List<EKG> listCopy;
            synchronized (dataList){
                //Take a copy of list and empty it;
                listCopy = new LinkedList<>(dataList);
                dataList.clear();
            }

            // Process data
            for (EKG ekg: listCopy) {
                dc.setEkgData(ekg);
            }
            dc.cleanUpGraphs();
        }
    }
}