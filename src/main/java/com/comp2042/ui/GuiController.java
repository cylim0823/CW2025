package com.comp2042.ui;

import com.comp2042.logic.InputEventListener;
import com.comp2042.model.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
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

public class GuiController implements Initializable {

    // Constants
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

    private InputEventListener eventListener;
    private GameRenderer gameRenderer;
    private GameLoopManager gameLoopManager;
    private KeyManager keyManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCustomFont();

        rootPane.requestFocus();

        this.gameRenderer = new GameRenderer(gamePanel, nextBricksContainer, holdBrickPanel);
        this.gameLoopManager = new GameLoopManager(this::onGameTick, countdownLabel);

        this.keyManager = new KeyManager(this, gameLoopManager);
        rootPane.setOnKeyPressed(keyManager::handleInput);

        resetUIState();
    }

    private void loadCustomFont() {
        try {
            Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
        } catch (Exception e) {
            System.err.println("Could not load font: " + FONT_PATH);
        }
    }


    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameRenderer.initGameView(boardMatrix, brick);
        gameLoopManager.initGameLoop();
        gameLoopManager.showCountdown();
    }

    private void onGameTick() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
    }

    public void refreshBrick(ViewData brick) {
        if (gameLoopManager.isPauseProperty().get()) {
            return;
        }
        gameRenderer.refreshBrick(brick);
    }

    public void refreshGameBackground(int[][] board) {
        gameRenderer.refreshGameBackground(board);
    }

    public void moveDown(MoveEvent event) {
        if (gameLoopManager.isPauseProperty().get()) {
            return;
        }
        DownData downData = eventListener.onDownEvent(event);
        refreshBrick(downData.getViewData());
        rootPane.requestFocus();
    }

    public void startNewGame() {
        ViewData initialData = eventListener.createNewGame();
        int[][] initialBoard = eventListener.getBoard();

        resetUIState();
        rootPane.requestFocus();

        gameLoopManager.newGame();
        refreshGameBackground(initialBoard);
        refreshBrick(initialData);
    }

    public void togglePause() {
        gameLoopManager.togglePause();
        boolean isPaused = gameLoopManager.isPauseProperty().get();

        pausePane.setVisible(isPaused);
        if (isPaused) {
            pausePane.toFront();
        }
        rootPane.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        if (this.keyManager != null) {
            this.keyManager.setEventListener(eventListener);
        }
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString("Score: %d"));
    }

    public void bindLevel(IntegerProperty integerProperty) {
        levelLabel.textProperty().bind(integerProperty.asString("Level: %d"));
    }

    public void updateLevel(int level) {
        gameLoopManager.updateLevel(level);
    }

    public void showCountdown() {
        gameLoopManager.showCountdown();
    }

    public void showLineClearNotification(String message) {
        NotificationPanel notifPanel = new NotificationPanel(message);
        rootPane.getChildren().add(notifPanel);
        notifPanel.showScore(rootPane.getChildren());
    }

    public void gameOver() {
        gameLoopManager.gameOver();
        gameOverPane.setVisible(true);
        gameOverPane.toFront();
    }

    private void resetUIState() {
        gameOverPane.setVisible(false);
        pausePane.setVisible(false);
    }


    @FXML
    public void handleNewGameButton(ActionEvent actionEvent) {
        startNewGame();
    }

    @FXML
    public void handlePauseButton(ActionEvent actionEvent) {
        togglePause();
    }

    @FXML
    private void handleExitButton(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void handleMainMenuButton(ActionEvent actionEvent) {
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