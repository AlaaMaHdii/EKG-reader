package com.hmaar.sundhed;
import java.sql.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HealthApplicationMain extends Application {


    public static void run(){
            launch();
    }

    // Bruges ikke
    @Override
    public  void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HealthApplicationMain.class.getResource("login-view.fxml")); // skift til login-view senere hen
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Vital");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Programmet lukker
        System.exit(0);
    }



}