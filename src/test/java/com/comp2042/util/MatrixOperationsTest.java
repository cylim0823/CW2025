package com.comp2042.util;

import com.comp2042.model.ClearRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrixOperationsTest {

    @Test
    void testIntersectionLogic() {

        // A big empty game board (5 rows, 5 cols)
        int[][] board = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        // define a simple 2x2 square brick
        int[][] brick = {
                {4, 4},
                {4, 4}
        };

        // check if the brick intersects with the board at (x=1, y=1)
        boolean result = MatrixOperations.intersect(board, brick, 1, 1);

        // Check if the result is what we expect
        assertFalse(result, "A brick should not intersect with an empty board");
    }

    @Test
    void testIntersectionWithExistingBlocks() {

        // A board that already has a block at [row=2, col=2]
        int[][] board = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 9, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        // define a T-shaped brick
        int[][] tBrick = {
                {0, 7, 0},
                {7, 7, 7}
        };

        // place the T-brick at (1,1) so it overlaps with the obstacle at (2,2)
        boolean result = MatrixOperations.intersect(board, tBrick, 1, 1);

        // since the brick hits the existing block, result should be true
        assertTrue(result, "A piece should intersect with an existing block.");
    }

    @Test
    void testMergeLogic() {
        int[][] board = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        int[][] tBrick = {
                {0, 7, 0},
                {7, 7, 7}
        };

        // manually define what the board should look like after merging the brick
        int[][] expectedResult = {
                {0, 0, 0, 0, 0},
                {0, 0, 7, 0, 0},
                {0, 7, 7, 7, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        int[][] actualResult = MatrixOperations.merge(board, tBrick, 1, 1);

        assertArrayEquals(expectedResult, actualResult, "The brick was not merged correctly.");
    }

    @Test
    void testCheckRemovingClearsLines() {
        // setup a 4x4 board where the bottom row is completely full
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {1, 1, 1, 1}
        };

        // run the logic to detect and remove full rows
        ClearRow result = MatrixOperations.checkRemoving(board);

        // verify it counted exactly 1 removed line
        assertEquals(1, result.getLinesRemoved(), "Should detect 1 cleared line");

        // check that the bottom row in the new matrix is now empty (0)
        int[][] newMatrix = result.getNewMatrix();
        assertEquals(0, newMatrix[3][0], "Bottom row should be cleared (0)");
    }

    @Test
    void testWallCollision() {
        int[][] board = new int[5][5];
        int[][] brick = {{1}}; // single block for easy testing

        // try to place brick outside left boundary
        assertTrue(MatrixOperations.intersect(board, brick, -1, 0), "Should collide with left wall");

        // try to place brick outside right boundary
        assertTrue(MatrixOperations.intersect(board, brick, 5, 0), "Should collide with right wall");

        // try to place brick below the floor
        assertTrue(MatrixOperations.intersect(board, brick, 0, 5), "Should collide with floor");
    }

    @Test
    void testDeepCopy() {
        // verify that the utility creates a new object instance, not just a reference
        int[][] original = {{1}};
        int[][] copy = MatrixOperations.copy(original);

        assertNotSame(original, copy, "Should return a new object, not just a reference");
        assertEquals(original[0][0], copy[0][0], "Values should match");

        // modify the original array and ensure the copy stays the same
        original[0][0] = 9;
        assertEquals(1, copy[0][0], "Copy should NOT change when original changes");
    }
}