package com.comp2042.ui;

import com.comp2042.logic.GameController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

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

        Scene currentScene = ((Node) event.getSource()).getScene();

        // Just replace the root of the existing scene
        currentScene.setRoot(root);
        c.startNewGame();
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