module ro.ubbcluj.map.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;


    opens ro.ubbcluj.map.socialnetwork to javafx.fxml;
    exports ro.ubbcluj.map.socialnetwork;
}