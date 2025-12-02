package com.comp2042.controllers;

import com.comp2042.logic.InputEventListener;
import com.comp2042.logic.mode.GameMode;
import com.comp2042.managers.ScoreManager;
import com.comp2042.managers.ColorManager;
import com.comp2042.managers.GameLoopManager;
import com.comp2042.managers.KeyManager;
import com.comp2042.managers.SoundManager;
import com.comp2042.model.*;
import com.comp2042.ui.*;
import com.comp2042.managers.EffectManager;
import com.comp2042.util.EventSource;
import com.comp2042.util.EventType;
import com.comp2042.util.GameConfiguration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The main GUI controller for the Tetris game.
 *
 * <p>This class acts as the View + Controller in the MVC architecture, handling:
 * <ul>
 *   <li>Rendering the game board, next bricks, and hold panel through {@link GameRenderer}</li>
 *   <li>Receiving user input via {@link KeyManager}</li>
 *   <li>Forwarding input events to the {@link GameController} (the Model)</li>
 *   <li>Updating UI when notified through {@link GameObserver}</li>
 *   <li>Managing UI panels such as pause and game-over screens</li>
 * </ul>
 *
 * <p>Game updates are driven by {@link GameLoopManager}, which generates timed tick events
 * that simulate the falling bricks. The GUI controller listens to these ticks and sends
 * DOWN events to the model.
 * @author Chen Yu
 * @version 1.0
 */
public class GuiController implements Initializable, GameObserver {

    private static final String FONT_PATH = GameConfiguration.FONT_PATH;
    private static final double FONT_SIZE = GameConfiguration.FONT_SIZE_DEFAULT;

    @FXML private GridPane gamePanel;
    @FXML private VBox nextBricksContainer;
    @FXML private GridPane holdBrickPanel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label countdownLabel;
    @FXML private StackPane rootPane;
    @FXML private VBox pausePane;
    @FXML private VBox gameOverPane;

    // High Score Labels
    @FXML private Label gameOverScoreLabel;
    @FXML private Label currentScoreLabel;

    private SoundManager soundManager;
    private GameRenderer gameRenderer;
    private GameLoopManager gameLoopManager;
    private KeyManager keyManager;
    private EffectManager effectManager;
    private GameController gameController;

    private InputEventListener eventListener;
    private int currentScore = 0;

    /**
     * Initializes all UI components, event managers, and the game renderer.
     *
     * <p>This method wires together:
     * <ul>
     *   <li>GameLoopManager – handles the main ticking loop</li>
     *   <li>KeyManager – handles keyboard input</li>
     *   <li>GameRenderer – draws the board and UI elements</li>
     *   <li>SoundManager – plays background music and SFX</li>
     *   <li>GameController – the Model that contains game logic</li>
     * </ul>
     *
     * <p>Observers are registered here so that the GUI receives model updates.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCustomFont();
        rootPane.requestFocus();

        ColorManager colorManager = new ColorManager();
        this.soundManager = new SoundManager();
        this.gameRenderer = new GameRenderer(gamePanel, nextBricksContainer, holdBrickPanel, colorManager);
        this.gameLoopManager = new GameLoopManager(this::onGameTick, countdownLabel);
        this.keyManager = new KeyManager(this, gameLoopManager);
        this.effectManager = new EffectManager(gamePanel);
        this.gameController = new GameController();

        gameController.addObserver(soundManager);
        soundManager.playMusic();

        gameRenderer.initGameView(gameController.getBoard());

        gameLoopManager.initGameLoop();
        gameController.addObserver(this);

        this.setEventListener(gameController);
        rootPane.setOnKeyPressed(keyManager::handleInput);

        resetUIState();
    }

    /**
     * Applies a selected game mode to the model and adjusts UI visibility
     * (such as enabling or disabling the level counter).
     *
     * @param mode the game mode selected by the user
     */
    public void initGameMode(GameMode mode) {
        if (gameController != null) {
            gameController.setGameMode(mode);
        }
        if (levelLabel != null) {
            levelLabel.setVisible(mode.isLevelLabelVisible());
        }
    }

    // Observer methods

    /**
     * Called when the board state changes. Updates the brick positions
     * and active piece on the screen.
     *
     * @param viewData snapshot of the board and active piece for rendering
     */
    @Override
    public void onBoardUpdated(ViewData viewData) {
        gameRenderer.refreshBrick(viewData);
    }

    /**
     * Updates static tile background (ghost blocks, locked bricks, etc).
     *
     * @param boardMatrix the full board matrix of locked tiles
     */
    @Override
    public void onGameBackgroundUpdated(int[][] boardMatrix) {
        gameRenderer.refreshGameBackground(boardMatrix);
    }

    /**
     * Refreshes the score label when the player gains points.
     *
     * @param score the new total score
     */
    @Override
    public void onScoreUpdated(int score) {
        this.currentScore = score;
        Platform.runLater(() -> scoreLabel.setText("Score: " + score));
    }

    /**
     * Updates the level display and increases the game speed.
     *
     * @param level the current game level
     */
    @Override
    public void onLevelUpdated(int level) {
        Platform.runLater(() -> levelLabel.setText("Level: " + level));
        gameLoopManager.updateLevel(level);
    }

    /**
     * Shows a temporary UI notification (e.g., "Double!", "Tetris!")
     * when the model signals a line clear.
     *
     * @param lines number of lines cleared
     * @param message the display text for the combo
     */
    @Override
    public void onLineCleared(int lines, String message) {
        Platform.runLater(() -> showLineClearNotification(message));
    }

    @Override
    public void onBrickDropped() {}

    /**
     * Displays the game-over screen, plays the game-over sound, and
     * loads high score information from the {@link ScoreManager}.
     */
    @Override
    public void onGameOver() {
        Platform.runLater(() -> {
            gameLoopManager.gameOver();
            if (soundManager != null) soundManager.onGameOver();

            ScoreManager sm = new ScoreManager();
            int bestScore = sm.getHighestScore();

            if (gameOverScoreLabel != null) {
                gameOverScoreLabel.setText("Highest Score: " + bestScore);
            }
            if (currentScoreLabel != null) {
                currentScoreLabel.setText("Your Score: " + currentScore);
            }

            gameOverPane.setVisible(true);
            gameOverPane.toFront();
        });
    }

    /**
     * Shows or hides a danger visual effect when the stack is close to the top.
     *
     * @param isDanger true if the board is near overflow
     */
    @Override
    public void onDangerStateChanged(boolean isDanger) {
        // Visual updates must happen on the JavaFX thread
        Platform.runLater(() -> {
            if (effectManager != null) {
                effectManager.setDangerMode(isDanger);
            }
        });
    }

    // Helper methods
    /**
     * Called by the {@link GameLoopManager} at fixed intervals.
     * Pushes a DOWN input event into the model to simulate gravity.
     */
    private void onGameTick() {
        if (eventListener != null) {
            eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        }
    }

    public void moveDown(MoveEvent event) {
        if (gameLoopManager.isPauseProperty().get()) return;
        eventListener.onDownEvent(event);
    }

    /**
     * Resets UI panels, restarts the model state, restarts the game loop,
     * and begins background music.
     */
    public void startNewGame() {
        eventListener.createNewGame();
        resetUIState();
        rootPane.requestFocus();
        gameLoopManager.newGame();

        if (soundManager != null) {
            soundManager.playMusic();
        }
    }

    /**
     * Toggles the pause state and shows/hides the pause panel.
     */
    public void togglePause() {
        gameLoopManager.togglePause();
        boolean isPaused = gameLoopManager.isPauseProperty().get();
        pausePane.setVisible(isPaused);
        if(isPaused) pausePane.toFront();
        rootPane.requestFocus();
    }

    /**
     * Toggles background music and SFX mute state.
     */
    public void toggleMute(){
        if (soundManager != null){
            soundManager.toggleMute();
        }
    }

    /**
     * Sets the listener that should receive keyboard events.
     * This is usually the {@link GameController} (the Model).
     *
     * @param eventListener the input listener to receive key actions
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        if (this.keyManager != null) {
            this.keyManager.setEventListener(eventListener);
        }
    }

    /**
     * Creates and displays a floating score notification effect
     * (e.g., "TETRIS!") on the root pane.
     *
     * @param message the text to display
     */
    public void showLineClearNotification(String message) {
        NotificationPanel notifPanel = new NotificationPanel(message);
        rootPane.getChildren().add(notifPanel);
        notifPanel.showScore(rootPane.getChildren());
    }

    /**
     * Hides pause and game-over UI panels.
     * Called when starting a new game.
     */
    private void resetUIState() {
        gameOverPane.setVisible(false);
        pausePane.setVisible(false);
    }

    /**
     * Loads a custom font file specified in the game configuration.
     * Falls back to default fonts if loading fails.
     */
    private void loadCustomFont() {
        try {
            Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
        } catch (Exception e) {
            System.err.println("Could not load font: " + FONT_PATH);
        }
    }

    // Button handlers

    /** Starts a new game when the New Game button is pressed. */
    @FXML
    public void handleNewGameButton() {
        startNewGame();
    }

    /** Toggles the pause menu from the UI button. */
    @FXML
    public void handlePauseButton() {
        togglePause();
    }

    /**
     * Ends the current game, stops audio, and loads the main menu scene.
     */
    @FXML
    public void handleMainMenuButton() {
        gameLoopManager.gameOver();
        if (soundManager != null){
            soundManager.stopMusic();
        }
        loadScene("fxml/mainMenu.fxml");
    }

    /**
     * Loads another FXML scene and replaces the current root node.
     *
     * @param fxmlFile path to the FXML file inside resources
     */
    private void loadScene(String fxmlFile) {
        try {
            URL url = getClass().getClassLoader().getResource(fxmlFile);
            if (url == null) throw new IOException("File not found: " + fxmlFile);

            FXMLLoader fxmlLoader = new FXMLLoader(url);
            Parent root = fxmlLoader.load();
            Scene currentScene = rootPane.getScene();
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}