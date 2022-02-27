module com.example.sundhed {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    opens com.example.sundhed to javafx.fxml;
    exports com.example.sundhed;
}