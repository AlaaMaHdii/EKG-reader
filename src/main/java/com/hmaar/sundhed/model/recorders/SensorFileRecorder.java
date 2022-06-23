package com.hmaar.sundhed.model.recorders;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.hmaar.sundhed.controller.DataController;
import com.hmaar.sundhed.model.DataPublisher;
import com.hmaar.sundhed.model.implementation.EKG;
import javafx.application.Platform;

import java.io.*;
// @Author Alaa Mahdi

public class SensorFileRecorder extends SensorRecorder {
    private DataPublisher subject;
    private BufferedReader reader;

    public DataController getDataController() {
        return dc;
    }

    public void setDataController(DataController dc) {
        this.dc = dc;
    }

    public DataController dc;

    public void setSubject(DataPublisher Subject) {
        this.subject = Subject;
    }


    @Override
    public void run() {
        getDataController().setSensorRecorder(this);
        Platform.runLater(() ->dc.setupSensors());
        try(BufferedReader br = new BufferedReader(new FileReader(""))) {
            for(String line; (line = br.readLine()) != null; ) {
                // process the line.
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
