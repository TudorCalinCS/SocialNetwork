package com.socialnetwork.socialnetwork.controllers;

import com.socialnetwork.socialnetwork.StartApplication;
import com.socialnetwork.socialnetwork.domain.Utilizator;
import com.socialnetwork.socialnetwork.service.ServiceFX;
import com.socialnetwork.socialnetwork.utility.observer.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

/**
 * Dialogue for starting window.
 */
public class LoginController {
    @FXML
    private TextField fullNameField;
    @FXML
    private PasswordField passwordField;

    private ServiceFX service;

    public void setService(ServiceFX service) {
        this.service = service;
    }

    @FXML
    public void handleLogin() throws IOException {
        String fullName = fullNameField.getText();
        String[] res = fullName.split("-");
        String password = passwordField.getText();
        //String decryptedPassword=
        Utilizator user = service.existUser(res[0], res[1]);
        //User exists
        if (user != null) {
            if (BCrypt.checkpw(password, user.getPassword())) {
                FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/socialnetwork/socialnetwork/views/main-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                AdminController adminController = fxmlLoader.getController();
                adminController.setData(service, user);

                Stage stage = new Stage();
                stage.setTitle("Social Network");
                stage.setScene(scene);
                stage.show();
                Stage stagee = (Stage) fullNameField.getScene().getWindow();
                stagee.close();
            }
        }
        //User not found
        else {
            MessageAlert.showErrorMessage(null, "User not found");
        }
    }

    @FXML
    public void handleCreate() throws IOException {
        // Loading the scene for showing.
        FXMLLoader userAddFXMLLoader = new FXMLLoader(StartApplication.class.getResource("/com/socialnetwork/socialnetwork/views/user-add-dialogue.fxml"));
        Scene userAddScene = new Scene(userAddFXMLLoader.load());

        // Retrieving the controller of the dialogue.
        UserAddDialogue userAddDialogue = userAddFXMLLoader.getController();
        // Preparing the stage for showing.
        Stage userAddStage = new Stage();
        userAddStage.setScene(userAddScene);
        //Showing and waiting for execution.
        userAddStage.showAndWait();
        if (userAddDialogue.isCancelled()) {
            return;
        }

        // Retrieving data from the dialogue.
        Map<String, String> values = userAddDialogue.handleAdd();
        String firstName = values.get("firstName");
        String lastName = values.get("lastName");
        String password = values.get("password");


        this.service.addUser(firstName, lastName, password);
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", "Account created");

    }
}