package com.socialnetwork.socialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendMessageDialogue {
    @FXML
    public TextField senderFullNameField;
    @FXML
    public ComboBox senderFriends;
    @FXML
    public TextArea message;
    private boolean cancelled = false;

    /**
     * Handler for when the 'Send Request' button is pressed.
     *
     * @return Data from the fields.
     */

    public void initData(String firstName, String lastName, List<String> friends) {
        senderFullNameField.setText(firstName + " " + lastName);
        senderFriends.getItems().addAll(friends);
    }
    public Map<String, String> handleSendMessage() {
        // Retrieving data from the fields.
        String friendsName=senderFriends.getValue().toString();
        // Inserting the data into a map.
        Map<String, String> map = new HashMap<>();
        String[] parts = friendsName.split(" ");

        map.put("firstName", parts[0]);
        map.put("lastName", parts[1]);
        map.put("message",message.getText());
        // Closing the window.
        Stage stage = (Stage) this.senderFriends.getScene().getWindow();
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
        Stage stage = (Stage) this.senderFriends.getScene().getWindow();
        stage.close();
    }
}
