package org.example.screens;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.vechimo.Parsing;
import org.example.vechimo.User;

public class WindowController {

    private static boolean run = false;

    public static void loadMainView(Stage stage, String title) {
        try {



            javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
            root.setPrefSize(600, 500);


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


            javafx.scene.layout.VBox glassCard = new javafx.scene.layout.VBox(20); // 20px spacing
            glassCard.setAlignment(javafx.geometry.Pos.CENTER);
            glassCard.setMaxWidth(450);
            glassCard.setMaxHeight(400);
            glassCard.setPadding(new javafx.geometry.Insets(30));


            glassCard.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.25);" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.6);" +
                            "-fx-border-radius: 20;" +
                            "-fx-border-width: 1.5;"
            );


            javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
            shadow.setColor(javafx.scene.paint.Color.rgb(0, 0, 0, 0.15));
            shadow.setRadius(20);
            glassCard.setEffect(shadow);


            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Generator Adeverință");
            titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
            javafx.scene.effect.DropShadow textShadow = new javafx.scene.effect.DropShadow(3, javafx.scene.paint.Color.rgb(0,0,0,0.3));
            titleLabel.setEffect(textShadow);


            javafx.scene.control.Label descLabel = new javafx.scene.control.Label(
                    "Această aplicație generează adeverința de vechime.\nSelectați fișierul PDF sursă pentru a începe."
            );
            descLabel.setWrapText(true);
            descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            descLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: white;");
            descLabel.setEffect(textShadow);


            javafx.scene.control.TextField pathField = new javafx.scene.control.TextField();
            pathField.setPromptText("Niciun fișier selectat...");
            pathField.setEditable(false);
            pathField.setPrefWidth(220);
            pathField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-text-fill: #333; -fx-background-radius: 5;");

            // second file selector field
            javafx.scene.control.TextField pathField2 = new javafx.scene.control.TextField();
            pathField2.setPromptText("Niciun fișier selectat...");
            pathField2.setEditable(false);
            pathField2.setPrefWidth(220);
            pathField2.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-text-fill: #333; -fx-background-radius: 5;");

            javafx.scene.control.Button browseBtn = new javafx.scene.control.Button("Alege PDF 1");
            String btnStyleCommon = "-fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;";
            browseBtn.setStyle(btnStyleCommon + "-fx-background-color: rgba(255,255,255,0.5); -fx-text-fill: white;");

            javafx.scene.control.Button browseBtn2 = new javafx.scene.control.Button("Alege PDF 2");
            browseBtn2.setStyle(btnStyleCommon + "-fx-background-color: rgba(255,255,255,0.5); -fx-text-fill: white;");

            javafx.scene.control.Label idLabel = new javafx.scene.control.Label("Număr Identificare:");
            idLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-text-fill: white;");

            javafx.scene.control.TextField idField = new javafx.scene.control.TextField();
            idField.setPromptText("Introdu numărul de identificare...");
            idField.setPrefWidth(200);
            idField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-text-fill: #333; -fx-background-radius: 5;");

            javafx.scene.layout.HBox idBox = new javafx.scene.layout.HBox(10, idLabel, idField);
            idBox.setAlignment(javafx.geometry.Pos.CENTER);


            browseBtn.setOnAction(e -> {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setTitle("Selecteaza PDF 1");
                fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
                java.io.File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null) {
                    pathField.setText(selectedFile.getAbsolutePath());
                }
            });

            browseBtn2.setOnAction(e -> {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setTitle("Selecteaza PDF 2");
                fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
                java.io.File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null) {
                    pathField2.setText(selectedFile.getAbsolutePath());
                }
            });

            // first selector row
            javafx.scene.layout.HBox inputBox = new javafx.scene.layout.HBox(10, pathField, browseBtn);
            inputBox.setAlignment(javafx.geometry.Pos.CENTER);

            // second selector row (under the first)
            javafx.scene.layout.HBox inputBox2 = new javafx.scene.layout.HBox(10, pathField2, browseBtn2);
            inputBox2.setAlignment(javafx.geometry.Pos.CENTER);

            javafx.scene.control.Button submitBtn = new javafx.scene.control.Button("Generează Document");
            submitBtn.setPrefSize(200, 40);

            submitBtn.setStyle(btnStyleCommon + "-fx-background-color: white; -fx-text-fill: #ff8800; -fx-font-size: 14px;");


            submitBtn.setOnMouseEntered(e -> submitBtn.setStyle(btnStyleCommon + "-fx-background-color: #fff8e1; -fx-text-fill: #ff8800; -fx-font-size: 14px;"));
            submitBtn.setOnMouseExited(e -> submitBtn.setStyle(btnStyleCommon + "-fx-background-color: white; -fx-text-fill: #ff8800; -fx-font-size: 14px;"));


            submitBtn.setOnAction(e -> {
                String path1 = pathField.getText();
                String path2 = pathField2.getText();
                if (idField.getText().isEmpty() && !run) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Te rog introdu numarul de identificare.");
                    alert.showAndWait();
                    return;

                }
                // require both PDFs
                if (path1 == null || path1.isEmpty()) {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Te rog selecteaza un fisier mai intai.(PDF 1)");
                    alert.showAndWait();
                    System.out.println("Validation Error: Please select a file first.");

                } else {
                    System.out.println("Processing PDFs at: " + path1 + " and " + path2);

                    try {

                        run = true;
                        User.addNumber(idField.getText());
                        // pass first PDF path into Parsing (adjust Parsing if it needs both)
                        new Parsing(path1, path2);

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Document generat cu succes!");
                        successAlert.showAndWait();
                        // cleanup after the dialog is closed
                        User.resetData();
                        run = false;


                    } catch (java.io.IOException ioException) {
                        System.err.println("Error processing file: " + ioException.getMessage());
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Processing Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("A apărut o eroare la procesarea fișierului.");
                        errorAlert.showAndWait();
                    }

                }


            });

            // add both selector rows to the card
            glassCard.getChildren().addAll(titleLabel, descLabel, inputBox, inputBox2, idBox, submitBtn);
            root.getChildren().add(glassCard);


            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            // Catch generic Exception to be safe since we removed IOException specific FXML loading
            System.err.println("Error loading Main View.");
            e.printStackTrace();
        }
    }


    public static boolean isRun() {
        return run;
    }

    public static void setRun(boolean run) {
        WindowController.run = run;
    }
}
