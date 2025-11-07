package com.comp2042.ui;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameLoopManager {

    private final BooleanProperty isPause = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);
    private final BooleanProperty isCountingDown = new SimpleBooleanProperty(false);
    private Timeline timeLine;
    private long currentSpeedMillis;
    private final Runnable onTickAction;
    private final Label countdownLabel;
    private final GameOverPanel gameOverPanel;
    private final Group groupNotification;
    private final Label pauseLabel;

    public GameLoopManager(Runnable onTickAction, Label countdownLabel, GameOverPanel gameOverPanel, Group groupNotification) {
        this.onTickAction = onTickAction;
        this.countdownLabel = countdownLabel;
        this.gameOverPanel = gameOverPanel;
        this.groupNotification = groupNotification;
        this.pauseLabel = new Label("PAUSED");
        this.pauseLabel.getStyleClass().add("gameOverStyle");
        this.pauseLabel.setVisible(false);

        try {
            VBox notificationVBox = (VBox) groupNotification.getChildren().get(0);
            notificationVBox.getChildren().add(pauseLabel);
        } catch (Exception e) {
            System.err.println("Warning: Could not find VBox in groupNotification. Adding pauseLabel directly.");
            this.groupNotification.getChildren().add(pauseLabel);
        }
    }

    public void initGameLoop() {
        this.currentSpeedMillis = 400; // Default level 1 speed
        this.timeLine = createTimeline(this.currentSpeedMillis);
    }

    private Timeline createTimeline(long speedMillis) {
        Timeline newTimeline = new Timeline(new KeyFrame(
                Duration.millis(speedMillis),
                ae -> onTickAction.run()
        ));
        newTimeline.setCycleCount(Timeline.INDEFINITE);
        return newTimeline;
    }

    private void startGame() {
        isCountingDown.set(false);
        if (timeLine != null) {
            timeLine.play();
        }
    }

    public void togglePause() {
        if (isGameOver.get() || isCountingDown.get()) {
            return;
        }
        isPause.set(!isPause.get());
        if (isPause.get()) {
            if (timeLine != null) timeLine.pause();
            pauseLabel.setVisible(true);
        } else {
            if (timeLine != null) timeLine.play();
            pauseLabel.setVisible(false);
        }
    }

    public void gameOver() {
        if (timeLine != null) timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        groupNotification.toFront();
    }

    public void newGame() {
        if (timeLine != null) timeLine.stop();

        gameOverPanel.setVisible(false);
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        pauseLabel.setVisible(false); // Hide pause label

        updateLevel(1); // Reset speed
        showCountdown(); // Start countdown
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

        if (this.currentSpeedMillis == newSpeed || timeLine == null) {
            return;
        }

        this.currentSpeedMillis = newSpeed;
        Timeline.Status oldStatus = timeLine.getStatus();
        timeLine.stop();

        this.timeLine = createTimeline(newSpeed);

        if (oldStatus == Timeline.Status.RUNNING && !isPause.get()) {
            timeLine.play();
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

    public BooleanProperty isPauseProperty() {
        return isPause;
    }

    public BooleanProperty isGameOverProperty() {
        return isGameOver;
    }

    public BooleanProperty isCountingDownProperty() {
        return isCountingDown;
    }
}