package com.hmaar.sundhed.model.recorders;

import com.hmaar.sundhed.model.DataPublisher;
import com.hmaar.sundhed.model.implementation.Puls;
// @Author Alaa Mahdi

public class PulsRecorder {
    private DataPublisher subject;

    public void record() {
        new Thread(() -> {
            try {
                //Dummy data generation
                while(true) {
                    Thread.sleep(500);
                    if (subject != null) {
                        subject.setPulsData(new Puls(Math.random(), System.currentTimeMillis()));
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
