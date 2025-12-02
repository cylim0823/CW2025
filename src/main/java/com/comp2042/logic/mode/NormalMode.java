package com.comp2042.logic.mode;

import com.comp2042.controllers.GameController;
import com.comp2042.util.GameConfiguration;

/**
 * Implements the standard, competitive rules for the Tetris game.
 * <p>
 * This strategy defines the default gameplay experience where:
 * <ul>
 * <li>High scores are tracked and saved.</li>
 * <li>The game speed increases as levels progress.</li>
 * <li>Visual danger effects are enabled to increase tension.</li>
 * <li>The "Undo" feature is strictly limited to preserve difficulty.</li>
 * </ul>
 * </p>
 */
public class NormalMode implements GameMode {

    /**
     * {@inheritDoc}
     * @return true, as competitive play requires tracking high scores.
     */
    @Override
    public boolean isHighScoreEnabled() { return true; }

    /**
     * {@inheritDoc}
     * @return true, allowing the game speed to increase as the player clears lines.
     */
    @Override
    public boolean isLevelingEnabled() { return true; }

    /**
     * {@inheritDoc}
     * @return true, enabling visual shake and audio effects when the board is full.
     */
    @Override
    public boolean isDangerAllowed() { return true; }

    /**
     * {@inheritDoc}
     * @return true, displaying the current level to the player.
     */
    @Override
    public boolean isLevelLabelVisible() { return true; }

    /**
     * Returns the strict undo limit defined in the global configuration.
     * <p>
     * This limits the number of times a player can correct mistakes, ensuring
     * the game remains challenging.
     * </p>
     *
     * @return {@link GameConfiguration#UNDO_LIMIT_NORMAL} (typically 3).
     */
    @Override
    public int getUndoLimit(){
        return GameConfiguration.UNDO_LIMIT_NORMAL;
    }

    /**
     * Triggers the standard "Game Over" sequence.
     * <p>
     * Instead of restarting, this commands the controller to stop the game loop,
     * save the score, and display the Game Over UI panel.
     * </p>
     *
     * @param gameController the controller to notify.
     */
    @Override
    public void handleGameOver(GameController gameController) {
        // Normal behavior: Show the Game Over screen
        gameController.notifyGameOver();
    }
}