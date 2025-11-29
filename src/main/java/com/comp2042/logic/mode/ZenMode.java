package com.comp2042.logic.mode;

import com.comp2042.controllers.GameController;

public class ZenMode implements GameMode {
    @Override
    public boolean isHighScoreEnabled() { return false; }

    @Override
    public boolean isLevelingEnabled() { return false; }

    @Override
    public boolean isDangerAllowed() { return false; }

    @Override
    public boolean isLevelLabelVisible() { return false; }

    @Override
    public int getUndoLimit(){
        return Integer.MAX_VALUE;  // Infinite undos
    }

    @Override
    public void handleGameOver(GameController gameController) {
        // Zen behavior: Auto-restart immediately
        gameController.createNewGame();
    }
}