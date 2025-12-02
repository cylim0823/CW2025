package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * A concrete implementation of {@link BrickGenerator} that provides a fair sequence of pieces.
 * <p>
 * <b>Algorithm: The 7-Bag System</b><br>
 * Instead of pure randomness (which could lead to "piece droughts"), this class implements
 * the standard "Bag" randomizer used in modern Tetris games.
 * </p>
 * <ul>
 * <li>A "bag" is filled with one of each of the 7 Tetrominoes.</li>
 * <li>The bag is shuffled.</li>
 * <li>Pieces are drawn from the bag until empty, then a new bag is created.</li>
 * </ul>
 * <p>
 * This ensures that a player will never go more than 12 turns without seeing a specific piece.
 * </p>
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final ArrayList<Brick> bag = new ArrayList<>();
    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private static final int PREVIEW_COUNT = 4;

    /**
     * Constructs a new generator and pre-fills the queue.
     * Ensures that the game starts with a full set of upcoming pieces ready for the View.
     */
    public RandomBrickGenerator() {
        fillQueue();
    }

    /**
     * Refills the internal bag if empty and draws the next piece.
     * <p>
     * When the bag empties, this method instantiates all 7 {@link Brick} types
     * (I, J, L, O, S, T, Z) and uses {@link Collections#shuffle(List)} to randomize them.
     * </p>
     *
     * @return a single Brick from the current bag.
     */
    private Brick getNextBrickFromBag() {
        if (bag.isEmpty()) {
            bag.add(new IBrick());
            bag.add(new JBrick());
            bag.add(new LBrick());
            bag.add(new OBrick());
            bag.add(new SBrick());
            bag.add(new TBrick());
            bag.add(new ZBrick());

            Collections.shuffle(bag);
        }
        return bag.remove(0);
    }

    /**
     * Ensures the {@code nextBricks} queue always contains enough pieces
     * to satisfy the UI preview requirements (defined by {@code PREVIEW_COUNT}).
     */
    private void fillQueue() {
        while (nextBricks.size() <= PREVIEW_COUNT) {
            nextBricks.add(getNextBrickFromBag());
        }
    }

    /**
     * Retrieves the next brick for gameplay.
     * <p>
     * This method removes the head of the preview queue and immediately triggers
     * a refill to maintain the look-ahead buffer.
     * </p>
     *
     * @return the {@link Brick} to be spawned on the board.
     */
    @Override
    public Brick getBrick() {
        fillQueue();
        return nextBricks.poll();
    }

    /**
     * Provides a read-only snapshot of the upcoming pieces.
     * <p>
     * Used by the {@link com.comp2042.ui.GameRenderer} to draw the "Next" sidebar.
     * The returned list is a copy, so modifying it does not affect the actual generator state.
     * </p>
     *
     * @return a list of the next {@code PREVIEW_COUNT} bricks.
     */
    @Override
    public List<Brick> getUpcomingBricks() {
        fillQueue();
        return new ArrayList<>(nextBricks);
    }
}