package com.hmaar.sundhed.controller;

import com.hmaar.sundhed.HealthApplicationMain;
import com.hmaar.sundhed.model.AuthenticatedUser;
import com.hmaar.sundhed.model.Database;
import com.hmaar.sundhed.model.Patient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class LoginController implements Initializable {

    // Staff fields
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginAsStaffButton;
    @FXML
    private TextField cprField;
    @FXML
    private TextField cprPatientField;

    // Patient fields
    @FXML
    private Label errorLabelPatient;
    @FXML
    private Button loginAsPatientButton;
    @FXML
    private TextField cprFieldPatientLogin;


    private Database db = new Database();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db.connectToDb();
    }

    @FXML
    private void onLoginButtonClick(javafx.event.ActionEvent event) throws IOException, SQLException {
        String cprNummerParsed = cprField.getText().replace("-","");
        String cprNummerPatientParsed = cprPatientField.getText().replace("-","");
        System.out.println("CPR-nummer: " + cprNummerParsed);
        AuthenticatedUser user = db.loginAsStaff(cprNummerParsed);
        Patient patient = db.retreivePatient(cprNummerPatientParsed);
        if(user == null || patient == null){
            errorLabel.setOpacity(1);
        }else{
            // login er valid
            System.out.println(user.fullName);
            changeSceneStaff(event, user, patient);
        }
    }

    @FXML
    private void changeSceneStaff(javafx.event.ActionEvent event, AuthenticatedUser user, Patient patient) throws IOException {

        // load fxml
        FXMLLoader loader = new FXMLLoader(HealthApplicationMain.class.getResource("app-view.fxml"));
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        // opstil controlleren
        DataController controller = new DataController();
        controller.user = user;
        controller.patient = patient;
        controller.db = db;

        // opstil view
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}