package com.hmaar.sundhed.controller;
import com.fazecast.jSerialComm.SerialPort;
import com.hmaar.sundhed.model.*;
import com.hmaar.sundhed.model.implementation.EKG;
import com.hmaar.sundhed.model.interfaces.*;
import com.hmaar.sundhed.model.recorders.SensorRecorder;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;


public class DataController implements Initializable, Observer {
        // Realtime data
        @FXML
        private LineChart<CategoryAxis, Number> graph;


        private XYChart.Series<CategoryAxis, Number> pulsGraf;
        private XYChart.Series<CategoryAxis, Number> spO2Graf;
        private XYChart.Series<CategoryAxis, Number> tempGraf;
        private XYChart.Series<CategoryAxis, Number> ekgGraf;
        private DataPublisher subject;

        // Historisk data
        @FXML
        private LineChart<CategoryAxis, Number> graphHistoric;
        private XYChart.Series<CategoryAxis, Number> pulsGrafHistoric;
        private XYChart.Series<CategoryAxis, Number> spO2GrafHistoric;
        private XYChart.Series<CategoryAxis, Number> tempGrafHistoric;
        private XYChart.Series<CategoryAxis, Number> ekgGrafHistoric;
        private DataPublisher subject1;
        @FXML
        private Label loggedInLabel;
        @FXML
        private Label patientNavnLabel;
        @FXML
        private Label patientAlderLabel;
        @FXML
        private Label patientCprLabel;

        @FXML
        private DatePicker tilDate;

        @FXML
        private DatePicker fraDate;


        // status bar
        @FXML
        private Label pulsLabel;
        @FXML
        private Label spO2Label;
        @FXML
        private Label tempLabel;
        @FXML
        private Label statusLabel;

        private final Color red = Color.rgb(234,11,11);
        private final Color yellow = Color.rgb(220,189,63);
        private final Color green = Color.rgb(26,183,54);

        // graf
        @FXML
        private CheckBox spO2Button;
        @FXML
        private CheckBox tempButton;
        @FXML
        private CheckBox ekgButton;
        @FXML
        private CheckBox pulsButton;
        @FXML
        private CheckBox spO2Button1;
        @FXML
        private CheckBox tempButton1;
        @FXML
        private CheckBox ekgButton1;
        @FXML
        private CheckBox pulsButton1;
        @FXML
        public MenuBar menuBar;

        @FXML
        private MenuItem reloadMenuItem;

        @FXML
        private Menu sensorMenu;

        @FXML
        private Menu connectSubMenu;

        @FXML
        private MenuItem disconnectMenuItem;

        // tabel
        public static TableView<Comments> TableInfo;
        public static ObservableList<Comments> dataTable;
        @FXML
        private TableView<Comments> logTabel;
        @FXML
        private TableColumn<Comments, String> colTime, colType, colComment;
        @FXML
        private TableColumn<Comments, Button> colUpdate;

        // Den seneste data skal gemmes i disse variabler
        private EKGData ekgData;
        private PulsData pulsData;
        private SpO2Data spO2Data;
        private TempData tempData;

        public AuthenticatedUser user;
        public Patient patient;

        public Database db;
        private EkgConsumer ekgConsumer;
        private Thread ekgConsumerThread;
        public SensorRecorder sensorRecorder;


        @Override
        public void initialize(URL url, ResourceBundle rb) {
                // Dummy data til grafen
                //-154.0, -11.0, -116.0, -11.0, -107.0, -24.0, -90.0, -10.0, -75.0, -108.0, -75.0, 51.0, -62.0, -24.0, 5.0, -45.0, 76.0, -10.0
                //graph = new LineChart<>(new NumberAxis(), new NumberAxis());

                // Realtime
                subject = new DataPublisher(this);
                subject.registerObserver(this);

                // start producers
                subject.record();

                // start consumer
                ekgConsumer = new EkgConsumer(this);
                ekgConsumerThread = new Thread(ekgConsumer);
                ekgConsumerThread.start();


                graph.setTitle("Realtime Data");
                pulsGraf = new XYChart.Series<>();
                pulsGraf.setName("Puls");

                spO2Graf = new XYChart.Series<>();
                spO2Graf.setName("SpO2");

                tempGraf = new XYChart.Series<>();
                tempGraf.setName("Temperatur");

                ekgGraf = new XYChart.Series<>();
                ekgGraf.setName("EKG");
                //NumberAxis xAxis = (NumberAxis) graph.getXAxis();

                graph.getData().add(pulsGraf);
                graph.getData().add(spO2Graf);
                graph.getData().add(tempGraf);
                graph.getData().add(ekgGraf);

                // Historic
                graphHistoric.setTitle("Historisk Data");
                pulsGrafHistoric = new XYChart.Series<>();
                pulsGrafHistoric.setName("Puls");

                spO2GrafHistoric = new XYChart.Series<>();
                spO2GrafHistoric.setName("SpO2");

                tempGrafHistoric = new XYChart.Series<>();
                tempGrafHistoric.setName("Temperatur");

                ekgGrafHistoric = new XYChart.Series<>();
                ekgGrafHistoric.setName("EKG");
                //NumberAxis xAxis = (NumberAxis) graph.getXAxis();

                graphHistoric.getData().add(pulsGrafHistoric);
                graphHistoric.getData().add(spO2GrafHistoric);
                graphHistoric.getData().add(tempGrafHistoric);
                graphHistoric.getData().add(ekgGrafHistoric);

                graph.setAnimated(false);
                graphHistoric.setAnimated(false);


                // Setup GUI
                updateStaffGui();
                updatePatientLabels();
                initCol();
                loadDataToTable();
                enableEditOnTable();
        }





        public void dateChanged(){
                if(fraDate.getValue() != null && tilDate.getValue() != null){
                        Date dateStart = Date.from(Instant.from(fraDate.getValue().atStartOfDay(ZoneId.systemDefault())));
                        Date dateEnd = Date.from(Instant.from(tilDate.getValue().atStartOfDay(ZoneId.systemDefault())));
                        ArrayList<Log> logs = patient.getLogs(dateStart, dateEnd);
                        for (Log log: logs) {
                                switch (log.getType()) {
                                        case "Puls" -> setPulsDataHistoric(log.generatePuls());
                                        case "EKG" -> setEkgDataHistoric(log.generateEkg());
                                        case "Temp" -> setTempDataHistoric(log.generateTemp());
                                        case "SpO2" -> setSpO2DataHistoric(log.generateSpO2());
                                        default -> throw new IllegalArgumentException("Unkonwn type: " + log.getType());
                                }
                        }
                }

        }

        private void initCol(){
                colType.setCellValueFactory(new PropertyValueFactory<>("warning"));
                colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
                colTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        }

        public void loadDataToTable(){
                ArrayList<Comments> warnings = patient.getWarnings();
                logTabel.getItems().clear();
                logTabel.getItems().addAll(warnings);
        }

        private void enableEditOnTable(){
                colComment.setCellFactory(TextFieldTableCell.forTableColumn());
                colComment.setOnEditCommit(e->{
                        String newComment = e.getNewValue();
                        Comments comment = e.getTableView().getItems().get(e.getTablePosition().getRow());
                        comment.setComment(newComment);
                });

                logTabel.setEditable(true);
        }

        public void updateStaffGui(){
                loggedInLabel.setText("Du er nu logget ind som " + user.getFullName() + " (" + user.getRole() + ")");
        }

        public void updatePatientLabels(){
                this.patientNavnLabel.setText(patient.getFullName());
                this.patientAlderLabel.setText(patient.getAge() + " år");
                this.patientCprLabel.setText(patient.getCprFormatted());

        }

        public void setStatusSensorError(){
                Platform.runLater(() -> {
                        statusLabel.setText("Forbundet til Vital databasen. Sensor er ikke forbundet.");
                        disconnectMenuItem.setDisable(true);
                        connectSubMenu.setDisable(false);
                        setupSensors();
                });
        }

        public void setStatusSensorRecording(){
                Platform.runLater(() -> {
                        statusLabel.setText("Forbundet til Vital databasen. Sensor opsamler korrekt.");
                        disconnectMenuItem.setDisable(false);
                        connectSubMenu.setDisable(true);

                });
        }


        public void disconnectFromSensor(){
                if(sensorRecorder != null){
                        sensorRecorder.resetSerialConnection();
                }
        }

        public void setupSensors(){
                SerialPort SerialPorts[] = SerialPort.getCommPorts();
                connectSubMenu.getItems().clear(); // clear all
                for (SerialPort serialPort: SerialPorts) {
                        // for hver serialport fundet
                        String portName = serialPort.getSystemPortName();
                        MenuItem menuItem = new MenuItem(portName);
                        menuItem.setOnAction(t -> sensorRecorder.setSerialPort(serialPort));
                        connectSubMenu.getItems().add(menuItem);
                }
        }

        public void toggleSeriesRealTime(){
                boolean showSpO2 = spO2Button.isSelected();
                boolean showTemp = tempButton.isSelected();
                boolean showEkg = ekgButton.isSelected();
                boolean showPuls = pulsButton.isSelected();
                Platform.runLater(() -> {


                        // Lang historie kort, der sker en race condition, da animation foregår asynkront https://bugs.openjdk.java.net/browse/JDK-8125967
                        graph.setAnimated(false);
                        graph.getData().clear();
;

                        if (showSpO2) {
                                graph.getData().add(spO2Graf);
                        }

                        if (showTemp) {
                                graph.getData().add(tempGraf);
                        }

                        if (showEkg) {
                                graph.getData().add(ekgGraf);
                        }

                        if (showPuls) {
                                graph.getData().add(pulsGraf);
                        }
                        //graph.setAnimated(true);
                });
        }

        public void toggleSeriesHistoric(){
                boolean showSpO2 = spO2Button1.isSelected();
                boolean showTemp = tempButton1.isSelected();
                boolean showEkg = ekgButton1.isSelected();
                boolean showPuls = pulsButton1.isSelected();
                Platform.runLater(() -> {


                        // Lang historie kort, der sker en race condition, da animation foregår asynkront https://bugs.openjdk.java.net/browse/JDK-8125967
                        graphHistoric.setAnimated(false);
                        graphHistoric.getData().clear();


                        if (showSpO2) {
                                graphHistoric.getData().add(spO2GrafHistoric);
                        }

                        if (showTemp) {
                                graphHistoric.getData().add(tempGrafHistoric);
                        }

                        if (showEkg) {
                                graphHistoric.getData().add(ekgGrafHistoric);
                        }

                        if (showPuls) {
                                graphHistoric.getData().add(pulsGrafHistoric);
                        }
                        //graph1.setAnimated(true);
                });
        }
        public void checkForAnomalies() {

                if(pulsData != null && pulsLabel != null) {
                        if (pulsData.getPuls() < 50 || pulsData.getPuls() > 130) {
                                // kritisk
                                user.uploadWarning(patient.getId(), "Puls er kritisk: " + (double) Math.round(pulsData.getPuls() * 100) / 100 + "BPM", "", pulsData.getPuls(), pulsData.getTime(), this);
                                Platform.runLater(() -> pulsLabel.setTextFill(red));
                        } else if (pulsData.getPuls() < 60 || pulsData.getPuls() > 100) {
                                // info
                                user.uploadWarning(patient.getId(), "Puls er unormal: " + (double) Math.round(pulsData.getPuls() * 100) / 100 + "BPM", "", pulsData.getPuls(),pulsData.getTime(), this);
                                Platform.runLater(() -> pulsLabel.setTextFill(yellow));
                        } else if (pulsData.getPuls() > 60 || pulsData.getPuls() < 130) {
                                // ok
                                Platform.runLater(() -> pulsLabel.setTextFill(green));
                        }
                        Platform.runLater(() -> pulsLabel.setText((double) Math.round(pulsData.getPuls() * 100) / 100 + "BPM"));
                }

                if(tempData != null && tempLabel != null) {
                        if (tempData.getTemp() < 36 || tempData.getTemp() > 39) {
                                // kritisk
                                user.uploadWarning(patient.getId(),"Temperatur er kritisk: " + (double) Math.round(tempData.getTemp() * 100) / 100 + "°C", "", tempData.getTemp(), tempData.getTime(), this);
                                Platform.runLater(() -> tempLabel.setTextFill(red));
                        } else if (tempData.getTemp() == 36 || tempData.getTemp() == 39) {
                                user.uploadWarning(patient.getId(), "Temperatur er unormal: " + (double) Math.round(tempData.getTemp() * 100) / 100 + "°C", "", tempData.getTemp(),tempData.getTime(), this);
                                // info
                                Platform.runLater(() -> tempLabel.setTextFill(yellow));
                        } else if (tempData.getTemp() == 37 || tempData.getTemp() == 39) {
                                // ok
                                Platform.runLater(() -> tempLabel.setTextFill(green));
                        }
                        Platform.runLater(() -> tempLabel.setText((double) Math.round(tempData.getTemp() * 100) / 100 + "°C"));
                }

                if(spO2Data != null && tempLabel != null) {
                        if (spO2Data.getSpO2() < 94) {
                                // kritisk
                                user.uploadWarning(patient.getId(), "SpO2 er kritisk: " + (double) Math.round(spO2Data.getSpO2() * 100) / 100 + "%", "", spO2Data.getSpO2(), spO2Data.getTime(),this);
                                Platform.runLater(() -> spO2Label.setTextFill(red));
                        } else if (spO2Data.getSpO2() >= 94 || spO2Data.getSpO2() < 96) {
                                user.uploadWarning(patient.getId(), "SpO2 er unormal: " + (double) Math.round(spO2Data.getSpO2() * 100) / 100 + "%", "", spO2Data.getSpO2(),spO2Data.getTime(),this);
                                // info
                                Platform.runLater(() -> spO2Label.setTextFill(yellow));
                        } else if (spO2Data.getSpO2() == 100 || spO2Data.getSpO2() >= 97) {
                                // ok
                                Platform.runLater(() -> spO2Label.setTextFill(green));
                        }
                        Platform.runLater(() -> spO2Label.setText((double) Math.round(spO2Data.getSpO2() * 100) / 100 + "%"));
                }

        }

        @Override
        public void update(EKGData ekgData, PulsData pulsData, TempData tempData, SpO2Data spO2Data) {
                ekgConsumer.enqueue(ekgData);
                ekgConsumer.notifyOnEmpty();
                /*
                // Opdatere felterne
                // Vi skal køre denne kode på UI tråden.
                 cleanUpGraphs();
                */
        }

        public void cleanUpGraphs(){
                for(int i = 0; i < graph.getData().size(); i++){
                        int finalI = i;
                        if(graph.getData().get(finalI).getData().size() > 2000) {
                                // denne kode køres i en thread. Vi prøver at undgå en race condition her.
                                //graph.setAnimated(false);
                                Platform.runLater(() -> graph.getData().get(finalI).getData().remove(0));
                                //graph.setAnimated(true);
                        }
                }
        }

        private String convertToString(long unix){
                Date date = new java.sql.Date(unix);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
                return formatter.format(date);
        }

        private String convertToStringHistoric(long unix){
                Date date = new java.sql.Date(unix);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                return formatter.format(date);
        }
        public void setEkgData(EKGData ekgData) {
                // Tjek om der har været en opdatering i ekgDataet
                if(ekgData != this.ekgData && ekgGraf != null && ekgData != null){
                        // Ændre værdien
                        this.ekgData = ekgData;
                        // Placere det nye data i serien.

                        Platform.runLater(() -> {
                                XYChart.Data data = new XYChart.Data(convertToString(ekgData.getTime()), ekgData.getVoltage());
                                ekgGraf.getData().add(data);


                        });
                        user.uploadLog(patient.getId(), (SQLData) ekgData);
                }
        }


        public void setEkgData(List<EKG> ekgData) {
                // Tjek om der har været en opdatering i ekgDataet
                for (EKGData ekg: ekgData) {
                        if(ekg != this.ekgData && ekgGraf != null && ekg != null){
                                // Ændre værdien
                                this.ekgData = ekg;
                                // Placere det nye data i serien.
                                Platform.runLater(() -> ekgGraf.getData().add(new XYChart.Data(convertToString(ekg.getTime()), ekg.getVoltage())));
                                cleanUpGraphs();
                        }
                }
                List<SQLData> sqlDatas = (List<SQLData>)(List<?>) ekgData; // ekgdata til sqldata
                user.uploadLog(patient.getId(), sqlDatas);
        }

        public void setPulsData(PulsData pulsData) {
                if(pulsData != this.pulsData && pulsGraf != null && pulsData != null){
                        this.pulsData = pulsData;
                        Platform.runLater(() -> pulsGraf.getData().add(new XYChart.Data(convertToString(pulsData.getTime()), pulsData.getPuls())));
                        user.uploadLog(patient.getId(), (SQLData) pulsData);
                }
                checkForAnomalies();
        }

        public void setSpO2Data(SpO2Data spO2Data) {
                if(spO2Data != this.spO2Data && spO2Graf != null && spO2Data != null){
                        this.spO2Data = spO2Data;
                        Platform.runLater(() -> spO2Graf.getData().add(new XYChart.Data(convertToString(spO2Data.getTime()),spO2Data.getSpO2())));
                        user.uploadLog(patient.getId(), (SQLData) spO2Data);
                }
                checkForAnomalies();
        }

        public void setTempData(TempData tempData) {
                if(tempData != this.tempData && tempGraf != null && tempData != null){
                        this.tempData = tempData;
                        Platform.runLater(() -> tempGraf.getData().add(new XYChart.Data(convertToString(tempData.getTime()), tempData.getTemp())));
                        user.uploadLog(patient.getId(), (SQLData) tempData);
                }
                checkForAnomalies();
        }


        // Historic graph
        public void setEkgDataHistoric(EKGData ekgData) {
                // Tjek om der har været en opdatering i ekgDataet
                if( tempGrafHistoric != null && ekgData != null){
                        Platform.runLater(() -> ekgGrafHistoric.getData().add(new XYChart.Data(convertToStringHistoric(ekgData.getTime()), ekgData.getVoltage())));
                }
        }

        public void setPulsDataHistoric(PulsData pulsData) {
                if(pulsGrafHistoric != null && pulsData != null){
                        Platform.runLater(() -> pulsGrafHistoric.getData().add(new XYChart.Data(convertToStringHistoric(pulsData.getTime()), pulsData.getPuls())));
                }
        }

        public void setSpO2DataHistoric(SpO2Data spO2Data) {
                if(spO2GrafHistoric != null && spO2Data != null){
                        Platform.runLater(() -> spO2GrafHistoric.getData().add(new XYChart.Data(convertToStringHistoric(spO2Data.getTime()),spO2Data.getSpO2())));
                }
        }

        public void setTempDataHistoric(TempData tempData) {
                if(tempGrafHistoric != null && tempData != null){
                        Platform.runLater(() -> tempGrafHistoric.getData().add(new XYChart.Data(convertToStringHistoric(tempData.getTime()), tempData.getTemp())));
                }
        }

        public void setSensorRecorder(SensorRecorder sensorRecorder){
                this.sensorRecorder = sensorRecorder;
        }
        public SensorRecorder getSensorRecorder(){
                return sensorRecorder;
        }
}
