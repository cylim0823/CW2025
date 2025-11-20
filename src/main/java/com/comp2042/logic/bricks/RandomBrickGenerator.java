package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class RandomBrickGenerator implements BrickGenerator {

    private final ArrayList<Brick> bag = new ArrayList<>();

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    private static final int PREVIEW_COUNT = 4;

    public RandomBrickGenerator() {
        fillQueue();
    }

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

    // This method's job is to keep the 'nextBricks' queue full
    private void fillQueue() {
        while (nextBricks.size() <= PREVIEW_COUNT) {
            nextBricks.add(getNextBrickFromBag());
        }
    }

    @Override
    public Brick getBrick() {
        fillQueue();
        return nextBricks.poll();
    }

    @Override
    public List<Brick> getUpcomingBricks() {
        fillQueue();
        return new ArrayList<>(nextBricks);
    }
}