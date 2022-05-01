package com.hmaar.sundhed.model.recorders;

import com.hmaar.sundhed.model.DataPublisher;
import com.hmaar.sundhed.model.implementation.SpO2;

public class SpO2Recorder {
    private DataPublisher subject;

    public void record() {
        new Thread(() -> {
            try {
                //Dummy data generation
                while(true) {
                    Thread.sleep(500);
                    if (subject != null) {
                        subject.setSpO2Data(new SpO2(Math.random(), System.currentTimeMillis()));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

    }

    public void setSubject(DataPublisher Subject) {
        this.subject = Subject;
    }

}
