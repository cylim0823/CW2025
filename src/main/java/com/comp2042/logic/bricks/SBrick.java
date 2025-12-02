package com.comp2042.logic.bricks;

import com.comp2042.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 'S' Tetris piece (often called the "Snake" or "Skew").
 * <p>
 * This class is a concrete implementation of the {@link Brick} interface.
 * It defines the shape geometry for the Red block (ID 5).
 * </p>
 * <p>
 * <b>Orientation Note:</b> The S-Brick has 2 distinct visual states.
 * This class initializes those two states, allowing the {@link com.comp2042.logic.BrickRotator}
 * to toggle between them during gameplay.
 * </p>
 */
final class SBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new S-Brick.
     * <p>
     * Initializes the 4x4 matrix states for horizontal and vertical orientations.
     * The value '5' is used to represent the Red color code.
     * </p>
     */
    public SBrick() {
        // State 0: Horizontal S
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0}
        });
        // State 1: Vertical S
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns the list of rotation states for this brick.
     *
     * <p>
     * <b>Encapsulation Note:</b> This method uses {@link MatrixOperations#deepCopyList(List)}
     * to return a defensive copy. This prevents client code from accidentally
     * modifying the master shape definition of the S-Brick.
     * </p>
     *
     * @return a safe, deep copy of the shape matrices.
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}