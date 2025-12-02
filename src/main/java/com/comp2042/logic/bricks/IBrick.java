package com.comp2042.logic.bricks;

import com.comp2042.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 'I' Tetris piece (the long straight bar).
 * <p>
 * This class is a concrete implementation of the {@link Brick} interface.
 * It defines the shape geometry for the Cyan block across its rotation states.
 * </p>
 * <p>
 * The class is marked {@code final} to prevent inheritance, ensuring the
 * geometric definition of an I-Brick remains immutable and consistent.
 * </p>
 */
final class IBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new I-Brick.
     * <p>
     * Initializes the 4x4 matrix states for the standard horizontal and vertical orientations.
     * The value '1' is used to represent the Cyan color code.
     * </p>
     */
    public IBrick() {
        // State 0: Horizontal
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        // State 1: Vertical
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }

    /**
     * Returns the list of rotation states for this brick.
     *
     * <p>
     * <b>Security Note:</b> This method returns a deep copy of the internal matrix list
     * using {@link MatrixOperations#deepCopyList(List)}. This ensures that external
     * modifications to the returned list (e.g., by the Board logic) do not corrupt
     * the original shape definition of the I-Brick.
     * </p>
     *
     * @return a safe copy of the shape matrices.
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}