package com.comp2042.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameOverPanel extends BorderPane {

    private final Button playAgainButton;
    private final Button mainMenuButton;

    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");

        playAgainButton = new Button("Play Again");
        playAgainButton.getStyleClass().add("playAgainButton");

        mainMenuButton = new Button("Main Menu");
        mainMenuButton.getStyleClass().add("mainMenuButton");

        // Use an HBox for side-by-side buttons
        HBox buttonBox = new HBox(20, playAgainButton, mainMenuButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Use a VBox to stack the label and the button box
        VBox centerBox = new VBox(30, gameOverLabel, buttonBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        setCenter(centerBox);
    }

    public void setOnPlayAgain(EventHandler<ActionEvent> handler) {
        playAgainButton.setOnAction(handler);
    }

    public void setOnMainMenu(EventHandler<ActionEvent> handler) {
        mainMenuButton.setOnAction(handler);
    }
}