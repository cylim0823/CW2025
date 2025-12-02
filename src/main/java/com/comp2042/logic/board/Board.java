package com.comp2042.logic.board;

import com.comp2042.model.ClearRow;
import com.comp2042.model.ViewData;

/**
 * Defines the contract for the Tetris game board logic.
 * <p>
 * This interface abstracts the core mechanics of the game grid, including:
 * <ul>
 * <li>Collision detection and brick movement</li>
 * <li>Row clearing logic</li>
 * <li>State management (creating new games, restoring history)</li>
 * <li>Data retrieval for rendering</li>
 * </ul>
 * <p>
 * By using this interface, the {@link com.comp2042.controllers.GameController} remains decoupled
 * from the specific board implementation (e.g., {@link SimpleBoard}), adhering to the Dependency Inversion Principle.
 */
public interface Board {

    /**
     * Attempts to move the active brick down by one row.
     *
     * @return true if the move was successful; false if the brick collided with the bottom or another block.
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the active brick one column to the left.
     *
     * @return true if successful; false if blocked by a wall or stack.
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the active brick one column to the right.
     *
     * @return true if successful; false if blocked by a wall or stack.
     */
    boolean moveBrickRight();

    /**
     * Attempts to rotate the active brick counter-clockwise.
     * Implements wall-kick logic to adjust position if the rotation initially fails.
     *
     * @return true if the rotation (and potential wall-kick) was successful.
     */
    boolean rotateLeftBrick();

    /**
     * Spawns a new random brick at the top of the board.
     *
     * @return true if the new brick immediately collides with existing blocks (Game Over condition); false otherwise.
     */
    boolean createNewBrick();

    /**
     * Retrieves the raw 2D array representing the locked blocks on the board.
     * Used by observers to render the background.
     *
     * @return a 2D integer array where values represent color codes (0 is empty).
     */
    int[][] getBoardMatrix();

    /**
     * Captures a snapshot of the current board state for the View.
     * This includes the active brick, ghost piece position, hold brick, and next bricks.
     *
     * @return a {@link ViewData} object containing all necessary rendering information.
     */
    ViewData getViewData();

    /**
     * Locks the current active brick into the board matrix.
     * Called when a brick lands and can no longer move.
     */
    void mergeBrickToBackground();

    /**
     * Scans the board for full rows, removes them, and shifts blocks down.
     *
     * @return a {@link ClearRow} object containing the number of cleared lines and the updated matrix.
     */
    ClearRow clearRows();

    /**
     * Resets the board state for a fresh game.
     * Clears the matrix, hold piece, and history.
     */
    void newGame();

    /**
     * Instantly drops the current brick to the lowest valid position.
     *
     * @return the number of rows the brick dropped (used for scoring).
     */
    int hardDrop();

    /**
     * Swaps the current brick with the held brick.
     *
     * @return true if the swap resulted in a Game Over (spawn collision); false otherwise.
     */
    boolean holdCurrentBrick();

    /**
     * Restores the board matrix to a previous state.
     * Part of the Memento Pattern for the Undo functionality.
     *
     * @param savedGrid the 2D array from a {@link com.comp2042.model.BoardMemento}.
     */
    void restoreState(int[][] savedGrid);

    /**
     * Resets the position of the current brick to the spawn point.
     * Used during Zen Mode board clears to maintain continuity.
     */
    void resetCurrentBrick();

    /**
     * Checks if any blocks have reached the "Danger Zone" (near the top of the board).
     * Used to trigger visual and audio warnings.
     *
     * @return true if the stack height is critical.
     */
    boolean isDangerState();
}