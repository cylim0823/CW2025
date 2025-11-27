package com.comp2042.logic;

import com.comp2042.model.BoardMemento;
import com.comp2042.util.GameConfiguration;

import java.util.Stack;

/**
 * This class handles the logic for saving and restoring game states.
 * It uses a Stack to keep track of the board history and makes sure
 * the player doesn't exceed the undo limit.
 */
public class GameHistory {

    private final Stack<BoardMemento> history = new Stack<>();
    private int undoCount = 0;

    /**
     * Saves the current state of the game.
     * undo the most recent locked brick, not rewind the whole game.
     * @param memento The state to be saved.
     */
    public void save(BoardMemento memento) {
        history.clear();
        history.push(memento);
    }

    /**
     * Tries to get the previous game state.
     * It checks if the player has enough "undo lives" left before returning the state.
     * @return The previous state if valid, or null if the stack is empty or limit reached.
     */
    public BoardMemento popState() {
        // Check if user hit the limit
        if (undoCount >= GameConfiguration.MAX_UNDO_PER_GAME) {
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
     * Resets the counter and clears history.
     * Called when the player starts a new game.
     */
    public void reset() {
        undoCount = 0;
        history.clear();
    }

    /**
     * Returns how many times undo has been used.
     * @return current undo count.
     */
    public int getUndoCount() {
        return undoCount;
    }
}