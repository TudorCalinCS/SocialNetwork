module com.socialnetwork.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.socialnetwork.socialnetwork to javafx.fxml;
    exports com.socialnetwork.socialnetwork;
    exports com.socialnetwork.socialnetwork.controllers;
    opens com.socialnetwork.socialnetwork.controllers to javafx.fxml;
    opens com.socialnetwork.socialnetwork.domain to javafx.base;
}