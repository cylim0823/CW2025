package com.comp2042.logic;

import com.comp2042.model.MoveEvent;
import com.comp2042.model.ViewData;

public interface InputEventListener {

    void onDownEvent(MoveEvent event);

    void onLeftEvent(MoveEvent event);

    void onRightEvent(MoveEvent event);

    void onRotateEvent(MoveEvent event);

    void onHardDropEvent(MoveEvent event);

    void onHoldEvent(MoveEvent event);

    ViewData createNewGame();

    ViewData getViewData();

    int[][] getBoard();
}