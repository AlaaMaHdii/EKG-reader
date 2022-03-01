package com.hmaar.sundhed;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;


public class DataController implements Initializable {
        @FXML
        public LineChart<Number, Number> graph;


        @Override
        public void initialize(URL url, ResourceBundle rb) {
                // Dummy data til grafen
                //-154.0, -11.0, -116.0, -11.0, -107.0, -24.0, -90.0, -10.0, -75.0, -108.0, -75.0, 51.0, -62.0, -24.0, 5.0, -45.0, 76.0, -10.0
                //graph = new LineChart<>(new NumberAxis(), new NumberAxis());
                graph.setTitle("Dummy data med BPM");
                XYChart.Series<Number, Number> dummyData = new XYChart.Series<Number, Number>();
                dummyData.setName("BPM");
                dummyData.getData().add(new XYChart.Data<>( 1, 100));
                dummyData.getData().add(new XYChart.Data<>( 2, 105));
                dummyData.getData().add(new XYChart.Data<>( 3, 103));
                dummyData.getData().add(new XYChart.Data<>( 4, 104));
                dummyData.getData().add(new XYChart.Data<>( 5, 107));
                graph.getData().add(dummyData);

        }
}
