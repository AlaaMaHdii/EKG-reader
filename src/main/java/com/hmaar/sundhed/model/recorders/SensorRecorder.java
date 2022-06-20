package com.hmaar.sundhed.model.recorders;

import com.hmaar.sundhed.controller.DataController;
import com.hmaar.sundhed.model.DataPublisher;
import com.hmaar.sundhed.model.implementation.EKG;
import com.fazecast.jSerialComm.*;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SensorRecorder implements Runnable {
    private DataPublisher subject;
    private BufferedReader reader;
    private PrintWriter out;

    public DataController getDataController() {
        return dc;
    }

    public void setDataController(DataController dc) {
        this.dc = dc;
    }

    public DataController dc;
    private byte[] buffer = new byte[2]; // 16 bits men kun 12 bits bruges

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
        System.out.println("Resetting serial connection.");
        dc.setStatusSensorError();
        if(serialPort.isOpen()){
            try {
                serialPort.closePort();
            }catch(SerialPortInvalidPortException ignored) {}
        }
        reader = null;
        out = null;
        setSerialPort(null);
    }

    public volatile SerialPort serialPort;

    public SerialPort[] getSerialPorts(){
        return SerialPort.getCommPorts();
    }

    public void setSubject(DataPublisher Subject) {
        this.subject = Subject;
    }


    public void sendMessage(String msg){
        if(serialPort != null && serialPort.isOpen()){
            out.println(msg);
        }
    }

    private int fetchSample(){
        if(serialPort != null & serialPort.bytesAvailable() > 1) {
            serialPort.readBytes(buffer, 2);
            return (buffer[1] << 8) + buffer[0];
        }
        return -1; // da ADC ikke kan returnere -1, så kan vi bruge den som error
    }
    private void waitForData(){
        // Optimized busy wait
        while(serialPort.bytesAvailable() > 1){
            Thread.onSpinWait();
        }
    }

    @Override
    public void run() {
        getDataController().setSensorRecorder(this);
        Platform.runLater(() ->dc.setupSensors());
        // Main infinite loop
        try{
            while (true) {
                // Setup code
                dc.setStatusSensorError();
                while (this.serialPort == null || subject == null ) {
                    Thread.onSpinWait(); // meget effektiv.
                    //Thread.sleep(500);
                    //Platform.runLater(() -> dc.setupSensors());
                    // we are waiting for the user to choose an appropriate serialPort
                }

                // Connect to the port
                try{
                    serialPort.openPort();
                    serialPort.setComPortParameters(115200, 8, 1, 0);
                    serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
                    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100,100);
                    serialPort.setDTR();
                    dc.setStatusSensorRecording();
                }catch (SerialPortInvalidPortException ex){
                    //Sensor must have disconnected in the meantime or other program is using the serial
                    ex.printStackTrace();
                    System.out.println(ex);
                    resetSerialConnection();
                }
                // Setup in and out
                //reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                //out = new PrintWriter(serialPort.getOutputStream());

                // Separate thread code
                int errors = 0; // increment each time we get an error, if this exceeds 3 times, we reset!
                while (serialPort != null && serialPort.isOpen()) {
                        //int sample = fetchSample();
                        // data er klart
                        try {
                            // nanoTime giver ikke UTC time
                            subject.setEkgData(new EKG(fetchSample(), System.currentTimeMillis(), false));
                        } catch (NumberFormatException ex) {
                            // Hvis der er opstår 3 fejl i træk, så beder vi brugeren om at vælge en ny sensor, da det kan være vi er connected forkert.
                            if(errors == 3){
                                resetSerialConnection();
                            }else{
                                errors++;
                            }
                        }
                }
                // SerialPort er blevet null eller lukket
                resetSerialConnection();
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
