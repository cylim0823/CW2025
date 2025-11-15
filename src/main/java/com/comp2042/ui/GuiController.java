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
import javafx.scene.layout.StackPane;
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
    private GridPane brickPanel;
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
    @FXML
    private StackPane rootPane;
    @FXML
    private VBox pausePane;
    @FXML
    private VBox gameOverPane;

    private InputEventListener eventListener;
    private GameRenderer gameRenderer;
    private GameLoopManager gameLoopManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getResourceAsStream("/digital.ttf"), 38);

        rootPane.requestFocus();

        this.gameRenderer = new GameRenderer(gamePanel, nextBrickPanel, holdBrickPanel);
        this.gameLoopManager = new GameLoopManager(this::onGameTick, countdownLabel);

        rootPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.P) {
                    handlePauseButton(null);
                    keyEvent.consume();
                }

                if (gameLoopManager.isCountingDownProperty().get()) {
                    keyEvent.consume();
                    return;
                }

                if (gameLoopManager.isPauseProperty().get() || gameLoopManager.isGameOverProperty().get()) {
                    // Check for 'New Game' key
                    if (keyEvent.getCode() == KeyCode.N) {
                        handleNewGameButton(null);
                    }
                    return;
                }

                // --- This block will not run if game is over ---
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
                    DownData downData = eventListener.onHardDropEvent(new MoveEvent(null, EventSource.USER));

                    refreshBrick(downData.getViewData());
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.C) {
                    refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    handleNewGameButton(null);
                }
            }
        });

        gameOverPane.setVisible(false);
        pausePane.setVisible(false);

    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameRenderer.initGameView(boardMatrix, brick);
        gameLoopManager.initGameLoop();
        gameLoopManager.showCountdown();
    }

    private void onGameTick() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
    }

    private void refreshBrick(ViewData brick) {
        if (gameLoopManager.isPauseProperty().get()) {
            return;
        }
        gameRenderer.refreshBrick(brick);
    }

    public void refreshGameBackground(int[][] board) {
        gameRenderer.refreshGameBackground(board);
    }

    private void moveDown(MoveEvent event) {
        if (gameLoopManager.isPauseProperty().get()) {
            return;
        }

        DownData downData = eventListener.onDownEvent(event);

        refreshBrick(downData.getViewData());
        rootPane.requestFocus();
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
        gameOverPane.setVisible(true);
        gameOverPane.toFront();
    }

    public void updateLevel(int level) {
        gameLoopManager.updateLevel(level);
    }

    public void showCountdown() {
        gameLoopManager.showCountdown();
    }

    @FXML
    public void handleNewGameButton(ActionEvent actionEvent) {
        ViewData initialData = eventListener.createNewGame();
        int[][] initialBoard = eventListener.getBoard();
        rootPane.requestFocus();

        // Hide overlays
        gameOverPane.setVisible(false);
        pausePane.setVisible(false);

        gameLoopManager.newGame(); // This will start the countdown

        refreshGameBackground(initialBoard);
        refreshBrick(initialData);
    }

    @FXML
    public void handlePauseButton(ActionEvent actionEvent) {
        gameLoopManager.togglePause();
        pausePane.setVisible(gameLoopManager.isPauseProperty().get());
        if (gameLoopManager.isPauseProperty().get()) {
            pausePane.toFront();
        }
        rootPane.requestFocus();
    }

    @FXML
    private void handleExitButton(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void handleMainMenuButton(ActionEvent actionEvent) {
        try {
            // Stop the game loop before going to main menu
            gameLoopManager.gameOver();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent mainMenuRoot = fxmlLoader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(false);
            // Use the main menu's original size
            Scene scene = new Scene(mainMenuRoot, 1000, 700);
            stage.setScene(scene);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void showLineClearNotification(String message) {
        NotificationPanel notifPanel = new NotificationPanel(message);
        // Add the new panel to your main StackPane
        rootPane.getChildren().add(notifPanel);
        notifPanel.showScore(rootPane.getChildren());
    }
}
