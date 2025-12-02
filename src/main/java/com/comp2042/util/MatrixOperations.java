package com.comp2042.util;

import com.comp2042.model.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A stateless utility class providing core matrix manipulation algorithms for the game.
 * <p>
 * This class encapsulates the low-level mathematical operations required for a grid-based game,
 * including:
 * <ul>
 * <li><b>Collision Detection:</b> Checking if two matrices overlap ({@link #intersect}).</li>
 * <li><b>State Merging:</b> Locking a brick into the background grid ({@link #merge}).</li>
 * <li><b>Row Logic:</b> Detecting and clearing full rows ({@link #checkRemoving}).</li>
 * <li><b>Deep Copying:</b> Ensuring data integrity for immutable objects.</li>
 * </ul>
 * </p>
 * <p>
 * By separating these algorithms from {@link com.comp2042.logic.board.SimpleBoard}, the code adheres
 * to the Single Responsibility Principle: the Board handles game rules, while this class handles the math.
 * </p>
 */
public class MatrixOperations {

    /**
     * Checks if a brick collides with the board boundaries or existing blocks.
     * <p>
     * This method iterates through the non-zero cells of the brick matrix and verifies
     * if the corresponding target cells on the main board are occupied or out of bounds.
     * </p>
     *
     * @param matrix the background board grid.
     * @param brick the 4x4 matrix of the falling piece.
     * @param x the top-left X coordinate of the brick.
     * @param y the top-left Y coordinate of the brick.
     * @return true if a collision is detected; false otherwise.
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + j;
                int targetY = y + i;
                if (brick[i][j] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method to validate grid coordinates.
     *
     * @param matrix the board grid to check against.
     * @param targetX the column index to validate.
     * @param targetY the row index to validate.
     * @return true if the coordinates are outside the valid grid dimensions.
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        // Is the Y-coordinate (row) out of bounds?
        if (targetY < 0 || targetY >= matrix.length) {
            return true;
        }

        // Now that Y is safe, is the X-coordinate (column) out of bounds?
        if (targetX < 0 || targetX >= matrix[targetY].length) {
            return true;
        }
        return false;
    }

    /**
     * Creates a deep copy of a 2D integer array.
     * <p>
     * Used extensively throughout the application to enforce immutability in
     * data transfer objects (like {@link com.comp2042.model.ViewData}).
     * </p>
     *
     * @param original the source matrix.
     * @return a new, independent copy of the matrix.
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick into the background grid.
     * <p>
     * This creates a new board state where the active brick's cells are written
     * permanently onto the background matrix. Used when a piece locks in place.
     * </p>
     *
     * @param filledFields the current background grid.
     * @param brick the matrix of the brick to lock.
     * @param x the x-coordinate of the brick.
     * @param y the y-coordinate of the brick.
     * @return a new 2D array representing the merged state.
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + j;
                int targetY = y + i;
                if (brick[i][j] != 0) {
                    copy[targetY][targetX] = brick[i][j];
                }
            }
        }
        return copy;
    }

    /**
     * Scans the matrix for full rows, removes them, and shifts remaining rows down.
     * <p>
     * This implements the core Tetris line-clearing mechanic. It uses a {@link Deque}
     * to efficiently rebuild the matrix from the bottom up, excluding full rows.
     * </p>
     *
     * @param matrix the board state to check.
     * @return a {@link ClearRow} result containing the cleared line count and the new matrix.
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        return new ClearRow(clearedRows.size(), tmp);
    }

    /**
     * Deep copies a list of 2D arrays.
     * <p>
     * Used by {@link com.comp2042.logic.bricks.Brick} classes to return safe copies
     * of their internal shape data.
     * </p>
     *
     * @param list the source list of matrices.
     * @return a new list containing independent copies of each matrix.
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}