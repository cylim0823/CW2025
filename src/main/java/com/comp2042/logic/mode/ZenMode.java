package com.comp2042.logic.mode;

import com.comp2042.controllers.GameController;

/**
 * Implements the "Zen" ruleset for a relaxed, infinite gameplay experience.
 * <p>
 * This strategy alters the core game mechanics to remove stress and punishment:
 * <ul>
 * <li>High scores and levels are disabled.</li>
 * <li>Game speed remains constant (no leveling up).</li>
 * <li>"Danger" effects (shaking, intense music) are suppressed.</li>
 * <li>The player has infinite "Undos" to practice placement.</li>
 * <li>Game Over results in an immediate board reset rather than a termination screen.</li>
 * </ul>
 * </p>
 */
public class ZenMode implements GameMode {

    /**
     * {@inheritDoc}
     * @return false, as Zen mode is for practice and does not track competitive records.
     */
    @Override
    public boolean isHighScoreEnabled() { return false; }

    /**
     * {@inheritDoc}
     * @return false, ensuring the game speed remains constant for a relaxing experience.
     */
    @Override
    public boolean isLevelingEnabled() { return false; }

    /**
     * {@inheritDoc}
     * @return false, suppressing stress-inducing audio and visual warnings.
     */
    @Override
    public boolean isDangerAllowed() { return false; }

    /**
     * {@inheritDoc}
     * @return false, hiding the level counter since progression is disabled.
     */
    @Override
    public boolean isLevelLabelVisible() { return false; }

    /**
     * Returns an effectively infinite undo limit.
     * <p>
     * This allows players to experiment with brick placement and correct mistakes
     * indefinitely without penalty.
     * </p>
     *
     * @return {@link Integer#MAX_VALUE}.
     */
    @Override
    public int getUndoLimit(){
        return Integer.MAX_VALUE;  // Infinite undos
    }

    /**
     * Triggers the Zen Mode "Auto-Restart" sequence.
     * <p>
     * Instead of showing a Game Over screen, this strategy commands the controller
     * to immediately clear the board and start a fresh game, creating a seamless loop.
     * </p>
     *
     * @param gameController the controller used to reset the game state.
     */
    @Override
    public void handleGameOver(GameController gameController) {
        // Zen behavior: Auto-restart immediately
        gameController.createNewGame();
    }
}