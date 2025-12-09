package org.example.vechimo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.screens.MainWindow;
import org.example.screens.WindowController;

import java.io.IOException;

public class VechimoApplication extends Application {

    private Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {

        //Use a helper class to load the main window
        WindowController.loadMainView(stage, "/org/example/vechimo/hello-view.fxml", "Vechimo Application");
    }
}
