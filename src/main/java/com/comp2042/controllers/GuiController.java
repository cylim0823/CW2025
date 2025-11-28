package com.comp2042.controllers;

import com.comp2042.logic.InputEventListener;
import com.comp2042.managers.ScoreManager;
import com.comp2042.managers.ColorManager;
import com.comp2042.managers.GameLoopManager;
import com.comp2042.managers.KeyManager;
import com.comp2042.managers.SoundManager;
import com.comp2042.model.*;
import com.comp2042.ui.*;
import com.comp2042.managers.EffectManager;
import com.comp2042.util.EventSource;
import com.comp2042.util.EventType;
import com.comp2042.util.GameConfiguration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable, GameObserver {

    private static final String FONT_PATH = GameConfiguration.FONT_PATH;
    private static final double FONT_SIZE = GameConfiguration.FONT_SIZE_DEFAULT;

    @FXML private GridPane gamePanel;
    @FXML private VBox nextBricksContainer;
    @FXML private GridPane holdBrickPanel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label countdownLabel;
    @FXML private StackPane rootPane;
    @FXML private VBox pausePane;
    @FXML private VBox gameOverPane;

    // High Score Labels
    @FXML private Label gameOverScoreLabel;
    @FXML private Label currentScoreLabel;

    private SoundManager soundManager;
    private GameRenderer gameRenderer;
    private GameLoopManager gameLoopManager;
    private KeyManager keyManager;
    private EffectManager effectManager;
    private GameController gameController;

    private InputEventListener eventListener;
    private int currentScore = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCustomFont();
        rootPane.requestFocus();

        ColorManager colorManager = new ColorManager();
        this.soundManager = new SoundManager();
        this.gameRenderer = new GameRenderer(gamePanel, nextBricksContainer, holdBrickPanel, colorManager);
        this.gameLoopManager = new GameLoopManager(this::onGameTick, countdownLabel);
        this.keyManager = new KeyManager(this, gameLoopManager);
        this.effectManager = new EffectManager(gamePanel);
        this.gameController = new GameController();

        gameController.addObserver(soundManager);
        soundManager.playMusic();

        gameRenderer.initGameView(gameController.getBoard());

        gameLoopManager.initGameLoop();
        gameController.addObserver(this);

        this.setEventListener(gameController);
        rootPane.setOnKeyPressed(keyManager::handleInput);

        resetUIState();
    }

    public void initGameMode(boolean isZenMode) {
        if (gameController != null) {
            gameController.setZenMode(isZenMode);
        }
        if (levelLabel != null) {
            levelLabel.setVisible(!isZenMode);
        }
    }

    // Observer methods

    @Override
    public void onBoardUpdated(ViewData viewData) {
        gameRenderer.refreshBrick(viewData);
    }

    @Override
    public void onGameBackgroundUpdated(int[][] boardMatrix) {
        gameRenderer.refreshGameBackground(boardMatrix);
    }

    @Override
    public void onScoreUpdated(int score) {
        this.currentScore = score;
        Platform.runLater(() -> scoreLabel.setText("Score: " + score));
    }

    @Override
    public void onLevelUpdated(int level) {
        Platform.runLater(() -> levelLabel.setText("Level: " + level));
        gameLoopManager.updateLevel(level);
    }

    @Override
    public void onLineCleared(int lines, String message) {
        Platform.runLater(() -> showLineClearNotification(message));
    }

    @Override
    public void onBrickDropped() {}

    @Override
    public void onGameOver() {
        Platform.runLater(() -> {
            gameLoopManager.gameOver();
            if (soundManager != null) soundManager.onGameOver();

            ScoreManager sm = new ScoreManager();
            int bestScore = sm.getHighestScore();

            if (gameOverScoreLabel != null) {
                gameOverScoreLabel.setText("Highest Score: " + bestScore);
            }
            if (currentScoreLabel != null) {
                currentScoreLabel.setText("Your Score: " + currentScore);
            }

            gameOverPane.setVisible(true);
            gameOverPane.toFront();
        });
    }

    @Override
    public void onDangerStateChanged(boolean isDanger) {
        // Visual updates must happen on the JavaFX thread
        Platform.runLater(() -> {
            if (effectManager != null) {
                effectManager.setDangerMode(isDanger);
            }
        });
    }

    // Helper methods
    private void onGameTick() {
        if (eventListener != null) {
            eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        }
    }

    public void moveDown(MoveEvent event) {
        if (gameLoopManager.isPauseProperty().get()) return;
        eventListener.onDownEvent(event);
    }

    public void startNewGame() {
        eventListener.createNewGame();
        resetUIState();
        rootPane.requestFocus();
        gameLoopManager.newGame();

        if (soundManager != null) {
            soundManager.playMusic();
        }
    }

    public void togglePause() {
        gameLoopManager.togglePause();
        boolean isPaused = gameLoopManager.isPauseProperty().get();
        pausePane.setVisible(isPaused);
        if(isPaused) pausePane.toFront();
        rootPane.requestFocus();
    }

    public void toggleMute(){
        if (soundManager != null){
            soundManager.toggleMute();
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        if (this.keyManager != null) {
            this.keyManager.setEventListener(eventListener);
        }
    }

    public void showLineClearNotification(String message) {
        NotificationPanel notifPanel = new NotificationPanel(message);
        rootPane.getChildren().add(notifPanel);
        notifPanel.showScore(rootPane.getChildren());
    }

    private void resetUIState() {
        gameOverPane.setVisible(false);
        pausePane.setVisible(false);
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
    public void handleNewGameButton() {
        startNewGame();
    }

    @FXML
    public void handlePauseButton() {
        togglePause();
    }

    @FXML
    public void handleMainMenuButton() {
        gameLoopManager.gameOver();
        if (soundManager != null){
            soundManager.stopMusic();
        }
        loadScene("fxml/mainMenu.fxml");
    }

    private void loadScene(String fxmlFile) {
        try {
            URL url = getClass().getClassLoader().getResource(fxmlFile);
            if (url == null) throw new IOException("File not found: " + fxmlFile);

            FXMLLoader fxmlLoader = new FXMLLoader(url);
            Parent root = fxmlLoader.load();
            Scene currentScene = rootPane.getScene();
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}