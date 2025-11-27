package com.comp2042.managers;

import com.comp2042.util.GameConfiguration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Random;

public class EffectManager {

    private final Pane targetPane;
    private final Timeline shakeTimeline;
    private final Random random = new Random();

    public EffectManager(Pane targetPane) {
        this.targetPane = targetPane;

        this.shakeTimeline = new Timeline(new KeyFrame(
                Duration.millis(GameConfiguration.SHAKE_DURATION_MS),
                e -> performShake()
        ));
        this.shakeTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Activates or Deactivates the Danger Shake.
     */
    public void setDangerMode(boolean isDanger) {
        if (isDanger) {
            startShake();
        } else {
            stopShake();
        }
    }

    private void startShake() {
        if (shakeTimeline.getStatus() != Timeline.Status.RUNNING) {
            shakeTimeline.play();
        }
    }

    private void stopShake() {
        shakeTimeline.stop();
        targetPane.setTranslateX(0);
        targetPane.setTranslateY(0);
    }

    private void performShake() {
        double intensity = GameConfiguration.SHAKE_INTENSITY;

        // Randomly move X and Y by a small amount
        double xOffset = (random.nextDouble() * intensity * 2) - intensity;
        double yOffset = (random.nextDouble() * intensity * 2) - intensity;

        targetPane.setTranslateX(xOffset);
        targetPane.setTranslateY(yOffset);
    }
}