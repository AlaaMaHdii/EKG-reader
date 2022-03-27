package com.hmaar.sundhed.model.recorders;

import com.hmaar.sundhed.controller.Observer;
import com.hmaar.sundhed.model.DataPublisher;
import com.hmaar.sundhed.model.Subject;
import com.hmaar.sundhed.model.implementation.EKG;

public class EKGRecorder {
    private DataPublisher subject;

    public void record() {
        new Thread(() -> {
            try {
                //Dummy data generation
                while(true) {
                    Thread.sleep(500);
                    if (subject != null) {
                        subject.setEkgData(new EKG(Math.random(), Math.random()));
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
