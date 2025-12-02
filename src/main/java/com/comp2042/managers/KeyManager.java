package com.comp2042.managers;

import com.comp2042.controllers.GuiController;
import com.comp2042.logic.InputEventListener;
import com.comp2042.model.*;
import com.comp2042.util.EventSource;
import com.comp2042.util.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Manages keyboard input handling for the application.
 * <p>
 * This class acts as an <b>Input Adapter/Controller</b>. It intercepts raw JavaFX {@link KeyEvent}s,
 * determines if they are valid based on the current game state (Running, Paused, Game Over),
 * and maps them to specific logical actions.
 * </p>
 * <p>
 * It routes system-level commands (Pause, Mute, New Game) to the {@link GuiController}
 * and gameplay commands (Move, Rotate, Drop) to the {@link InputEventListener} (the Model).
 * </p>
 */
public class KeyManager {

    private final GuiController guiController;
    private final GameLoopManager gameLoopManager;
    private InputEventListener eventListener;

    /**
     * Constructs a new KeyManager.
     *
     * @param guiController reference to the GUI for UI-level actions (Pause, Restart).
     * @param gameLoopManager reference to check the current game state (Paused, Countdown).
     */
    public KeyManager(GuiController guiController, GameLoopManager gameLoopManager) {
        this.guiController = guiController;
        this.gameLoopManager = gameLoopManager;
    }

    /**
     * Sets the listener that will receive gameplay-related commands.
     * This is typically the main GameController.
     *
     * @param eventListener the logic component listening for moves.
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * The central handler for all keyboard events.
     * <p>
     * This method executes a hierarchy of checks:
     * <ol>
     * <li>Global Toggles (Pause 'P', Mute 'M') - Always active.</li>
     * <li>State Blocks - Ignores input if counting down.</li>
     * <li>Menu Shortcuts - Allows 'N' for New Game even if Game Over.</li>
     * <li>Gameplay Controls - Maps WASD/Arrows to movement only if the game is running.</li>
     * </ol>
     * </p>
     *
     * @param keyEvent the raw key event from JavaFX.
     */
    public void handleInput(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        // Global Controls
        if (code == KeyCode.P) {
            guiController.togglePause();
            keyEvent.consume();
            return;
        }

        if (code == KeyCode.M){
            guiController.toggleMute();
            keyEvent.consume();
            return;
        }

        // Block Input during Countdown
        if (gameLoopManager.isCountingDownProperty().get()) {
            keyEvent.consume();
            return;
        }

        // Game Over / Pause State Logic
        if (gameLoopManager.isPauseProperty().get() || gameLoopManager.isGameOverProperty().get()) {
            if (code == KeyCode.N) {
                guiController.startNewGame();
            }
            return;
        }

        // Active Gameplay Controls
        if (eventListener != null) {
            switch (code) {
                case LEFT:
                case A:
                    eventListener.onLeftEvent();
                    break;
                case RIGHT:
                case D:
                    eventListener.onRightEvent();
                    break;
                case UP:
                case W:
                    eventListener.onRotateEvent();
                    break;
                case DOWN:
                case S:
                    guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                    break;
                case SPACE:
                    eventListener.onHardDropEvent(new MoveEvent(null, EventSource.USER));
                    break;
                case C:
                    eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
                    break;
                case R:
                    eventListener.onUndoEvent();
                    break;
                case N:
                    guiController.startNewGame();
                    break;
            }
        }
        keyEvent.consume(); // Prevent event bubbling
    }
}