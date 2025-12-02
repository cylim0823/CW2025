package com.comp2042.logic;

import com.comp2042.model.BoardMemento;

import java.util.Stack;

/**
 * Manages the history of game states to support the "Undo" feature.
 * <p>
 * <b>Design Pattern: Memento (Caretaker)</b><br>
 * This class acts as the Caretaker. It stores and retrieves {@link BoardMemento} objects,
 * which contain the snapshot of the board, score, and level.
 * </p>
 * <p>
 * It enforces the undo limits defined by the active {@link com.comp2042.logic.mode.GameMode},
 * ensuring that players in Normal Mode cannot exceed the allowed number of reverts.
 * </p>
 */
public class GameHistory {

    private final Stack<BoardMemento> history = new Stack<>();
    private int undoCount = 0;

    /**
     * Captures and stores a snapshot of the current game state.
     * <p>
     * This is typically called immediately before a piece locks into place,
     * creating a save point that the player can revert to.
     * </p>
     *
     * @param memento the state object containing the board matrix and score.
     */
    public void save(BoardMemento memento) {
        history.clear();
        history.push(memento);
    }

    /**
     * Attempts to revert the game to the previous state.
     * <p>
     * This method validates the request against the current game mode's rules.
     * If the player has used up their allowance (e.g., 3 times in Normal Mode),
     * the request is denied.
     * </p>
     *
     * @param maxAllowed the maximum number of undos permitted by the current strategy.
     * @return the previous {@link BoardMemento} if successful; {@code null} if the limit is reached or history is empty.
     */
    public BoardMemento popState(int maxAllowed) {
        if (undoCount >= maxAllowed) {
            return null;
        }

        // Check if there is anything to undo (prevents spamming R)
        if (history.isEmpty()) {
            return null;
        }

        undoCount++;
        return history.pop();
    }

    /**
     * Clears the undo history and resets the usage counter.
     * <p>
     * Called when a new game starts or when the board is wiped (e.g., in Zen Mode restart),
     * as previous states are no longer valid for the new board.
     * </p>
     */
    public void reset() {
        undoCount = 0;
        history.clear();
    }

    /**
     * Retrieves the number of times the player has successfully used the undo feature.
     *
     * @return the current usage count.
     */
    public int getUndoCount() {
        return undoCount;
    }
}