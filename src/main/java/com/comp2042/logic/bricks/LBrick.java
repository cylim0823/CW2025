package com.comp2042.logic.bricks;

import com.comp2042.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 'L' Tetris piece (L-shaped tetromino).
 * <p>
 * This class is a concrete implementation of the {@link Brick} interface.
 * It defines the shape geometry for the Dark Green block (ID 3) across its four possible rotation states.
 * </p>
 * <p>
 * The class is marked {@code final} to ensure that the geometric definition
 * of this specific game piece remains immutable and consistent throughout the application lifecycle.
 * </p>
 */
final class LBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new L-Brick.
     * <p>
     * Initializes the 4x4 matrix states for all four rotation orientations (0°, 90°, 180°, 270°).
     * The value '3' is used to represent the color code for this specific brick type.
     * </p>
     */
    public LBrick() {
        // State 0: L shape pointing right
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 3},
                {0, 3, 0, 0},
                {0, 0, 0, 0}
        });
        // State 1: L shape pointing down
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 3, 0},
                {0, 0, 3, 0}
        });
        // State 2: L shape pointing left
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 0, 3, 0},
                {3, 3, 3, 0},
                {0, 0, 0, 0}
        });
        // State 3: L shape pointing up
        brickMatrix.add(new int[][]{
                {0, 3, 0, 0},
                {0, 3, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns the list of rotation states for this brick.
     *
     * <p>
     * <b>Encapsulation Note:</b> This method uses {@link MatrixOperations#deepCopyList(List)}
     * to return a defensive copy. This prevents client code (such as the Board logic)
     * from accidentally modifying the master shape definition of the L-Brick.
     * </p>
     *
     * @return a safe, deep copy of the shape matrices.
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}