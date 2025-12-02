package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Defines the contract for a mechanism that produces {@link Brick} objects.
 * <p>
 * This interface abstracts the logic of how bricks are chosen (e.g., pure random,
 * "7-bag" system, or deterministic for testing). It allows the {@link com.comp2042.logic.board.Board}
 * to request pieces without knowing the underlying generation algorithm.
 * </p>
 */
public interface BrickGenerator {

    /**
     * Retrieves the next brick from the generation queue.
     * <p>
     * Calling this method removes the brick from the front of the queue and
     * typically triggers the generation of a new brick to refill the internal buffer.
     * </p>
     *
     * @return the next {@link Brick} to be placed on the board.
     */
    Brick getBrick();

    /**
     * Provides a look-ahead at the sequence of bricks coming up next.
     * <p>
     * This is primarily used by the {@link com.comp2042.ui.GameRenderer} to draw
     * the "Next Pieces" preview panel, allowing the player to plan their strategy.
     * </p>
     *
     * @return a List of upcoming {@link Brick} objects.
     */
    List<Brick> getUpcomingBricks();
}