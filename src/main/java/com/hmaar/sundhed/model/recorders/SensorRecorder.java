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
        System.out.println("Resetting serial connection.");
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

    public SerialPort[] getSerialPorts(){
        return SerialPort.getCommPorts();
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
        if(serialPort != null && serialPort.isOpen()){
            out.println(msg);
        }
    }

    private int fetchSample(){
        serialPort.readBytes(buffer, 2);
        return (buffer[1] << 8) + buffer[0];
    }

    @Override
    public void run() {
        getDataController().setSensorRecorder(this);
        Platform.runLater(() ->dc.setupSensors());
        // Main infinite loop
        while (true) {
            // Setup code
            while (this.serialPort == null || subject == null ) {
                Thread.onSpinWait();
                // we are waiting for the user to choose an appropriate serialPort
            }

            // Connect to the port
            try{
                serialPort.openPort();
                serialPort.setComPortParameters(115200, 8, 1, 0);
                serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100,100);
                serialPort.setDTR();
            }catch (SerialPortInvalidPortException ex){
                //Sensor must have disconnected in the meantime
                resetSerialConnection();
            }
            // Setup in and out
            reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            out = new PrintWriter(serialPort.getOutputStream());

            // Separate thread code
            int errors = 0; // increment each time we get an error, if this exceeds 3 times, we reset!
            while (serialPort != null && serialPort.isOpen()) {
                    if(serialPort.bytesAvailable() > 0){ // skal være 1 i bytes
                        //int sample = fetchSample();
                        // data er klart
                        String sampleString = null;
                        int sample = 0;
                        try {
                            // Parse
                            sampleString = reader.readLine();
                            sample = Integer.parseInt(sampleString);
                        } catch (NumberFormatException | IOException ex) {
                            // Hvis der er opstår 3 fejl i træk, så beder vi brugeren om at vælge en ny sensor, da det kan være vi er connected forkert.
                            if(errors == 3){
                                resetSerialConnection();
                            }else{
                                errors++;
                            }
                        }
                        if (sampleString != null && sample != 0){
                            // Notify consumers.
                            subject.setEkgData(new EKG(sample, System.currentTimeMillis(), false));
                            // Reset antal fejl.
                            errors = 0;
                        }
                    }
            }
            resetSerialConnection();
        }
    }
}
