package com.comp2042.managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {

    @Test
    void testScoreAccumulation() {
        ScoreManager sm = new ScoreManager();
        sm.reset();

        // soft drops should add exactly 1 point
        sm.onSoftDrop();
        assertEquals(1, sm.scoreProperty().get());

        // hard drops should add points equal to the rows dropped (e.g. 5 rows = 5 points)
        sm.onHardDrop(5);
        assertEquals(6, sm.scoreProperty().get());
    }

    @Test
    void testRestoreState() {
        ScoreManager sm = new ScoreManager();
        sm.reset();

        // simulate some gameplay to increase the score
        sm.onSoftDrop();
        sm.onSoftDrop();
        assertEquals(2, sm.scoreProperty().get());

        // use the restore function (from the undo feature) to revert to a previous score
        sm.restoreState(0, 1);

        // verify that the score property actually went back to 0
        assertEquals(0, sm.scoreProperty().get(), "Score should revert to 0 after restoreState");
        assertEquals(1, sm.levelProperty().get(), "Level should match restored value");
    }
}