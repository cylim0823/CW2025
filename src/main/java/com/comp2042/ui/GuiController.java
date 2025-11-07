package com.comp2042.ui;

import com.comp2042.logic.InputEventListener;
import com.comp2042.model.*;
import javafx.animation.PauseTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {
    @FXML
    private GridPane gamePanel;
    @FXML
    private Group groupNotification;
    @FXML
    private GridPane brickPanel;
    @FXML
    private GameOverPanel gameOverPanel;
    @FXML
    private GridPane nextBrickPanel;
    @FXML
    private GridPane holdBrickPanel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label countdownLabel;

    private InputEventListener eventListener;
    private GameRenderer gameRenderer;
    private GameLoopManager gameLoopManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getResourceAsStream("/digital.ttf"), 38);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        this.gameRenderer = new GameRenderer(gamePanel, brickPanel, nextBrickPanel, holdBrickPanel);
        this.gameLoopManager = new GameLoopManager(this::onGameTick, countdownLabel, gameOverPanel, groupNotification);

        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                // Ask the manager for the state
                if (keyEvent.getCode() == KeyCode.P) {
                    gameLoopManager.togglePause();
                    keyEvent.consume();
                }

                if (gameLoopManager.isCountingDownProperty().get()) {
                    keyEvent.consume();
                    return;
                }

                if (!gameLoopManager.isPauseProperty().get() && !gameLoopManager.isGameOverProperty().get()) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        refreshBrick(eventListener.onHardDropEvent(new MoveEvent(null, EventSource.USER)));
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.C) {
                        refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);
        gameOverPanel.setOnPlayAgain(this::newGame);
        gameOverPanel.setOnMainMenu(e -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
                Parent mainMenuRoot = fxmlLoader.load();
                Stage stage = (Stage) gameOverPanel.getScene().getWindow();
                Scene scene = new Scene(mainMenuRoot, 450, 510);
                stage.setScene(scene);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameRenderer.initGameView(boardMatrix, brick);
        gameLoopManager.initGameLoop();
    }

    private void onGameTick() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
    }

    private void refreshBrick(ViewData brick) {
        if (gameLoopManager.isPauseProperty().get() || gameLoopManager.isGameOverProperty().get()) {
            return;
        }
        gameRenderer.refreshBrick(brick);
    }

    public void refreshGameBackground(int[][] board) {
        gameRenderer.refreshGameBackground(board);
    }

    private void moveDown(MoveEvent event) {
        if (gameLoopManager.isPauseProperty().get() || gameLoopManager.isGameOverProperty().get()) {
            return;
        }

        DownData downData = eventListener.onDownEvent(event);
        if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }
        refreshBrick(downData.getViewData());
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString("Score: %d"));
    }

    public void bindLevel(IntegerProperty integerProperty) {
        levelLabel.textProperty().bind(integerProperty.asString("Level: %d"));
    }

    public void gameOver() {
        gameLoopManager.gameOver();
    }

    public void newGame(ActionEvent actionEvent) {
        ViewData initialData = eventListener.createNewGame();
        int[][] initialBoard = eventListener.getBoard();
        gamePanel.requestFocus();
        gameLoopManager.newGame();
        // Refresh the UI before the countdown when the user press play again
        refreshGameBackground(initialBoard);
        refreshBrick(initialData);
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    public void updateLevel(int level) {
        gameLoopManager.updateLevel(level);
    }

    public void showCountdown() {
        gameLoopManager.showCountdown();
    }
}