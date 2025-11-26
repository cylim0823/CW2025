package com.comp2042.logic;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RandomBrickGeneratorTest {

    @Test
    void testGetBrickReturnsValidBrick() {
        BrickGenerator generator = new RandomBrickGenerator();

        // run a loop to make sure the random generator is stable and doesn't crash
        for (int i = 0; i < 100; i++) {
            Brick brick = generator.getBrick();

            // generator should always produce a brick object
            assertNotNull(brick, "Generator should never return null");

            // every brick needs rotation data
            List<int[][]> shapes = brick.getShapeMatrix();
            assertFalse(shapes.isEmpty(), "Brick should have at least one rotation shape");

            // check that the brick grid is the correct size (4x4)
            int[][] shape0 = shapes.get(0);
            assertEquals(4, shape0.length, "Brick matrix height should be 4");
            assertEquals(4, shape0[0].length, "Brick matrix width should be 4");
        }
    }

    @Test
    void testUpcomingBricks() {
        BrickGenerator generator = new RandomBrickGenerator();
        List<Brick> upcoming = generator.getUpcomingBricks();

        // make sure the next bricks list is initialized and populated
        assertNotNull(upcoming);
        assertFalse(upcoming.isEmpty(), "Should have upcoming bricks ready");
        assertTrue(upcoming.size() >= 1, "Should show at least 1 next brick");
    }
}