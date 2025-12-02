package com.comp2042.model;

import java.util.Arrays;

/**
 * Represents a snapshot of the game state at a specific point in time.
 * <p>
 * <b>Design Pattern: Memento</b><br>
 * This class acts as the <b>Memento</b> in the pattern. It stores the internal state
 * of the game (the grid, score, and level) so that the {@link com.comp2042.logic.GameHistory} (Caretaker)
 * can save it and the {@link com.comp2042.controllers.GameController} (Originator) can restore it later.
 * </p>
 * <p>
 * <b>Immutability:</b> This class is immutable. The constructor performs a deep copy
 * of the board matrix to ensure that subsequent changes to the active game board
 * do not corrupt the saved history states.
 * </p>
 */
public final class BoardMemento {

    private final int[][] boardState;
    private final int score;
    private final int level;

    /**
     * Creates a new Memento containing a deep copy of the current game state.
     *
     * @param board the source 2D array representing the grid (values are copied, not referenced).
     * @param score the current score at the time of saving.
     * @param level the current level at the time of saving.
     */
    public BoardMemento(int[][] board, int score, int level) {
        this.score = score;
        this.level = level;

        // Deep copy the 2D array to ensure immutability
        this.boardState = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            this.boardState[i] = Arrays.copyOf(board[i], board[i].length);
        }
    }

    /**
     * Retrieves a safe copy of the saved board matrix.
     *
     * @return a new 2D integer array containing the grid state.
     * Modifying this return value will not affect the stored memento.
     */
    public int[][] getBoardState() {
        int[][] copy = new int[boardState.length][];
        for (int i = 0; i < boardState.length; i++) {
            copy[i] = Arrays.copyOf(boardState[i], boardState[i].length);
        }
        return copy;
    }

    /**
     * Retrieves the score saved in this snapshot.
     * @return the score integer.
     */
    public int getScore() {
        return score;
    }

    /**
     * Retrieves the level saved in this snapshot.
     * @return the level integer.
     */
    public int getLevel() {
        return level;
    }
}