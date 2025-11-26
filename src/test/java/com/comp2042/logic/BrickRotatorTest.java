package com.comp2042.logic;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.model.NextShapeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrickRotatorTest {

    private BrickRotator rotator;
    private Brick brick;

    // creating a simple stub class to test rotation logic independently
    // this avoids dependencies on specific brick implementations like JBrick
    private static class TestBrick implements Brick {
        private final List<int[][]> shapes = new ArrayList<>();

        public TestBrick() {
            // first shape state (single dot)
            shapes.add(new int[][]{{1}});
            // second shape state (empty/zero)
            shapes.add(new int[][]{{0}});
        }

        @Override
        public List<int[][]> getShapeMatrix() {
            return shapes;
        }
    }

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();

        // initialize with the stub brick
        brick = new TestBrick();
        rotator.setBrick(brick);
    }

    @Test
    void testInitialState() {
        // verify the rotator starts with the first shape index
        int[][] current = rotator.getCurrentShape();
        assertArrayEquals(new int[][]{{1}}, current);
    }

    @Test
    void testGetNextShape() {
        // check that the next shape cycles correctly from index 0 to 1
        NextShapeInfo next = rotator.getNextShape();
        assertEquals(1, next.getPosition());
        assertArrayEquals(new int[][]{{0}}, next.getShape());
    }

    @Test
    void testRotationWrapsAround() {
        // set to the last available shape index (1) to test the loop
        rotator.setCurrentShape(1);

        // next shape should wrap back to index 0
        NextShapeInfo next = rotator.getNextShape();
        assertEquals(0, next.getPosition());
        assertArrayEquals(new int[][]{{1}}, next.getShape());
    }
}