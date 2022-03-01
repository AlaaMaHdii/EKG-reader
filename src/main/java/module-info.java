module com.example.sundhed {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    opens com.hmaar.sundhed to javafx.fxml;
    exports com.hmaar.sundhed;
}