package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    private static final int PREVIEW_COUNT = 4;

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());

        fillQueue(); // 1 to play and 4 for preview
    }

    private void fillQueue(){
        while(nextBricks.size() <= PREVIEW_COUNT){
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
    }

    @Override
    public Brick getBrick() {
        fillQueue();
        return nextBricks.poll(); // Take the next brick
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek(); // shows the next one
    }

    @Override
    public List<Brick> getUpcomingBricks() {
        return new ArrayList<>(nextBricks);
    }
}
