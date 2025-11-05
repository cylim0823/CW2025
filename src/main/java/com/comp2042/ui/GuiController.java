package com.comp2042.ui;

import com.comp2042.logic.InputEventListener;
import com.comp2042.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
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
    private Timeline timeLine;
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    private Label pauseLabel;
    private final BooleanProperty isCountingDown = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getResourceAsStream("/digital.ttf"), 38);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("gameOverStyle");
        pauseLabel.setVisible(false);
        VBox notificationVBox = (VBox) groupNotification.getChildren().get(0);
        notificationVBox.getChildren().add(pauseLabel);

        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.P) {
                    togglePause();
                    keyEvent.consume();
                }

                if (isCountingDown.get()) {
                    keyEvent.consume();
                    return;
                }

                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
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
        this.gameRenderer = new GameRenderer(gamePanel, brickPanel, nextBrickPanel, holdBrickPanel);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameRenderer.initGameView(boardMatrix, brick);
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
    }

    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            gameRenderer.refreshBrick(brick);
        }
    }

    public void refreshGameBackground(int[][] board) {
            gameRenderer.refreshGameBackground(board);
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
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
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        groupNotification.toFront();
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        updateLevel(1);
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        showCountdown();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    private void togglePause() {
        if (isGameOver.get()) {
            return;
        }
        isPause.set(!isPause.get());
        if (isPause.get()) {
            timeLine.pause();
            pauseLabel.setVisible(true);
        } else {
            timeLine.play();
            pauseLabel.setVisible(false);
        }
    }

    public void updateLevel(int level) {
        long newSpeed;
        switch (level) {
            case 1: newSpeed = 400; break;
            case 2: newSpeed = 360; break;
            case 3: newSpeed = 320; break;
            case 4: newSpeed = 280; break;
            case 5: newSpeed = 240; break;
            case 6: newSpeed = 200; break;
            case 7: newSpeed = 160; break;
            case 8: newSpeed = 120; break;
            case 9: newSpeed = 100; break;
            default: newSpeed = 80; break;
        }

        timeLine.stop();
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(newSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    public void startGame() {
        isCountingDown.set(false);
        if (timeLine != null) {
            timeLine.play();
            gamePanel.requestFocus();
        }
    }

    public void showCountdown() {
        isCountingDown.set(true);
        if (timeLine != null) {
            timeLine.pause();
        }
        IntegerProperty countdown = new SimpleIntegerProperty(3);
        countdownLabel.textProperty().bind(countdown.asString());
        countdownLabel.setVisible(true);
        countdownLabel.toFront();
        Timeline countdownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> countdown.set(countdown.get() - 1))
        );
        countdownTimeline.setCycleCount(3);
        countdownTimeline.setOnFinished(e -> {
            countdownLabel.textProperty().unbind();
            countdownLabel.setText("GO!");
            PauseTransition goPause = new PauseTransition(Duration.seconds(1));
            goPause.setOnFinished(event -> {
                countdownLabel.setVisible(false);
                startGame();
            });
            goPause.play();
        });
        countdownTimeline.play();
    }
}