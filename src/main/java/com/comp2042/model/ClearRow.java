package com.comp2042.model;

import com.comp2042.util.MatrixOperations;

/**
 * An immutable data carrier that represents the result of a row-clearing operation.
 * <p>
 * This class packages two critical pieces of information generated when the board checks for full lines:
 * <ul>
 * <li>The count of lines removed (used by {@link com.comp2042.managers.ScoreManager} for calculating points).</li>
 * <li>The updated board matrix (used to update the game state after rows collapse).</li>
 * </ul>
 * </p>
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;

    /**
     * Constructs a new ClearRow result object.
     *
     * @param linesRemoved the integer count of rows cleared (0 to 4).
     * @param newMatrix the new state of the board grid after clearing and shifting.
     */
    public ClearRow(int linesRemoved, int[][] newMatrix) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
    }

    /**
     * Retrieves the number of lines that were cleared.
     *
     * @return the row count (e.g., 4 for a "Tetris").
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Retrieves the updated grid matrix.
     * <p>
     * <b>Immutability Note:</b> This method returns a deep copy of the matrix using
     * {@link MatrixOperations#copy(int[][])}. This prevents external classes from modifying
     * the internal state of this result object, ensuring data integrity.
     * </p>
     *
     * @return a safe copy of the 2D integer array.
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }
}