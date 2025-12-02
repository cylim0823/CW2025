package com.comp2042.managers;

import com.comp2042.util.GameConfiguration;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Manages the core game loop and timing mechanics.
 * <p>
 * This class uses JavaFX {@link Timeline} animations to generate "ticks" at intervals
 * defined by the current game level. It acts as the heartbeat of the game, triggering
 * gravity events that move pieces down.
 * </p>
 * <p>
 * Responsibilities include:
 * <ul>
 * <li>Managing the main game loop timer.</li>
 * <li>Handling start, pause, resume, and stop states.</li>
 * <li>Executing the "Countdown" sequence before a game begins.</li>
 * <li>Adjusting tick speed dynamically based on the level.</li>
 * </ul>
 */
public class GameLoopManager {

    private final BooleanProperty isPause = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);
    private final BooleanProperty isCountingDown = new SimpleBooleanProperty(false);
    private Timeline timeLine;
    private Timeline countdownTimeline;
    private long currentSpeedMillis;
    private final Runnable onTickAction;
    private final Label countdownLabel;

    /**
     * Constructs a new GameLoopManager.
     *
     * @param onTickAction the {@link Runnable} to execute every game tick (typically moving the active piece down).
     * @param countdownLabel the UI label used to display the "3, 2, 1, GO!" sequence.
     */
    public GameLoopManager(Runnable onTickAction, Label countdownLabel) {
        this.onTickAction = onTickAction;
        this.countdownLabel = countdownLabel;
    }

    /**
     * Initializes the game loop with the starting speed defined in {@link GameConfiguration}.
     * The timeline is created but not started until the countdown finishes.
     */
    public void initGameLoop() {
        this.currentSpeedMillis = GameConfiguration.LEVEL_SPEEDS.getFirst();
        this.timeLine = createTimeline(this.currentSpeedMillis);
    }

    /**
     * Creates a new JavaFX Timeline for the specified speed.
     *
     * @param speedMillis interval in milliseconds between ticks.
     * @return a configured Timeline with indefinite cycle count.
     */
    private Timeline createTimeline(long speedMillis) {
        Timeline newTimeline = new Timeline(new KeyFrame(
                Duration.millis(speedMillis),
                ae -> onTickAction.run()
        ));
        newTimeline.setCycleCount(Timeline.INDEFINITE);
        return newTimeline;
    }

    /**
     * Internal method to actually begin the game loop after the countdown is complete.
     */
    private void startGame() {
        isCountingDown.set(false);
        if (timeLine != null) {
            timeLine.play();
        }
    }

    /**
     * Toggles the pause state of the game loop.
     * <p>
     * If the game is currently counting down or already over, this method does nothing.
     * When paused, the timeline stops; when resumed, it continues from where it left off.
     * </p>
     */
    public void togglePause() {
        if (isGameOver.get() || isCountingDown.get()) {
            return;
        }
        isPause.set(!isPause.get());
        if (isPause.get()) {
            if (timeLine != null) timeLine.pause();
        } else {
            if (timeLine != null) timeLine.play();
        }
    }

    /**
     * Stops the game loop permanently and marks the state as Game Over.
     */
    public void gameOver() {
        if (timeLine != null) timeLine.stop();
        isGameOver.setValue(Boolean.TRUE);
    }

    /**
     * Resets the manager state for a new game session.
     * <p>
     * Stops any running timelines, resets flags, sets speed back to Level 1,
     * and initiates the countdown sequence.
     * </p>
     */
    public void newGame() {
        if (timeLine != null) timeLine.stop();
        if (countdownTimeline != null) countdownTimeline.stop();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        updateLevel(1); // Reset speed
        showCountdown(); // Start countdown
    }

    /**
     * Updates the game loop speed based on the current level.
     * <p>
     * It retrieves the target speed from {@link GameConfiguration}. If the speed has changed,
     * the current timeline is stopped and replaced with a new one running at the faster rate.
     * </p>
     *
     * @param level the current game level (1-based index).
     */
    public void updateLevel(int level) {
        int index = level - 1;

        if (index < 0) index = 0;
        if (index >= GameConfiguration.LEVEL_SPEEDS.size()) {
            index = GameConfiguration.LEVEL_SPEEDS.size() - 1;
        }

        long newSpeed = GameConfiguration.LEVEL_SPEEDS.get(index);

        if (this.currentSpeedMillis == newSpeed || timeLine == null) {
            return;
        }

        this.currentSpeedMillis = newSpeed;
        Timeline.Status oldStatus = timeLine.getStatus();
        timeLine.stop();
        this.timeLine = createTimeline(newSpeed);

        // If the game was running (not paused), restart the new timeline immediately
        if (oldStatus == Timeline.Status.RUNNING && !isPause.get()) {
            timeLine.play();
        }
    }

    /**
     * Starts the visual "3, 2, 1, GO!" countdown sequence.
     * <p>
     * This blocks the main game loop from starting until the animation finishes.
     * It uses a secondary {@link Timeline} to update the countdown label every second.
     * </p>
     */
    public void showCountdown() {
        isCountingDown.set(true);
        if (timeLine != null) {
            timeLine.pause();
        }

        IntegerProperty countdown = new SimpleIntegerProperty(3);
        countdownLabel.textProperty().bind(countdown.asString());
        countdownLabel.setVisible(true);
        countdownLabel.toFront();

        countdownTimeline = new Timeline(
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