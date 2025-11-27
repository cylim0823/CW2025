package com.comp2042.logic.board;

import com.comp2042.model.ClearRow;
import com.comp2042.model.ViewData;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    void newGame();

    int hardDrop();

    boolean holdCurrentBrick();

    void restoreState(int[][] savedGrid);

    void resetCurrentBrick();

    boolean isDangerState();
}