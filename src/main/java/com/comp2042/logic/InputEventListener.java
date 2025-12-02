package com.comp2042.logic;

import com.comp2042.model.MoveEvent;

/**
 * Defines the contract for handling user inputs and game state triggers.
 * <p>
 * <b>Design Pattern: Observer / Listener</b><br>
 * This interface decouples the Input mechanism (Keys, GUI buttons) from the Game Logic.
 * The {@link com.comp2042.managers.KeyManager} triggers these events, and the
 * {@link com.comp2042.controllers.GameController} implements them to update the game state.
 * </p>
 * <p>
 * This abstraction allows the input system to be swapped or modified (e.g., adding joystick support)
 * without changing a single line of code in the core game logic.
 * </p>
 */
public interface InputEventListener {

    /**
     * Triggered when a "Down" command is received.
     * This could be from the user pressing the down arrow (Soft Drop)
     * or the game loop timer tick (Gravity).
     *
     * @param event contains metadata about the source of the movement.
     */
    void onDownEvent(MoveEvent event);

    /**
     * Triggered when the user attempts to move the active piece to the left.
     */
    void onLeftEvent();

    /**
     * Triggered when the user attempts to move the active piece to the right.
     */
    void onRightEvent();

    /**
     * Triggered when the user attempts to rotate the active piece.
     * Standard rotation is counter-clockwise.
     */
    void onRotateEvent();

    /**
     * Triggered when the user presses the "Hard Drop" key (Spacebar).
     * Instantly drops the piece to the lowest valid position.
     *
     * @param event contains event source data.
     */
    void onHardDropEvent(MoveEvent event);

    /**
     * Triggered when the user presses the "Hold" key.
     * Swaps the current falling piece with the piece in the hold slot.
     *
     * @param event contains event source data.
     */
    void onHoldEvent(MoveEvent event);

    /**
     * Signals that a new game session should start.
     * Resets the board, score, level, and history.
     */
    void createNewGame();

    /**
     * Retrieves the current board state matrix.
     * <p>
     * While primarily an input interface, this method allows the Input/UI layer
     * to obtain the necessary data structure for initialization without coupling
     * to the concrete Controller class.
     * </p>
     *
     * @return the 2D integer array representing the grid.
     */
    int[][] getBoard();

    /**
     * Triggered when the user requests to "Undo" the last move.
     * Reverts the game state using the Memento pattern.
     */
    void onUndoEvent();
}