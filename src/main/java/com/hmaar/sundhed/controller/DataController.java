package com.hmaar.sundhed.controller;
import com.hmaar.sundhed.model.*;
import com.hmaar.sundhed.model.interfaces.EKGData;
import com.hmaar.sundhed.model.interfaces.PulsData;
import com.hmaar.sundhed.model.interfaces.SpO2Data;
import com.hmaar.sundhed.model.interfaces.TempData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.SQLException;
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
        private LineChart<CategoryAxis, Number> graph1;
        private XYChart.Series<CategoryAxis, Number> pulsGraf1;
        private XYChart.Series<CategoryAxis, Number> spO2Graf1;
        private XYChart.Series<CategoryAxis, Number> tempGraf1;
        private XYChart.Series<CategoryAxis, Number> ekgGraf1;
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



        @Override
        public void initialize(URL url, ResourceBundle rb) {
                // Dummy data til grafen
                //-154.0, -11.0, -116.0, -11.0, -107.0, -24.0, -90.0, -10.0, -75.0, -108.0, -75.0, 51.0, -62.0, -24.0, 5.0, -45.0, 76.0, -10.0
                //graph = new LineChart<>(new NumberAxis(), new NumberAxis());

                // Historic
                subject = new DataPublisher();
                subject.registerObserver(this);
                subject.record();

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
                graph1.setTitle("Historisk Data");
                pulsGraf1 = new XYChart.Series<>();
                pulsGraf1.setName("Puls");

                spO2Graf1 = new XYChart.Series<>();
                spO2Graf1.setName("SpO2");

                tempGraf1 = new XYChart.Series<>();
                tempGraf1.setName("Temperatur");

                ekgGraf1 = new XYChart.Series<>();
                ekgGraf1.setName("EKG");
                //NumberAxis xAxis = (NumberAxis) graph.getXAxis();

                graph1.getData().add(pulsGraf1);
                graph1.getData().add(spO2Graf1);
                graph1.getData().add(tempGraf1);
                graph1.getData().add(ekgGraf1);
                graph.setAnimated(false);
                graph1.setAnimated(false);

                updateStaffGui();
                updatePatientLabels();
                initCol();
                loadDataToTable();
                enableEditOnTable();
        }

        public void dateChanged(){
                if(fraDate.getValue() != null & tilDate.getValue() != null){
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
                        graph1.setAnimated(false);
                        graph1.getData().clear();
                        ;

                        if (showSpO2) {
                                graph1.getData().add(spO2Graf1);
                        }

                        if (showTemp) {
                                graph1.getData().add(tempGraf1);
                        }

                        if (showEkg) {
                                graph1.getData().add(ekgGraf1);
                        }

                        if (showPuls) {
                                graph1.getData().add(pulsGraf1);
                        }
                        //graph1.setAnimated(true);
                });
        }
        public void checkForAnomalies() {

                if(pulsData != null && pulsLabel != null) {
                        if (pulsData.getPuls() < 50 || pulsData.getPuls() > 130) {
                                // kritisk
                                user.uploadWarning(patient.getId(), "Puls er kritisk: " + (double) Math.round(pulsData.getPuls() * 100) / 100 + "BPM", "", pulsData.getPuls(), this);
                                pulsLabel.setTextFill(red);
                        } else if (pulsData.getPuls() < 60 || pulsData.getPuls() > 100) {
                                // info
                                user.uploadWarning(patient.getId(), "Puls er unormal: " + (double) Math.round(pulsData.getPuls() * 100) / 100 + "BPM", "", pulsData.getPuls(), this);
                                pulsLabel.setTextFill(yellow);
                        } else if (pulsData.getPuls() > 60 || pulsData.getPuls() < 130) {
                                // ok
                                pulsLabel.setTextFill(green);
                        }
                        pulsLabel.setText((double) Math.round(pulsData.getPuls() * 100) / 100 + "BPM");
                }

                if(tempData != null && tempLabel != null) {
                        if (tempData.getTemp() < 36 || tempData.getTemp() > 39) {
                                // kritisk
                                user.uploadWarning(patient.getId(), "Temperatur er kritisk: " + (double) Math.round(tempData.getTemp() * 100) / 100 + "°C", "", tempData.getTemp(), this);
                                tempLabel.setTextFill(red);
                        } else if (tempData.getTemp() == 36 || tempData.getTemp() == 39) {
                                user.uploadWarning(patient.getId(), "Temperatur er unormal: " + (double) Math.round(tempData.getTemp() * 100) / 100 + "°C", "", tempData.getTemp(), this);
                                // info
                                tempLabel.setTextFill(yellow);
                        } else if (tempData.getTemp() == 37 || tempData.getTemp() == 39) {
                                // ok
                                tempLabel.setTextFill(green);
                        }
                        tempLabel.setText((double) Math.round(tempData.getTemp() * 100) / 100 + "°C");
                }

                if(spO2Data != null && tempLabel != null) {
                        if (spO2Data.getSpO2() < 94) {
                                // kritisk
                                user.uploadWarning(patient.getId(), "SpO2 er kritisk: " + (double) Math.round(spO2Data.getSpO2() * 100) / 100 + "%", "", spO2Data.getSpO2(), this);
                                spO2Label.setTextFill(red);
                        } else if (spO2Data.getSpO2() >= 94 || spO2Data.getSpO2() < 96) {
                                user.uploadWarning(patient.getId(), "SpO2 er unormal: " + (double) Math.round(spO2Data.getSpO2() * 100) / 100 + "%", "", spO2Data.getSpO2(),this);
                                // info
                                spO2Label.setTextFill(yellow);
                        } else if (spO2Data.getSpO2() == 100 || spO2Data.getSpO2() >= 97) {
                                // ok
                                spO2Label.setTextFill(green);
                        }
                        spO2Label.setText((double) Math.round(spO2Data.getSpO2() * 100) / 100 + "%");
                }

        }

        @Override
        public void update(EKGData ekgData, PulsData pulsData, TempData tempData, SpO2Data spO2Data) {
                // Opdatere felterne
                // Vi skal køre denne kode på UI tråden.
                this.setEkgData(ekgData);
                this.setPulsData(pulsData);
                this.setTempData(tempData);
                this.setSpO2Data(spO2Data);

                // vis kun recent data
                for(int i = 0; i < graph.getData().size(); i++){
                        if(graph.getData().get(i).getData().size() > 40){
                                int finalI = i;
                                // denne kode køres i en thread. Vi prøver at undgå en race condition her.
                                Platform.runLater(() -> {
                                        graph.setAnimated(false);
                                        graph.getData().get(finalI).getData().remove(0);
                                        //graph.setAnimated(true);
                                });
                        }
                }
                Platform.runLater(this::checkForAnomalies);
        }

        private String convertToString(long unix){
                Date date = new java.sql.Date(unix);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                return formatter.format(date);
        }

        private String convertToStringHistoric(long unix){
                Date date = new java.sql.Date(unix);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                return formatter.format(date);
        }
        public void setEkgData(EKGData ekgData) {
                // Tjek om der har været en opdatering i ekgDataet
                if(ekgData != this.ekgData & tempGraf != null && ekgData != null){
                        // Ændre værdien
                        this.ekgData = ekgData;
                        // Placere det nye data i serien.

                        Platform.runLater(() -> ekgGraf.getData().add(new XYChart.Data(convertToString(ekgData.getTime()), ekgData.getVoltage())));
                        user.uploadLog(patient.getId(), ekgData);
                }
        }

        public void setPulsData(PulsData pulsData) {
                if(pulsData != this.pulsData & pulsGraf != null && pulsData != null){
                        this.pulsData = pulsData;
                        Platform.runLater(() -> pulsGraf.getData().add(new XYChart.Data(convertToString(pulsData.getTime()), pulsData.getPuls())));
                        user.uploadLog(patient.getId(), pulsData);
                }
        }

        public void setSpO2Data(SpO2Data spO2Data) {
                if(spO2Data != this.spO2Data & spO2Graf != null && spO2Data != null){
                        this.spO2Data = spO2Data;
                        Platform.runLater(() -> spO2Graf.getData().add(new XYChart.Data(convertToString(spO2Data.getTime()),spO2Data.getSpO2())));
                        user.uploadLog(patient.getId(), spO2Data);
                }
        }

        public void setTempData(TempData tempData) {
                if(tempData != this.tempData & tempGraf != null && tempData != null){
                        this.tempData = tempData;
                        Platform.runLater(() -> tempGraf.getData().add(new XYChart.Data(convertToString(tempData.getTime()), tempData.getTemp())));
                        user.uploadLog(patient.getId(), tempData);
                }
        }


        // Historic graph
        public void setEkgDataHistoric(EKGData ekgData) {
                // Tjek om der har været en opdatering i ekgDataet
                if( tempGraf1 != null && ekgData != null){
                        Platform.runLater(() -> ekgGraf1.getData().add(new XYChart.Data(convertToStringHistoric(ekgData.getTime()), ekgData.getVoltage())));
                }
        }

        public void setPulsDataHistoric(PulsData pulsData) {
                if(pulsGraf1 != null && pulsData != null){
                        Platform.runLater(() -> pulsGraf1.getData().add(new XYChart.Data(convertToStringHistoric(pulsData.getTime()), pulsData.getPuls())));
                }
        }

        public void setSpO2DataHistoric(SpO2Data spO2Data) {
                if(spO2Graf1 != null && spO2Data != null){
                        Platform.runLater(() -> spO2Graf1.getData().add(new XYChart.Data(convertToStringHistoric(spO2Data.getTime()),spO2Data.getSpO2())));
                }
        }

        public void setTempDataHistoric(TempData tempData) {
                if(tempGraf1 != null && tempData != null){
                        Platform.runLater(() -> tempGraf1.getData().add(new XYChart.Data(convertToStringHistoric(tempData.getTime()), tempData.getTemp())));
                }
        }
}
