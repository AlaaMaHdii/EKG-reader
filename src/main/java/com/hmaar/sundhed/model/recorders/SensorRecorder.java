package com.hmaar.sundhed.model.recorders;

import com.hmaar.sundhed.model.DataPublisher;
import com.hmaar.sundhed.model.implementation.EKG;
import com.fazecast.jSerialComm.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SensorRecorder implements Runnable {
    private DataPublisher subject;
    private BufferedReader reader;
    private PrintWriter out;

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void resetSerialConnection(){
        if(serialPort == null){
            return;
        }
        if(serialPort.isOpen()){
            try {
                serialPort.closePort();
            }catch(SerialPortInvalidPortException ignored) {}
        }
        reader = null;
        out = null;
        serialPort = null;
    }

    public volatile SerialPort serialPort;

    public void record() {
        new Thread(() -> {
            try {
                //Dummy data generation
                while(true) {
                    Thread.sleep(500);
                    if (subject != null) {
                        subject.setEkgData(new EKG(Math.random(), System.currentTimeMillis()));
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

    // This will update the available serial ports in the GUI
    public void updateComGui(){

    }

    // This will update the status in the GUI, in case we run in an error, we will show it to the user.
    public void updateStatusGui(){

    }

    public void sendMessage(String msg){
        if(serialPort != null & serialPort.isOpen()){
            out.println(msg);
        }
    }

    @Override
    public void run() {
        // Main infinite loop
        while (true) {
            // Setup code
            while (serialPort == null & subject == null ) {
                Thread.onSpinWait();
                // we are waiting for the user to choose an appropriate serialPort
            }

            // Connect to the port
            try{
                serialPort.openPort();
                serialPort.setComPortParameters(115200, 8, 1, 0);
                serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
            }catch (SerialPortInvalidPortException ex){
                //Sensor must have disconnected in the meantime
                resetSerialConnection();
            }
            // Setup in and out
            reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            out = new PrintWriter(serialPort.getOutputStream());

            // Separate thread code
            while (serialPort != null & serialPort.isOpen()) {
                    if(serialPort.bytesAvailable() > 0){
                        // data er klart
                    }
            }
            resetSerialConnection();
        }
    }
}
