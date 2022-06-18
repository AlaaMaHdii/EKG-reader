package com.hmaar.sundhed.model;

import com.hmaar.sundhed.model.implementation.EKG;

import java.util.List;

public class DataDAO {

    public void save(EKG data){
        //Simulate a slow save
        try {
            Thread.sleep(500);
            System.out.println("Data Saved: " + data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void save(List<EKG> dataList){
        //Simulate a slow save
        try {
            Thread.sleep(500);
            System.out.println("Data Saved: " + dataList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}