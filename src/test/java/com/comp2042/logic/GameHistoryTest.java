package com.comp2042.logic;

import com.comp2042.model.BoardMemento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameHistoryTest {

    private GameHistory history;
    private BoardMemento dummyState;

    @BeforeEach
    void setUp() {
        history = new GameHistory();
        // create a fake state just to put something in the stack
        dummyState = new BoardMemento(new int[1][1], 0, 0);
    }

    @Test
    void testSaveAndPop() {
        // save one state and try to get it back immediately
        history.save(dummyState);
        assertNotNull(history.popState());
    }

    @Test
    void testUndoLimitIsThree() {
        // simulate the player using up all 3 allowed undos
        history.save(dummyState); history.popState();
        history.save(dummyState); history.popState();
        history.save(dummyState); history.popState();

        // verify we used exactly 3 lives
        assertEquals(3, history.getUndoCount());

        // try to undo a 4th time, which should fail
        history.save(dummyState);
        BoardMemento result = history.popState();

        assertNull(result, "Should return null because max undo limit is reached");
        assertEquals(3, history.getUndoCount(), "Count should stop at 3 even if we try more");
    }

    @Test
    void testSpamProtection() {
        history.save(dummyState);

        // perform a valid undo first
        history.popState();

        // try to undo again immediately while the history is empty (spamming the key)
        BoardMemento spamClick = history.popState();

        // this should be null because there is nothing left to undo
        assertNull(spamClick, "Should return null if history is empty");

        // make sure the counter didn't go up for the failed attempt
        // the player shouldn't lose a "life" for clicking on an empty history
        assertEquals(1, history.getUndoCount());
    }
}