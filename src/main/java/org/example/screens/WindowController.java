package org.example.screens;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class WindowController {


    // Do not load FXML during field initialization — that can run before resources are available
    // and causes "Location is not set." Instead, load views from the Application class or via
    // an explicit method when needed.

    public static void loadMainView(Stage stage, String fxmlPath, String title) {

        try {
            // 1. Resolve resource and load the FXML
           // URL resource = getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource(fxmlPath));

           // FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();


            Scene scene = new Scene(root,400, 525);


            stage.setTitle(title);
            stage.setScene(scene);



            stage.centerOnScreen();


            stage.show();

        } catch (IOException e) {
            System.err.println("Could not load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }





}



