package com.comp2042.ui;

import com.comp2042.logic.GameController;
import com.comp2042.logic.InputEventListener;
import com.comp2042.model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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

    private static final String FONT_PATH = "/digital.ttf";
    private static final double FONT_SIZE = 38;

    @FXML private GridPane gamePanel;
    @FXML private VBox nextBricksContainer;
    @FXML private GridPane holdBrickPanel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label countdownLabel;
    @FXML private StackPane rootPane;
    @FXML private VBox pausePane;
    @FXML private VBox gameOverPane;

    private GameRenderer gameRenderer;
    private GameLoopManager gameLoopManager;
    private KeyManager keyManager;

    // The logic interface we talk to GameController
    private InputEventListener eventListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCustomFont();
        rootPane.requestFocus();

        this.gameRenderer = new GameRenderer(gamePanel, nextBricksContainer, holdBrickPanel);
        this.gameLoopManager = new GameLoopManager(this::onGameTick, countdownLabel);
        this.keyManager = new KeyManager(this, gameLoopManager);
        GameController logic = new GameController();

        gameRenderer.initGameView(logic.getBoard(), logic.getViewData());
        gameLoopManager.initGameLoop();
        logic.addObserver(this);

        this.setEventListener(logic);
        rootPane.setOnKeyPressed(keyManager::handleInput);

        resetUIState();
    }

    // Observer methods (Automatic updates from logic)

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
        Platform.runLater(() -> scoreLabel.setText("Score: " + score));
    }

    @Override
    public void onLevelUpdated(int level) {
        Platform.runLater(() -> levelLabel.setText("Level: " + level));
        gameLoopManager.updateLevel(level);
    }

    @Override
    public void onLineCleared(String message) {
        Platform.runLater(() -> showLineClearNotification(message));
    }

    @Override
    public void onGameOver() {
        Platform.runLater(() -> {
            gameLoopManager.gameOver();
            gameOverPane.setVisible(true);
            gameOverPane.toFront();
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
    }

    public void togglePause() {
        gameLoopManager.togglePause();
        boolean isPaused = gameLoopManager.isPauseProperty().get();
        pausePane.setVisible(isPaused);
        if(isPaused) pausePane.toFront();
        rootPane.requestFocus();
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
    @FXML public void handleNewGameButton(ActionEvent e) { startNewGame(); }
    @FXML public void handlePauseButton(ActionEvent e) { togglePause(); }
    @FXML public void handleMainMenuButton(ActionEvent e) {
        try {
            gameLoopManager.gameOver();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent mainMenuRoot = fxmlLoader.load();
            Scene currentScene = rootPane.getScene();
            currentScene.setRoot(mainMenuRoot);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}