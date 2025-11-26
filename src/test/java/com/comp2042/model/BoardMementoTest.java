package com.comp2042.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardMementoTest {

    @Test
    void testDeepCopyEnsuresImmutability() {
        // create a dummy board with some initial data to test copying
        int[][] initialBoard = {
                {1, 1},
                {0, 0}
        };

        // save this state into the memento object
        BoardMemento memento = new BoardMemento(initialBoard, 100, 1);

        // modify the original array to simulate the game state changing
        // we change a '1' to a '9' here
        initialBoard[0][0] = 9;

        // retrieve the board from the memento
        int[][] savedState = memento.getBoardState();

        // the saved memento should still have the old value (1)
        // if this is 9, it means the deep copy failed and the undo feature is broken
        assertEquals(1, savedState[0][0], "Memento should keep the old value (1), not the new value (9).");
    }

    @Test
    void testGetScoreAndLevel() {
        // simple check to make sure score and level are stored and retrieved correctly
        BoardMemento memento = new BoardMemento(new int[0][0], 500, 5);

        assertEquals(500, memento.getScore());
        assertEquals(5, memento.getLevel());
    }
}