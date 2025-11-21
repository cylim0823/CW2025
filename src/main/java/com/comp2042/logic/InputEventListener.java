package com.comp2042.logic;

import com.comp2042.model.MoveEvent;

public interface InputEventListener {

    void onDownEvent(MoveEvent event);

    void onLeftEvent();

    void onRightEvent();

    void onRotateEvent();

    void onHardDropEvent(MoveEvent event);

    void onHoldEvent(MoveEvent event);

    void createNewGame();

    int[][] getBoard();
}