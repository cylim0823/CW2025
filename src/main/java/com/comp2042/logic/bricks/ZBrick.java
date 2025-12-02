package com.comp2042.logic.bricks;

import com.comp2042.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 'Z' Tetris piece (Z-shaped tetromino).
 * <p>
 * This class is a concrete implementation of the {@link Brick} interface.
 * It defines the shape geometry for the Red/Burlywood block (ID 7) across its rotation states.
 * </p>
 * <p>
 * <b>Orientation Note:</b> Similar to the S-Brick, the Z-Brick has 2 distinct visual states.
 * This class initializes those two states (Horizontal and Vertical), allowing the rotation logic
 * to toggle between them.
 * </p>
 */
final class ZBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new Z-Brick.
     * <p>
     * Initializes the 4x4 matrix states for horizontal and vertical orientations.
     * The value '7' is used to represent the color code for this specific brick type.
     * </p>
     */
    public ZBrick() {
        // State 0: Horizontal Z
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {7, 7, 0, 0},
                {0, 7, 7, 0},
                {0, 0, 0, 0}
        });
        // State 1: Vertical Z
        brickMatrix.add(new int[][]{
                {0, 7, 0, 0},
                {7, 7, 0, 0},
                {7, 0, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns the list of rotation states for this brick.
     *
     * <p>
     * <b>Encapsulation Note:</b> This method uses {@link MatrixOperations#deepCopyList(List)}
     * to return a defensive copy. This prevents client code (such as the Board logic)
     * from accidentally modifying the master shape definition of the Z-Brick.
     * </p>
     *
     * @return a safe, deep copy of the shape matrices.
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}