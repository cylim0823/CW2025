package com.comp2042.ui;

import com.comp2042.logic.InputEventListener;
import com.comp2042.model.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyManager {

    private final GuiController guiController;
    private final GameLoopManager gameLoopManager;
    private InputEventListener eventListener;

    public KeyManager(GuiController guiController, GameLoopManager gameLoopManager) {
        this.guiController = guiController;
        this.gameLoopManager = gameLoopManager;
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void handleInput(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.P) {
            guiController.togglePause();
            keyEvent.consume();
            return;
        }

        if (code == KeyCode.M){
            guiController.toggleMute();
            keyEvent.consume();
            return;
        }

        if (gameLoopManager.isCountingDownProperty().get()) {
            keyEvent.consume();
            return;
        }

        if (gameLoopManager.isPauseProperty().get() || gameLoopManager.isGameOverProperty().get()) {
            if (code == KeyCode.N) {
                guiController.startNewGame();
            }
            return;
        }

        //  if game is running
        if (eventListener != null) {
            switch (code) {
                case LEFT:
                case A:
                    eventListener.onLeftEvent();
                    break;
                case RIGHT:
                case D:
                    eventListener.onRightEvent();
                    break;
                case UP:
                case W:
                    eventListener.onRotateEvent();
                    break;
                case DOWN:
                case S:
                    guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                    break;
                case SPACE:
                    eventListener.onHardDropEvent(new MoveEvent(null, EventSource.USER));
                    break;
                case C:
                    eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
                    break;
                case N:
                    guiController.startNewGame();
                    break;
            }
        }
        keyEvent.consume();
    }
}