module org.example.vechimo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.vechimo to javafx.fxml;
    exports org.example.vechimo;
}