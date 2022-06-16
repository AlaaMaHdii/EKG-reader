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
        launch();
    }

    @Override
    public void stop() {
        // Programmet lukker
        System.exit(0);
    }



}