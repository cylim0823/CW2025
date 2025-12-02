package com.comp2042.model;

import com.comp2042.util.MatrixOperations;

/**
 * An immutable data object representing the potential next state of a brick rotation.
 * <p>
 * This class is used primarily by the {@link com.comp2042.logic.BrickRotator} to package
 * the calculated matrix and rotation index for a proposed move. The {@link com.comp2042.logic.board.Board}
 * logic uses this information to check for collisions (including wall kicks) before
 * committing the rotation to the active game state.
 * </p>
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a new container for rotation info.
     *
     * @param shape the 4x4 integer matrix representing the new orientation.
     * @param position the index of the rotation state (0-3).
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Retrieves the matrix for the proposed shape.
     * <p>
     * <b>Security Note:</b> This method returns a deep copy of the matrix using
     * {@link MatrixOperations#copy(int[][])}. This defensive copying ensures that
     * external collision checks cannot accidentally modify the source data defined
     * in the Brick classes.
     * </p>
     *
     * @return a safe copy of the 2D integer array.
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Retrieves the index of the rotation state.
     *
     * @return the integer representing the orientation (e.g., 0 for default, 1 for 90° clockwise).
     */
    public int getPosition() {
        return position;
    }
}