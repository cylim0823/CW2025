package com.comp2042.logic.mode;

import com.comp2042.controllers.GameController;
import com.comp2042.util.GameConfiguration;

public class NormalMode implements GameMode {
    @Override
    public boolean isHighScoreEnabled() { return true; }

    @Override
    public boolean isLevelingEnabled() { return true; }

    @Override
    public boolean isDangerAllowed() { return true; }

    @Override
    public boolean isLevelLabelVisible() { return true; }

    @Override
    public int getUndoLimit(){
        return GameConfiguration.UNDO_LIMIT_NORMAL;
    }

    @Override
    public void handleGameOver(GameController gameController) {
        // Normal behavior: Show the Game Over screen
        gameController.notifyGameOver();
    }
}