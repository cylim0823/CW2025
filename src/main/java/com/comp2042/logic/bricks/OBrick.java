package com.comp2042.logic.bricks;

import com.comp2042.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 'O' Tetris piece (the Square).
 * <p>
 * This class is a concrete implementation of the {@link Brick} interface.
 * It defines the shape geometry for the Yellow block (ID 4).
 * </p>
 * <p>
 * <b>Unique Characteristic:</b> The O-Brick possesses rotational symmetry.
 * Consequently, this class defines only a single geometric state, as rotating
 * a square results in no visual or grid-based change.
 * </p>
 */
final class OBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new O-Brick.
     * <p>
     * Initializes the single 4x4 matrix state required for this piece.
     * The value '4' is used to represent the Yellow color code.
     * </p>
     */
    public OBrick() {
        // State 0: The Square (No rotation changes this)
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns the list of rotation states for this brick.
     *
     * <p>
     * <b>Optimization:</b> Returns a list containing only one matrix.
     * The {@link com.comp2042.logic.BrickRotator} handles the rotation logic by
     * wrapping the state index (index % size), so a size of 1 effectively
     * disables rotation for this piece.
     * </p>
     *
     * @return a safe, deep copy of the shape matrix list.
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}