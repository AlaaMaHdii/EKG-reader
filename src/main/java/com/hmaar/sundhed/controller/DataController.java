package com.hmaar.sundhed.controller;
import com.hmaar.sundhed.model.*;
import com.hmaar.sundhed.model.interfaces.EKGData;
import com.hmaar.sundhed.model.interfaces.PulsData;
import com.hmaar.sundhed.model.interfaces.SpO2Data;
import com.hmaar.sundhed.model.interfaces.TempData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;


public class DataController implements Initializable, Observer {
        @FXML
        private LineChart<Number, Number> graph;
        private XYChart.Series<Number, Number> pulsGraf;
        private XYChart.Series<Number, Number> spO2Graf;
        private XYChart.Series<Number, Number> tempGraf;
        private XYChart.Series<Number, Number> ekgGraf;
        private DataPublisher subject;

        // Den seneste data skal gemmes i disse variabler
        private EKGData ekgData;
        private PulsData pulsData;
        private SpO2Data spO2Data;
        private TempData tempData;


        @Override
        public void initialize(URL url, ResourceBundle rb) {
                // Dummy data til grafen
                //-154.0, -11.0, -116.0, -11.0, -107.0, -24.0, -90.0, -10.0, -75.0, -108.0, -75.0, 51.0, -62.0, -24.0, 5.0, -45.0, 76.0, -10.0
                //graph = new LineChart<>(new NumberAxis(), new NumberAxis());
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

                graph.getData().add(pulsGraf);
                graph.getData().add(spO2Graf);
                graph.getData().add(tempGraf);
                graph.getData().add(ekgGraf);

                /* Dummy data

                pulsGraf.getData().add(new XYChart.Data<>( 1, 100));
                pulsGraf.getData().add(new XYChart.Data<>( 2, 105));
                pulsGraf.getData().add(new XYChart.Data<>( 3, 103));
                pulsGraf.getData().add(new XYChart.Data<>( 4, 104));
                pulsGraf.getData().add(new XYChart.Data<>( 5, 107));

                 */

        }


        @Override
        public void update(EKGData ekgData, PulsData pulsData, TempData tempData, SpO2Data spO2Data) {
                // Opdatere felterne
                // Vi skal køre denne kode på UI tråden.
                this.setEkgData(ekgData);
                this.setPulsData(pulsData);
                this.setTempData(tempData);
                this.setSpO2Data(spO2Data);
        }

        public void setEkgData(EKGData ekgData) {
                // Tjek om der har været en opdatering i ekgDataet
                if(ekgData != this.ekgData & tempGraf != null){
                        // Ændre værdien
                        this.ekgData = ekgData;
                        // Placere det nye data i serien.

                        Platform.runLater(() -> ekgGraf.getData().add(new XYChart.Data<>( ekgData.getTime(),ekgData.getVoltage())));
                }
        }

        public void setPulsData(PulsData pulsData) {
                if(pulsData != this.pulsData & pulsGraf != null){
                        this.pulsData = pulsData;
                        Platform.runLater(() -> pulsGraf.getData().add(new XYChart.Data<>(pulsData.getTime(), pulsData.getPuls())));
                }
        }

        public void setSpO2Data(SpO2Data spO2Data) {
                if(spO2Data != this.spO2Data & spO2Graf != null){
                        this.spO2Data = spO2Data;
                        Platform.runLater(() -> spO2Graf.getData().add(new XYChart.Data<>( spO2Data.getTime(),spO2Data.getSpO2())));
                }
        }

        public void setTempData(TempData tempData) {
                if(tempData != this.tempData & tempGraf != null){
                        this.tempData = tempData;
                        Platform.runLater(() -> tempGraf.getData().add(new XYChart.Data<>( tempData.getTime(), tempData.getTemp())));
                }
        }
}
