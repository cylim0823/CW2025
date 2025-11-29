package com.comp2042.controllers;

import com.comp2042.logic.mode.GameMode;
import com.comp2042.logic.mode.NormalMode;
import com.comp2042.logic.mode.ZenMode;
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
        loadGameScene(event, new NormalMode());
    }

    @FXML
    private void handleZenModeButton(ActionEvent event) throws IOException {
        loadGameScene(event, new ZenMode());
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Helper method to load the game scene.
     * REFACTORED: Accepts GameMode interface
     */
    private void loadGameScene(ActionEvent event, GameMode mode) throws IOException {
        URL location = getClass().getClassLoader().getResource("fxml/gameLayout.fxml");
        if (location == null) {
            throw new IOException("Could not find fxml/gameLayout.fxml");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        GuiController c = fxmlLoader.getController();

        // Pass the Strategy Object
        c.initGameMode(mode);
        c.startNewGame();

        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(root);

        javafx.application.Platform.runLater(root::requestFocus);
    }
}