package com.socialnetwork.socialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplyDialogue {
    @FXML
    public TextField receiverNameField;
    @FXML
    public TextArea message;

    /**
     * Handler for when the 'Send Request' button is pressed.
     *
     * @return Data from the fields.
     */

    public void initData(String firstName, String lastName) {
        receiverNameField.setText(firstName + " " + lastName);
    }
    public Map<String, String> handleSendMessage() {
        // Inserting the data into a map.
        Map<String, String> map = new HashMap<>();
        map.put("message",message.getText());

        // Closing the window.
        Stage stage = (Stage) this.receiverNameField.getScene().getWindow();
        stage.close();

        // Returning the values.
        return map;
    }


}
