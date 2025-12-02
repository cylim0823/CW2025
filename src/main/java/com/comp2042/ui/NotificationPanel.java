package com.comp2042.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * A custom UI component representing a temporary, floating notification.
 * <p>
 * This class extends {@link BorderPane} to display stylized text (e.g., scoring bonuses like "TETRIS!")
 * that appears on the screen, floats upwards, fades out, and then automatically removes itself.
 * </p>
 * <p>
 * <b>Visual Polish:</b> This component adds "Game Feel" by using JavaFX effects (Glow)
 * and parallel animations to provide immediate visual feedback for player actions.
 * </p>
 */
public class NotificationPanel extends BorderPane {

    private static final double FADE_DURATION = 2000;
    private static final double MOVE_DURATION = 2500;
    private static final double MOVE_Y_AMOUNT = -40;
    private static final double GLOW_INTENSITY = 0.6;
    private static final double PANEL_WIDTH = 220;
    private static final double PANEL_HEIGHT = 200;

    /**
     * Constructs a new notification panel.
     * <p>
     * Applies specific CSS styling ("bonusStyle") and visual effects (Glow)
     * to ensure the text stands out against the game background.
     * </p>
     *
     * @param text the message to display (e.g., score value or combo name).
     */
    public NotificationPanel(String text) {
        setMinHeight(PANEL_HEIGHT);
        setMinWidth(PANEL_WIDTH);

        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");

        final Glow glow = new Glow(GLOW_INTENSITY);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);
    }

    /**
     * Triggers the "Float and Fade" animation sequence.
     * <p>
     * This method executes a {@link ParallelTransition} combining:
     * <ul>
     * <li><b>Translation:</b> Moving the text upwards.</li>
     * <li><b>Fade:</b> Changing opacity from 1.0 to 0.0.</li>
     * </ul>
     * </p>
     * <p>
     * <b>Self-Cleanup:</b> Upon animation completion, this component automatically removes
     * itself from the provided parent list to prevent UI clutter and memory leaks.
     * </p>
     *
     * @param list the observable children list of the parent pane (used for self-removal).
     */
    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(FADE_DURATION), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(MOVE_DURATION), this);
        tt.setToY(this.getLayoutY() + MOVE_Y_AMOUNT);

        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);

        // Lambda callback to remove this node from the Scene Graph when finished
        transition.setOnFinished(event -> list.remove(NotificationPanel.this));

        transition.play();
    }
}