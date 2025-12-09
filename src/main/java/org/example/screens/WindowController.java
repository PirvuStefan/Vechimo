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
            // --- NOTE: We are bypassing FXML loading to generate the specific ---
            // --- Glassmorphism UI programmatically as requested. ---

            // 1. Root Container (The Background)
            javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
            root.setPrefSize(600, 500); // Slightly larger for better breathing room

            // 2. The Gradient Background (ff8800 -> ffb700 -> ffea00)
            javafx.scene.paint.Stop[] stops = new javafx.scene.paint.Stop[] {
                    new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web("#ff8800")),
                    new javafx.scene.paint.Stop(0.5, javafx.scene.paint.Color.web("#ffb700")),
                    new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web("#ffea00"))
            };
            javafx.scene.paint.LinearGradient gradient = new javafx.scene.paint.LinearGradient(
                    0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE, stops
            );
            root.setBackground(new javafx.scene.layout.Background(
                    new javafx.scene.layout.BackgroundFill(gradient, javafx.scene.layout.CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)
            ));

            // 3. The "Glass" Card (Center Container)
            javafx.scene.layout.VBox glassCard = new javafx.scene.layout.VBox(20); // 20px spacing
            glassCard.setAlignment(javafx.geometry.Pos.CENTER);
            glassCard.setMaxWidth(450);
            glassCard.setMaxHeight(400);
            glassCard.setPadding(new javafx.geometry.Insets(30));

            // CSS for Glassmorphism (Semi-transparent white + Blur feel)
            glassCard.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.25);" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.6);" +
                            "-fx-border-radius: 20;" +
                            "-fx-border-width: 1.5;"
            );

            // Drop Shadow for depth
            javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
            shadow.setColor(javafx.scene.paint.Color.rgb(0, 0, 0, 0.15));
            shadow.setRadius(20);
            glassCard.setEffect(shadow);

            // 4. UI Components

            // Title
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Generator Adeverință");
            titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
            javafx.scene.effect.DropShadow textShadow = new javafx.scene.effect.DropShadow(3, javafx.scene.paint.Color.rgb(0,0,0,0.3));
            titleLabel.setEffect(textShadow);

            // Description
            javafx.scene.control.Label descLabel = new javafx.scene.control.Label(
                    "Această aplicație generează adeverința de vechime.\nSelectați fișierul PDF sursă pentru a începe."
            );
            descLabel.setWrapText(true);
            descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            descLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: white;");
            descLabel.setEffect(textShadow);

            // Input Field (Read only, displays path)
            javafx.scene.control.TextField pathField = new javafx.scene.control.TextField();
            pathField.setPromptText("Niciun fișier selectat...");
            pathField.setEditable(false);
            pathField.setPrefWidth(220);
            pathField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-text-fill: #333; -fx-background-radius: 5;");

            // Browse Button
            javafx.scene.control.Button browseBtn = new javafx.scene.control.Button("Alege PDF");
            String btnStyleCommon = "-fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;";
            browseBtn.setStyle(btnStyleCommon + "-fx-background-color: rgba(255,255,255,0.5); -fx-text-fill: white;");

            // Browse Logic
            browseBtn.setOnAction(e -> {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setTitle("Selectează PDF");
                fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
                java.io.File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null) {
                    pathField.setText(selectedFile.getAbsolutePath());
                }
            });

            // Layout for Input line
            javafx.scene.layout.HBox inputBox = new javafx.scene.layout.HBox(10, pathField, browseBtn);
            inputBox.setAlignment(javafx.geometry.Pos.CENTER);

            // Submit Button
            javafx.scene.control.Button submitBtn = new javafx.scene.control.Button("Generează Document");
            submitBtn.setPrefSize(200, 40);
            // White button with orange text for contrast
            submitBtn.setStyle(btnStyleCommon + "-fx-background-color: white; -fx-text-fill: #ff8800; -fx-font-size: 14px;");

            // Hover effects for Submit Button
            submitBtn.setOnMouseEntered(e -> submitBtn.setStyle(btnStyleCommon + "-fx-background-color: #fff8e1; -fx-text-fill: #ff8800; -fx-font-size: 14px;"));
            submitBtn.setOnMouseExited(e -> submitBtn.setStyle(btnStyleCommon + "-fx-background-color: white; -fx-text-fill: #ff8800; -fx-font-size: 14px;"));

            // Submit Logic
            submitBtn.setOnAction(e -> {
                String path = pathField.getText();
                if (path == null || path.isEmpty()) {
                    System.out.println("Validation Error: Please select a file first.");
                    // Optional: Show an Alert here
                } else {
                    System.out.println("Processing PDF at: " + path);
                    // TODO: CALL YOUR PDF GENERATION LOGIC HERE
                }
            });

            // Assemble
            glassCard.getChildren().addAll(titleLabel, descLabel, inputBox, submitBtn);
            root.getChildren().add(glassCard);

            // 5. Final Scene Setup
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            stage.setTitle(title); // Uses the title passed in the argument
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            // Catch generic Exception to be safe since we removed IOException specific FXML loading
            System.err.println("Error loading Main View.");
            e.printStackTrace();
        }
    }





}



