package com.comp2042.logic;

import com.comp2042.model.*;
import com.comp2042.ui.GuiController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private final GuiController viewGuiController;
    private int totalLinesCleared = 0;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindLevel(this.levelProperty());
    }

    public IntegerProperty levelProperty() {
        return currentLevel;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                totalLinesCleared += clearRow.getLinesRemoved();
                board.getScore().add(clearRow.getScoreBonus());
                while (totalLinesCleared >= currentLevel.get() * 10) {
                    currentLevel.set(currentLevel.get() + 1);
                    viewGuiController.updateLevel(currentLevel.get());
                }
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        totalLinesCleared = 0;
        currentLevel.set(1);
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    @Override
    public ViewData onHardDropEvent(MoveEvent event){
        board.hardDrop();
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        board.holdCurrentBrick();
        return board.getViewData();
    }
}