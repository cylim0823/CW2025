package com.comp2042.logic.mode;

import com.comp2042.controllers.GameController;

public interface GameMode {
    boolean isHighScoreEnabled();
    boolean isLevelingEnabled();
    boolean isDangerAllowed();
    boolean isLevelLabelVisible();
    int getUndoLimit();

    /**
     * Decides what happens when the game ends.
     * @param controller We pass the controller so the mode can call reset() or notifyGameOver()
     */
    void handleGameOver(GameController controller);
}