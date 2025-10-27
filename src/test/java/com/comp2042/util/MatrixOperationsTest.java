package com.comp2042.util;

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

        int[][] brick = {
                {4, 4},  // row 0
                {4, 4}   // row 1
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

        int[][] tBrick = {
                {0, 7, 0}, // row 0
                {7, 7, 7}  // row 1
        };

        // place the T-brick at (x=1, y=1)
        int x = 1; // col
        int y = 1; // row

        /* Visualizing the collision:
           Board:
           {0, 0, 0, 0, 0},
           {0, 0, 0, 0, 0},
           {0, 0, 9, 0, 0},
           {0, 0, 0, 0, 0},
           {0, 0, 0, 0, 0}

           Placing T-Brick at (x=1, y=1)
           {0, 0, 0, 0, 0},
           {0, 0, 7, 0, 0},
           {0, 7, 7, 7, 0},
           {0, 0, 0, 0, 0},
           {0, 0, 0, 0, 0}
        */

        boolean result = MatrixOperations.intersect(board, tBrick, x, y);

        // A collision is expected, so the result should be 'true'.
        assertTrue(result, "A piece should intersect with an existing block.");
    }


    @Test
    void testMergeLogic() {
        // An empty 5x5 board
        int[][] board = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        int[][] tBrick = {
                {0, 7, 0}, // row 0
                {7, 7, 7}  // row 1
        };

        int x = 1; // col
        int y = 1; // row

        int[][] expectedResult = {
                {0, 0, 0, 0, 0},
                {0, 0, 7, 0, 0},
                {0, 7, 7, 7, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        int[][] actualResult = MatrixOperations.merge(board, tBrick, x, y);

        assertArrayEquals(expectedResult, actualResult, "The brick was not merged correctly.");
    }
}