package com.comp2042.logic.mode;

import com.comp2042.controllers.GameController;

/**
 * Defines the contract for different game strategies (modes) within the application.
 * <p>
 * This interface is the core of the <b>Strategy Design Pattern</b>. By implementing this interface,
 * different classes (e.g., {@link NormalMode}, {@link ZenMode}) can define their own set of rules
 * for scoring, leveling, and game-over behaviors.
 * </p>
 * <p>
 * <b>Architectural Benefit:</b> This adheres to the <b>Open/Closed Principle</b>. New game modes
 * (like "Time Attack" or "Hardcore") can be added by creating new implementations of this interface
 * without modifying the existing {@link GameController} logic.
 * </p>
 *
 * @author Chen Yu
 * @version 1.0
 */
public interface GameMode {

    /**
     * Determines if the current game session should be recorded in the high score file.
     *
     * @return true if the score should be saved (e.g., Normal Mode); false for practice modes.
     */
    boolean isHighScoreEnabled();

    /**
     * Determines if the game difficulty (speed) should increase as lines are cleared.
     *
     * @return true if leveling is active; false if the speed remains constant (Zen Mode).
     */
    boolean isLevelingEnabled();

    /**
     * Determines if "Danger" visual effects (shaking, intense music) should play
     * when the stack reaches the top of the board.
     *
     * @return true to enable danger effects; false to disable them (for a relaxing experience).
     */
    boolean isDangerAllowed();

    /**
     * Controls the visibility of the Level counter in the UI.
     *
     * @return true to show the label; false to hide it.
     */
    boolean isLevelLabelVisible();

    /**
     * Defines the maximum number of times a player can use the "Undo" feature.
     *
     * @return the number of allowed undos (e.g., 3 for Normal, {@link Integer#MAX_VALUE} for Zen).
     */
    int getUndoLimit();

    /**
     * Defines the behavior when a game-over condition (brick overflow) is met.
     * <p>
     * Strategies implement this to either show the "Game Over" screen or to automatically
     * restart the game for continuous play.
     * </p>
     *
     * @param controller the {@link GameController} instance, used to trigger UI changes or board resets.
     */
    void handleGameOver(GameController controller);
}