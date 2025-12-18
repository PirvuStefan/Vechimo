package org.example.vechimo;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.screens.WindowController;

import java.io.IOException;

public class VechimoApplication extends Application {


    @Override
    public void start(Stage stage) {

        WindowController.loadMainView(stage, "Vechimo Application");
    }
}
