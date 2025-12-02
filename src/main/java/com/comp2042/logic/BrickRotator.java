package com.comp2042.logic;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.model.NextShapeInfo;

/**
 * Manages the rotation state of the active {@link Brick}.
 * <p>
 * This class acts as a state machine for the brick's orientation. It does not handle
 * collision detection or wall kicks directly; instead, it tracks the current rotation index
 * and provides the matrix data for the current and next possible states.
 * </p>
 * <p>
 * It cycles through the list of shape matrices provided by the {@link Brick} interface
 * (typically 0°, 90°, 180°, 270°).
 * </p>
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Calculates the matrix and index for the next clockwise rotation state.
     * <p>
     * This method uses modulo arithmetic to cycle through the available shapes.
     * It is typically called by the {@link com.comp2042.logic.board.Board} to "preview" a rotation
     * and check for collisions before committing the change.
     * </p>
     *
     * @return a {@link NextShapeInfo} object containing the potential next matrix and its index.
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Retrieves the 2D matrix representing the brick's current orientation.
     *
     * @return the 4x4 integer matrix of the active shape.
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Updates the current rotation index to a specific state.
     * <p>
     * This is called by the Board after a successful rotation (and potentially a wall kick)
     * is confirmed to be valid.
     * </p>
     *
     * @param currentShape the new index to set (e.g., 0 for default, 1 for 90°).
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Assigns a new brick to be managed and resets the rotation index to 0 (default state).
     *
     * @param brick the new {@link Brick} instance to control.
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    /**
     * Retrieves the current brick instance being managed.
     *
     * @return the active {@link Brick}.
     */
    public Brick getBrick() {
        return this.brick;
    }
}