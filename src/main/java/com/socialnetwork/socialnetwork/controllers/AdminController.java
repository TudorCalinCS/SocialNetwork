package com.socialnetwork.socialnetwork.controllers;

import com.socialnetwork.socialnetwork.StartApplication;
import com.socialnetwork.socialnetwork.domain.FriendshipStatus;
import com.socialnetwork.socialnetwork.domain.Message;
import com.socialnetwork.socialnetwork.domain.Prietenie;
import com.socialnetwork.socialnetwork.repository.Page;
import com.socialnetwork.socialnetwork.repository.Pageable;
import com.socialnetwork.socialnetwork.utility.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.socialnetwork.socialnetwork.domain.Utilizator;
import com.socialnetwork.socialnetwork.service.ServiceFX;
import com.socialnetwork.socialnetwork.events.SocialNetworkEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Controller for the actual social network interface.
 */
public class AdminController implements Observer<SocialNetworkEvent> {
    @FXML
    public TableView<Utilizator> userTableView;
    @FXML
    public TableColumn<Utilizator, UUID> userID;
    @FXML
    public TableColumn<Utilizator, String> userFirstName;
    @FXML
    public TableColumn<Utilizator, String> userLastName;
    @FXML
    public TableView<Prietenie> friendsTableView;
    @FXML
    public TableColumn<Prietenie, UUID> friendshipID;
    @FXML
    public TableColumn<Prietenie, String> friendFirstName;
    @FXML
    public TableColumn<Prietenie, String> friendLastName;
    @FXML
    public TableColumn<Prietenie, LocalDateTime> friendshipDate;
    @FXML
    public TableColumn<Prietenie, String> friendshipStatus;
    @FXML
    public TableView<Message> messageTableView;
    @FXML
    public TableColumn<Message, UUID> messageID;
    @FXML
    public TableColumn<Message, String> senderName;
    @FXML
    public TableColumn<Message, String> messageText;
    @FXML
    Button nextButton;
    @FXML
    Button previousButton;
    @FXML
    public Button confirmFriendshipButton;
    @FXML
    public Button declineFriendshipButton;
    @FXML
    public TextField pageSizeField;
    private ServiceFX service;
    private int currentPage = 0;
    private int pageSize = 5;
    private int totalNumberOfElements = 0;

    private Utilizator user;
    ObservableList<Utilizator> userModel = FXCollections.observableArrayList();

    public void setData(ServiceFX service, Utilizator user) {
        this.service = service;
        this.user = user;
        service.addObserver(this);
        initModel();
    }

    @Override
    public void update(SocialNetworkEvent t) {
        initModel();
    }

    @FXML
    public void initialize() {
        initializeFriendsModel(user);
        initializeMessageModel(user);
        friendsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            checkButtons();

        });

    }

    private void initModel() {

        initialize();
    }


    /**
     * Initializes the friendships table view model.
     */
    private void initializeFriendsModel(Utilizator selectedUser) {
        friendsTableView.getItems().clear();
        if (selectedUser != null) {

            ObservableList<Prietenie> friendsModel = FXCollections.observableList((List<Prietenie>) this.service.getUsersFriends(selectedUser));
            friendshipID.setCellValueFactory(new PropertyValueFactory<>("id"));
            friendFirstName.setCellValueFactory(data -> {
                Prietenie prietenie = data.getValue(); // Gets the friendship object for each row
                UUID friendshipId = prietenie.getId();
                // Utilizator u = service.getOtherUser(friendshipId, selectedUser);
                Utilizator u = prietenie.getOther(selectedUser);
                return new SimpleStringProperty(u.getFirstName());
            });

            friendLastName.setCellValueFactory(data -> {
                Prietenie prietenie = data.getValue();
                UUID friendshipId = prietenie.getId();
                // Utilizator u = service.getOtherUser(friendshipId, selectedUser);
                Utilizator u = prietenie.getOther(selectedUser);
                return new SimpleStringProperty(u.getLastName());
            });
            friendshipDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            friendshipStatus.setCellValueFactory(new PropertyValueFactory<>("friendshipStatus"));

            friendsTableView.setItems(friendsModel);

        }
    }

    /**
     * Initializes the messages table view model.
     */
    private void initializeMessageModel(Utilizator selectedUser) {
        messageTableView.getItems().clear();
        if (selectedUser != null) {
            //We are showing only the last message between 2 friends, not every single message
            ObservableList<Message> lastMessagesObservable = FXCollections.observableList(service.getDistinctMessagesBetweenUsers(selectedUser));
            messageID.setCellValueFactory(new PropertyValueFactory<>("id"));
            senderName.setCellValueFactory(data -> {
                Message message = data.getValue(); // Gets the message object for each row
                String fullname = message.getOtherUser(selectedUser).getFirstName() + " " + message.getOtherUser(selectedUser).getLastName();
                return new SimpleStringProperty(fullname);
            });
            messageText.setCellValueFactory(data -> {
                Message message = data.getValue();
                return new SimpleStringProperty(message.getText());
            });

            messageTableView.setItems(lastMessagesObservable);
        }
    }

    /**
     * Action based on the pressing of the 'delete user' button.
     */
    @FXML
    public void userRemoveAction() throws IOException {

        //Getting the selected user object
        Utilizator userToDelete = user;
        //Deleting the user
        this.service.deleteUser(userToDelete.getFirstName(), userToDelete.getLastName());
        //this.userTableView.getSelectionModel().clearSelection();
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", "User removed");
        logoutAction();
    }

    /**
     * Action based on the pressing of the 'update user' button.
     */
    @FXML
    public void userUpdateAction() throws IOException {

        // Loading the scene.
        FXMLLoader userUpdateFXMLLoader = new FXMLLoader(StartApplication.class.getResource("/com/socialnetwork/socialnetwork/views/user-update-dialogue.fxml"));
        Scene userUpdateScene = new Scene(userUpdateFXMLLoader.load());

        // Retrieving the controller of the dialogue.
        UserUpdateDialogue userUpdateDialogue = userUpdateFXMLLoader.getController();

        // Setting text & prompt fields for each field in the dialogue.
        userUpdateDialogue.setFields(user.getId().toString(), user.getFirstName(), user.getLastName());

        // Preparing the stage for showing.
        Stage userUpdateStage = new Stage();
        userUpdateStage.setScene(userUpdateScene);

        // Handle for the case of 'X' button pressing.
        userUpdateStage.setOnCloseRequest(event -> userUpdateDialogue.handleCancel());

        // Showing and waiting for execution.
        userUpdateStage.showAndWait();

        // Checking if the cancel button was pressed.
        if (userUpdateDialogue.isCancelled()) {
            return;
        }

        // Retrieving data from the dialogue.
        Map<String, String> values = userUpdateDialogue.handleUpdate();
        String newFirstName = values.get("firstName");
        String newLastName = values.get("lastName");
        service.updateUser(user.getFirstName(), user.getLastName(), newFirstName, newLastName);
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", "User updated");

    }

    /**
     * Action based on the pressing of the 'add friend' button.
     */
    @FXML
    public void userAddFriendAction() throws IOException {

        // Loading the scene.
        FXMLLoader userAddFriendFXMLLoader = new FXMLLoader(StartApplication.class.getResource("/com/socialnetwork/socialnetwork/views/user-add-friend.fxml"));
        Scene userAddFriendScene = new Scene(userAddFriendFXMLLoader.load());


        // Retrieving the controller of the dialogue.
        UserAddFriendDialogue userAddFriendDialogue = userAddFriendFXMLLoader.getController();

        // Preparing the stage for showing.
        Stage userAddFriendStage = new Stage();
        userAddFriendStage.setScene(userAddFriendScene);

        // Handle for the case of 'X' button pressing.
        userAddFriendStage.setOnCloseRequest(event -> userAddFriendDialogue.handleCancel());

        // Showing and waiting for execution.
        userAddFriendStage.showAndWait();

        // Checking if the cancel button was pressed.
        if (userAddFriendDialogue.isCancelled()) {
            return;
        }

        // Retrieving data from the dialogue.
        Map<String, String> values = userAddFriendDialogue.handleSendFriendRequest();
        String newFirstName = values.get("firstName");
        String newLastName = values.get("lastName");
        service.addPrietenie(user.getFirstName(), user.getLastName(), newFirstName, newLastName);
        initializeFriendsModel(user);
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", "Friend request sent");

    }

    /**
     * Checks and manages button states based on the selected friendship request
     */
    @FXML
    public void checkButtons() {
        if (!friendsTableView.getSelectionModel().isEmpty()) {
            Prietenie selectedFriendship = friendsTableView.getSelectionModel().getSelectedItem();
            //Utilizator user = this.userTableView.getSelectionModel().getSelectedItem();
            if (selectedFriendship.getFriendshipStatus() == FriendshipStatus.PENDING) {
                confirmFriendshipButton.setDisable(false);
                declineFriendshipButton.setDisable(false);
            } else if (selectedFriendship.getFriendshipStatus() == FriendshipStatus.CONFIRMED) {
                confirmFriendshipButton.setDisable(true);
                declineFriendshipButton.setDisable(false);
            } else {
                confirmFriendshipButton.setDisable(true);
                declineFriendshipButton.setDisable(true);
            }
        } else {
            confirmFriendshipButton.setDisable(true);
            declineFriendshipButton.setDisable(true);
        }

    }

    /**
     * Action based on the pressing of the 'confirm friend request' button.
     */
    @FXML
    public void setConfirmFriendshipButton() throws IOException {
        Prietenie selectedFriendship = friendsTableView.getSelectionModel().getSelectedItem();
        service.updateFriendship(selectedFriendship.getId());
        initializeFriendsModel(user);
    }

    /**
     * Action based on the pressing of the 'delete friend request' button.
     */
    @FXML
    public void setDeclineFriendshipButton() throws IOException {
        Prietenie selectedFriendship = friendsTableView.getSelectionModel().getSelectedItem();
        service.removePrietenie(selectedFriendship.getUser1().getFirstName(), selectedFriendship.getUser1().getLastName(), selectedFriendship.getUser2().getFirstName(), selectedFriendship.getUser2().getLastName());
        initializeFriendsModel(user);
    }

    /**
     * Action based on the pressing of the 'send message' button.
     */
    @FXML
    public void sendMessageAction() throws IOException {

        // Loading the scene.
        FXMLLoader sendMessageFXMLLoader = new FXMLLoader(StartApplication.class.getResource("/com/socialnetwork/socialnetwork/views/send-message.fxml"));
        Scene sendMessageScene = new Scene(sendMessageFXMLLoader.load());

        // Retrieving the controller of the dialogue.
        SendMessageDialogue sendMessageDialogue = sendMessageFXMLLoader.getController();

        List<String> friendsNames = new ArrayList<>();
        for (Utilizator u : service.getUserFriendObject(user)) {
            String firstname = u.getFirstName();
            String lastname = u.getLastName();
            friendsNames.add(firstname + " " + lastname);
        }
        sendMessageDialogue.initData(user.getFirstName(), user.getLastName(), friendsNames);
        // Preparing the stage for showing.
        Stage sendMessageStage = new Stage();
        sendMessageStage.setScene(sendMessageScene);

        // Handle for the case of 'X' button pressing.
        sendMessageStage.setOnCloseRequest(event -> sendMessageDialogue.handleCancel());

        // Showing and waiting for execution.
        sendMessageStage.showAndWait();

        // Checking if the cancel button was pressed.
        if (sendMessageDialogue.isCancelled()) {
            return;
        }

        // Retrieving data from the dialogue.
        Map<String, String> values = sendMessageDialogue.handleSendMessage();
        String newFirstName = values.get("firstName");
        String newLastName = values.get("lastName");
        String message = values.get("message");
        Message m = service.addMessage(user.getFirstName(), user.getLastName(), newFirstName, newLastName, message);
        //initializeFriendsModel(user);
        initializeMessageModel(user);
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", "Message sent");

    }

    /**
     * Action based on the pressing of the 'reply' button.
     */
    @FXML
    public void replyAction() throws IOException {
        //Checking if a message is selected
        if (this.messageTableView.getSelectionModel().isEmpty()) {
            MessageAlert.showErrorMessage(null, "No message selected!");
            return;
        }

        // Loading the scene.
        FXMLLoader replyFXMLLoader = new FXMLLoader(StartApplication.class.getResource("/com/socialnetwork/socialnetwork/views/reply.fxml"));
        Scene replyScene = new Scene(replyFXMLLoader.load());

        //Utilizator user = this.userTableView.getSelectionModel().getSelectedItem();
        Message message = this.messageTableView.getSelectionModel().getSelectedItem();
        // Retrieving the controller of the dialogue.
        ReplyDialogue replyDialogue = replyFXMLLoader.getController();
        replyDialogue.initData(message.getOtherUser(user).getFirstName(), message.getOtherUser(user).getLastName());
        // Preparing the stage for showing.
        Stage replyStage = new Stage();
        replyStage.setScene(replyScene);

        // Showing and waiting for execution.
        replyStage.showAndWait();


        // Retrieving data from the dialogue.
        Map<String, String> values = replyDialogue.handleSendMessage();

        String messageText = values.get("message");
        Message replyMessage = service.addMessage(user.getFirstName(), user.getLastName(), message.getOtherUser(user).getFirstName(), message.getOtherUser(user).getLastName(), messageText);
        message.setReply(replyMessage);
        service.updateMessage(message);
        //initializeFriendsModel(user);
        initializeMessageModel(user);
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", "Message sent");

    }

    /**
     * Action based on the pressing of the 'view conversation' button.
     */
    @FXML
    public void viewConvoAction() throws IOException {
        if (this.messageTableView.getSelectionModel().isEmpty()) {
            MessageAlert.showErrorMessage(null, "No message selected!");
            return;
        }
        Message message = this.messageTableView.getSelectionModel().getSelectedItem();
        displayConversationAsAlert(service.getUsersOrderedMessages(message.getUser1(), message.getUser2()));
    }

    private void displayConversationAsAlert(List<Message> conversation) {
        StringBuilder conversationText = new StringBuilder();
        for (Message message : conversation) {
            String sender = message.getUser1().getFullName();
            String receiver = message.getUser2().getFullName();
            conversationText.append(sender)
                    .append(": ")
                    .append(message.getText())
                    .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conversation");
        //alert.setHeaderText("Entire Conversation between " + user1.getFirstName() + " and " + user2.getFirstName());
        alert.setContentText(conversationText.toString());
        alert.showAndWait();
    }

    /**
     * Action based on the pressing of the 'logout' button.
     */
    public void logoutAction() throws IOException {
        Stage stage = (Stage) friendsTableView.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/socialnetwork/socialnetwork/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public void onPrevious() {
        currentPage--;
        initModel();
    }

    public void onNext() {
        currentPage++;
        initModel();
    }

    public void changePageSize(int newSize) {
        pageSize = newSize;
        initModel();
    }




}
