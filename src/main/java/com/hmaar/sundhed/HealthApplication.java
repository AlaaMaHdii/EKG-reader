package com.hmaar.sundhed;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.IOException;

public class HealthApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HealthApplication.class.getResource("app-view.fxml")); // skift til login-view senere hen
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Vital");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}