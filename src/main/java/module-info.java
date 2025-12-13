module org.example.vechimo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.services.textract;
    requires software.amazon.awssdk.regions;
    requires io.github.cdimascio.dotenv.java;

    opens org.example.vechimo to javafx.fxml;
    exports org.example.vechimo;
}