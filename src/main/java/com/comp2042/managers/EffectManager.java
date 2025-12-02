package com.comp2042.managers;

import com.comp2042.util.GameConfiguration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Random;

/**
 * Manages visual special effects for the game board.
 * <p>
 * This class encapsulates the logic for procedural animations, such as the
 * "Screen Shake" effect used during Danger Mode. It manipulates the JavaFX
 * properties (translation) of the target pane to create dynamic visual feedback.
 * </p>
 */
public class EffectManager {

    private final Pane targetPane;
    private final Timeline shakeTimeline;
    private final Random random = new Random();

    /**
     * Constructs a new EffectManager attached to a specific UI pane.
     * <p>
     * Initializes the {@link Timeline} used for the shake animation but does not start it.
     * The animation interval is defined in {@link GameConfiguration}.
     * </p>
     *
     * @param targetPane the JavaFX Pane (e.g., the Game Panel) to apply effects to.
     */
    public EffectManager(Pane targetPane) {
        this.targetPane = targetPane;

        this.shakeTimeline = new Timeline(new KeyFrame(
                Duration.millis(GameConfiguration.SHAKE_DURATION_MS),
                e -> performShake()
        ));
        this.shakeTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Toggles the "Danger" visual effect (screen shake).
     * <p>
     * This method is triggered by the {@link com.comp2042.controllers.GuiController}
     * when the game state changes to or from a danger state.
     * </p>
     *
     * @param isDanger true to start the shaking effect; false to stop it and reset position.
     */
    public void setDangerMode(boolean isDanger) {
        if (isDanger) {
            startShake();
        } else {
            stopShake();
        }
    }

    /**
     * Starts the shake animation loop if it is not already running.
     */
    private void startShake() {
        if (shakeTimeline.getStatus() != Timeline.Status.RUNNING) {
            shakeTimeline.play();
        }
    }

    /**
     * Stops the shake animation and resets the pane to its original position.
     * <p>
     * Reseting translation (X=0, Y=0) is critical to prevent the board from getting
     * "stuck" in an offset position when the effect ends.
     * </p>
     */
    private void stopShake() {
        shakeTimeline.stop();
        targetPane.setTranslateX(0);
        targetPane.setTranslateY(0);
    }

    /**
     * Executes a single "shake" step.
     * <p>
     * randomly offsets the target pane's X and Y coordinates within the range
     * defined by {@link GameConfiguration#SHAKE_INTENSITY}. This is called repeatedly
     * by the timeline to simulate vibration.
     * </p>
     */
    private void performShake() {
        double intensity = GameConfiguration.SHAKE_INTENSITY;

        // Randomly move X and Y by a small amount (+/- intensity)
        double xOffset = (random.nextDouble() * intensity * 2) - intensity;
        double yOffset = (random.nextDouble() * intensity * 2) - intensity;

        targetPane.setTranslateX(xOffset);
        targetPane.setTranslateY(yOffset);
    }
}