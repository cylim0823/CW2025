package com.comp2042.controllers;

import com.comp2042.managers.ScoreManager;
import com.comp2042.util.GameConfiguration;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    private static final String FONT_PATH = GameConfiguration.FONT_PATH;
    private static final double FONT_SIZE = GameConfiguration.FONT_SIZE_DEFAULT;

    @FXML private Label highScoreLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCustomFont();

        ScoreManager sm = new ScoreManager();

        if (highScoreLabel != null) {
            // REFACTOR: Changed getHighScore() to getHighestScore()
            highScoreLabel.setText("Highest Score: " + sm.getHighestScore());
        }
    }

    private void loadCustomFont() {
        try {
            Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
        } catch (Exception e) {
            System.err.println("Could not load font: " + FONT_PATH);
        }
    }

    /**
     * This method is called when the "Start Game" button is clicked.
     * It loads the game scene and replaces the menu scene.
     */
    @FXML
    private void handleStartButton(ActionEvent event) throws IOException {

        // Load the game's FXML and its controller
        URL location = getClass().getClassLoader().getResource("fxml/gameLayout.fxml");
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