module org.example.cm2601 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.base;
    requires com.google.gson;


    opens org.example.cm2601 to javafx.fxml;
    exports org.example.cm2601;
    exports org.example.cm2601.Controller;
    opens org.example.cm2601.Controller to javafx.fxml;
    exports org.example.cm2601.model;
    opens org.example.cm2601.model to javafx.fxml;
}