package com.socialnetwork.socialnetwork;

import com.socialnetwork.socialnetwork.controllers.AdminController;
import com.socialnetwork.socialnetwork.controllers.LoginController;
import com.socialnetwork.socialnetwork.domain.Message;
import com.socialnetwork.socialnetwork.domain.Prietenie;
import com.socialnetwork.socialnetwork.domain.Utilizator;
import com.socialnetwork.socialnetwork.repository.FriendshipDbRepository;
import com.socialnetwork.socialnetwork.repository.MessageDbRepository;
import com.socialnetwork.socialnetwork.repository.PagingRepository;
import com.socialnetwork.socialnetwork.repository.UserDbRepository;
import com.socialnetwork.socialnetwork.service.ServiceFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.socialnetwork.socialnetwork.repository.Repository;

import java.io.IOException;
import java.util.UUID;

public class StartApplication extends Application {

    public void start(Stage stage) throws IOException {

        PagingRepository<UUID, Utilizator> userRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetworkDB", "postgres", "password");
        PagingRepository<UUID, Prietenie> friendshipRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetworkDB", "postgres", "password");
        Repository<UUID, Message> messageRepo = new MessageDbRepository("jdbc:postgresql://localhost:5432/SocialNetworkDB", "postgres", "password");

        ServiceFX serviceFX = new ServiceFX(userRepository, friendshipRepository, messageRepo);

        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/socialnetwork/socialnetwork/views/login-view.fxml"));
        //serviceFX.addEntities();

        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(serviceFX);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}