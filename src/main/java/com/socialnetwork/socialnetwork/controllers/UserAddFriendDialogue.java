package com.socialnetwork.socialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class UserAddFriendDialogue {
    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;

    private boolean cancelled = false;

    /**
     * Handler for when the 'Send Request' button is pressed.
     *
     * @return Data from the fields.
     */
    public Map<String, String> handleSendFriendRequest() {
        // Retrieving data from the fields.
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        // Inserting the data into a map.
        Map<String, String> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);

        // Closing the window.
        Stage stage = (Stage) this.firstNameField.getScene().getWindow();
        stage.close();

        // Returning the values.
        return map;
    }


    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Handler for the pressing of the cancel button.
     */
    public void handleCancel() {
        // Updating the indicator.
        this.cancelled = true;

        // Closing the dialogue.
        Stage stage = (Stage) this.firstNameField.getScene().getWindow();
        stage.close();
    }
}
