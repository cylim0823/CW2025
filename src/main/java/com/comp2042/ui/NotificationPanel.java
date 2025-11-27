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

public class NotificationPanel extends BorderPane {

    private static final double FADE_DURATION = 2000;
    private static final double MOVE_DURATION = 2500;
    private static final double MOVE_Y_AMOUNT = -40;
    private static final double GLOW_INTENSITY = 0.6;
    private static final double PANEL_WIDTH = 220;
    private static final double PANEL_HEIGHT = 200;

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

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(FADE_DURATION), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(MOVE_DURATION), this);
        tt.setToY(this.getLayoutY() + MOVE_Y_AMOUNT);

        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(event -> list.remove(NotificationPanel.this));
        transition.play();
    }
}