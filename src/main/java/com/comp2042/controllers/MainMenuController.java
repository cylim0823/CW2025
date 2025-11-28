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

    // Button handlers

    @FXML
    private void handleStartButton(ActionEvent event) throws IOException {
        loadGameScene(event, false); // FALSE = Normal Mode
    }

    @FXML
    private void handleZenModeButton(ActionEvent event) throws IOException {
        loadGameScene(event, true);  // TRUE = Zen Mode
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Helper method to load the game scene.
     * Prevents code duplication between Start and Zen buttons.
     */
    private void loadGameScene(ActionEvent event, boolean isZenMode) throws IOException {
        URL location = getClass().getClassLoader().getResource("fxml/gameLayout.fxml");
        if (location == null) {
            throw new IOException("Could not find fxml/gameLayout.fxml");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        GuiController c = fxmlLoader.getController();

        c.initGameMode(isZenMode);
        c.startNewGame();

        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(root);

        javafx.application.Platform.runLater(root::requestFocus);
    }
}