package com.comp2042.model;

public interface GameObserver {
    /**
     * Called when the falling tetris brick moves or rotates
     * @param viewData The data needed to draw the current piece.
     */
    void onBoardUpdated(ViewData viewData);

    /**
     * Called when the static background grid changes (when a row is cleared)
     * @param boardMatrix The 2D array representing the board colors.
     */
    void onGameBackgroundUpdated(int[][] boardMatrix);

    /**
     * Called when the score changes.
     * @param score The new score value.
     */
    void onScoreUpdated(int score);

    /**
     * Called when the level increases.
     * @param level The new level.
     */
    void onLevelUpdated(int level);

    /**
     * Called when rows are cleared (Single, Double, Tetris!).
     * @param message The notification text to display.
     */
    void onLineCleared(String message);

    /**
     * Called when the game ends.
     */
    void onGameOver();
}