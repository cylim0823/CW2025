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

/**
 * Controller for the main menu screen.
 * <p>
 * Loads the highest score, handles button presses, and switches
 * to the game scene with the selected {@link GameMode}.
 * </p>
 */
public class MainMenuController implements Initializable {

    private static final String FONT_PATH = GameConfiguration.FONT_PATH;
    private static final double FONT_SIZE = GameConfiguration.FONT_SIZE_DEFAULT;

    @FXML private Label highScoreLabel;

    /**
     * Initializes the main menu.
     * <p>
     * - Loads custom font<br>
     * - Retrieves and displays the highest saved score
     * </p>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCustomFont();

        ScoreManager sm = new ScoreManager();
        if (highScoreLabel != null) {
            highScoreLabel.setText("Highest Score: " + sm.getHighestScore());
        }
    }

    /**
     * Loads a custom font for UI labels.
     * Logs an error if the font cannot be loaded.
     */
    private void loadCustomFont() {
        try {
            Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
        } catch (Exception e) {
            System.err.println("Could not load font: " + FONT_PATH);
        }
    }

    // Button Handlers

    /**
     * Starts the game in Normal Mode when the "Start" button is clicked.
     *
     * @param event the button click action event
     * @throws IOException if the game layout FXML cannot be loaded
     */
    @FXML
    private void handleStartButton(ActionEvent event) throws IOException {
        loadGameScene(event, new NormalMode());
    }

    /**
     * Starts the game in Zen Mode when the "Zen Mode" button is clicked.
     *
     * @param event the button click action event
     * @throws IOException if the game layout FXML cannot be loaded
     */
    @FXML
    private void handleZenModeButton(ActionEvent event) throws IOException {
        loadGameScene(event, new ZenMode());
    }

    /**
     * Exits the application when the "Exit" button is clicked.
     *
     * @param event the button click action event
     */
    @FXML
    private void handleExitButton(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Loads the game layout scene and initializes it with
     * the selected {@link GameMode}.
     *
     * <p>
     * Steps performed:
     * <ol>
     *     <li>Load the gameLayout.fxml file</li>
     *     <li>Get the {@link GuiController}</li>
     *     <li>Pass the chosen GameMode strategy</li>
     *     <li>Start a new game</li>
     *     <li>Replace the current scene root</li>
     * </ol>
     * </p>
     *
     * @param event the button click event used to access the current scene
     * @param mode the gameplay mode (Normal, Zen, etc.)
     * @throws IOException if FXML cannot be found or loaded
     */
    private void loadGameScene(ActionEvent event, GameMode mode) throws IOException {
        URL location = getClass().getClassLoader().getResource("fxml/gameLayout.fxml");
        if (location == null) {
            throw new IOException("Could not find fxml/gameLayout.fxml");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        GuiController c = fxmlLoader.getController();

        // Apply selected mode (Strategy pattern)
        c.initGameMode(mode);
        c.startNewGame();

        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(root);

        Platform.runLater(root::requestFocus);
    }
}