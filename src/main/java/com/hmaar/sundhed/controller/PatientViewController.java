package com.hmaar.sundhed.controller;
import com.hmaar.sundhed.model.AuthenticatedUser;
import com.hmaar.sundhed.model.Comments;
import com.hmaar.sundhed.model.Log;
import com.hmaar.sundhed.model.Patient;
import com.hmaar.sundhed.model.interfaces.EKGData;
import com.hmaar.sundhed.model.interfaces.PulsData;
import com.hmaar.sundhed.model.interfaces.SpO2Data;
import com.hmaar.sundhed.model.interfaces.TempData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class PatientViewController implements Initializable {

    @FXML
    private TableColumn<Comments, String> colTime, colType, colComment;
    @FXML
    private TableColumn<Comments, Button> colUpdate;

    @FXML
    private CheckBox ekgButton1;

    @FXML
    private DatePicker fraDate;

    @FXML
    private TableView<Comments> logTabel;

    @FXML
    private Label patientAlderLabel;

    @FXML
    private Label patientCprLabel;

    @FXML
    private Label patientNavnLabel;

    @FXML
    private CheckBox pulsButton1;

    @FXML
    private CheckBox spO2Button1;

    @FXML
    private CheckBox tempButton1;

    @FXML
    private DatePicker tilDate;
    @FXML
    private LineChart<CategoryAxis, Number> graph1;
    private XYChart.Series<CategoryAxis, Number> pulsGraf1;
    private XYChart.Series<CategoryAxis, Number> spO2Graf1;
    private XYChart.Series<CategoryAxis, Number> tempGraf1;
    private XYChart.Series<CategoryAxis, Number> ekgGraf1;

    public AuthenticatedUser user;
    public Patient patient;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        graph1.setAnimated(false);

        updatePatientLabels();
        initCol();
        loadDataToTable();
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

    public void updatePatientLabels(){
        this.patientNavnLabel.setText(patient.getFullName());
        this.patientAlderLabel.setText(patient.getAge() + " år");
        this.patientCprLabel.setText(patient.getCprFormatted());

    }
    @FXML
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
    @FXML
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
    private String convertToStringHistoric(long unix){
        Date date = new java.sql.Date(unix);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(date);
    }
}
