package com.comp2042.model;

/**
 * Defines the contract for components that wish to listen to game events.
 * <p>
 * <b>Design Pattern: Observer</b><br>
 * This interface facilitates loose coupling between the Game Logic (Subject) and the
 * UI/Sound systems (Observers). The {@link com.comp2042.controllers.GameController} notifies
 * all registered observers whenever the game state changes, without needing to know
 * the specific implementation details of the listeners.
 * </p>
 */
public interface GameObserver {

    /**
     * Triggered when the active falling brick moves, rotates, or spawns.
     * <p>
     * Used by the View to redraw the dynamic elements of the board (active piece, ghost piece).
     * </p>
     *
     * @param viewData a snapshot containing the current piece coordinates and shape.
     */
    void onBoardUpdated(ViewData viewData);

    /**
     * Triggered when the static background grid changes (e.g., when a piece locks or rows are cleared).
     * <p>
     * Used by the View to repaint the locked blocks on the grid.
     * </p>
     *
     * @param boardMatrix the 2D array representing the color codes of locked blocks.
     */
    void onGameBackgroundUpdated(int[][] boardMatrix);

    /**
     * Triggered when the player's score increases.
     *
     * @param score the new total score.
     */
    void onScoreUpdated(int score);

    /**
     * Triggered when the player advances to a new level.
     * This usually implies an increase in game speed.
     *
     * @param level the new level number.
     */
    void onLevelUpdated(int level);

    /**
     * Triggered when one or more rows are cleared.
     * <p>
     * Used to display visual notifications (e.g., "TETRIS!") or play celebration sounds.
     * </p>
     *
     * @param lines the number of lines cleared in this event.
     * @param message the notification text associated with the clear (e.g., "Single", "Double").
     */
    void onLineCleared(int lines, String message);

    /**
     * Triggered when the game ends (Game Over).
     * Used to stop music, show the game-over screen, and display final scores.
     */
    void onGameOver();

    /**
     * Triggered when a brick lands (soft drop or hard drop).
     * Primarily used by the audio system to play a landing sound effect.
     */
    void onBrickDropped();

    /**
     * Triggered when the "Danger" state changes.
     * <p>
     * The game enters a danger state when the stack reaches a critical height.
     * Observers can use this to play intense music or shake the screen.
     * </p>
     *
     * @param isDanger true if the game is in a critical state; false otherwise.
     */
    void onDangerStateChanged(boolean isDanger);
}