package com.comp2042.ui;

import com.comp2042.logic.GameController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController {

    /**
     * This method is called when the "Start Game" button is clicked.
     * It loads the game scene and replaces the menu scene.
     */
    @FXML
    private void handleStartButton(ActionEvent event) throws IOException {

        // Load the game's FXML and its controller
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        // Get the current window (Stage) from the button
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Create the new game scene
        Scene scene = new Scene(root, 1000, 700);

        // Set the window to show the new game scene
        stage.setScene(scene);
        stage.show();

        // Start the game logic
        new GameController(c);
    }

    /**
     * This method is called when the "Exit" button is clicked.
     */
    @FXML
    private void handleExitButton(ActionEvent event) {
        // Shuts down the application
        Platform.exit();
    }
}