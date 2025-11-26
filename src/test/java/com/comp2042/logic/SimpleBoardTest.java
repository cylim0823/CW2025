package com.comp2042.logic;

import com.comp2042.logic.board.SimpleBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        // initialize a standard 10x20 board before each test
        board = new SimpleBoard(20, 10);
        board.newGame();
    }

    @Test
    void testRestoreStateOverwritesGrid() {
        // create a dummy grid with a single block at the bottom left
        // this simulates loading an old save file
        int[][] savedGrid = new int[20][10];
        savedGrid[19][0] = 1;

        // force the board to adopt this saved state
        board.restoreState(savedGrid);

        // check if the internal board matrix actually updated to match our dummy grid
        int[][] currentMatrix = board.getBoardMatrix();
        assertEquals(1, currentMatrix[19][0], "Board should now have the block from the saved state");
    }

    @Test
    void testResetCurrentBrick() {
        // move the brick down a few times so it's definitely not at the top
        board.moveBrickDown();
        board.moveBrickDown();

        // trigger the reset function used during an undo
        board.resetCurrentBrick();

        // check view data to ensure the brick snapped back to the spawn coordinates
        // usually x=3 and y=0 for a standard width board
        assertEquals(3, board.getViewData().getxPosition(), "Brick X should reset to spawn position");
        assertEquals(0, board.getViewData().getyPosition(), "Brick Y should reset to 0");
    }

    @Test
    void testMoveLeftAndRight() {
        board.createNewBrick();

        // capture the starting position to compare against later
        int startX = board.getViewData().getxPosition();

        // move left and verify the x position decreased
        board.moveBrickLeft();
        assertEquals(startX - 1, board.getViewData().getxPosition(), "Should move left by 1");

        // move right and verify it returned to the original spot
        board.moveBrickRight();
        assertEquals(startX, board.getViewData().getxPosition(), "Should move back to start");
    }

    @Test
    void testNewGameClearsBoard() {
        // manually dirty the board by placing a block in the middle
        int[][] dirtyState = new int[20][10];
        dirtyState[10][5] = 1;
        board.restoreState(dirtyState);

        // confirm the block is actually there
        assertEquals(1, board.getBoardMatrix()[10][5]);

        // start a new game which should wipe everything
        board.newGame();

        // verify the specific spot is empty (0) again
        assertEquals(0, board.getBoardMatrix()[10][5], "New Game should wipe the board");
    }
}