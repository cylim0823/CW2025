package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Defines the contract for all Tetris game pieces (Tetrominoes).
 * <p>
 * This interface allows the {@link com.comp2042.logic.board.Board} and
 * {@link com.comp2042.ui.GameRenderer} to handle different shapes uniformly
 * via polymorphism, adhering to the Open/Closed Principle.
 * </p>
 * Implementing classes (e.g., {@link IBrick}, {@link TBrick}) define the specific
 * geometric data for each shape.
 */
public interface Brick {

    /**
     * Retrieves the geometric data for the brick across all possible rotation states.
     *
     * @return a List of 4x4 integer matrices, where each matrix represents
     * one rotation state (0°, 90°, 180°, 270°) of the brick.
     * Values inside the matrix represent the color code of the block.
     */
    List<int[][]> getShapeMatrix();
}