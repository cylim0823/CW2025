package com.comp2042.logic;

import com.comp2042.model.*;
import com.comp2042.ui.GuiController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private final GuiController viewGuiController;
    private final ScoreManager scoreManager;

    public GameController(GuiController c) {
        viewGuiController = c;
        scoreManager = new ScoreManager();
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.showCountdown();
        viewGuiController.bindScore(scoreManager.scoreProperty());
        viewGuiController.bindLevel(scoreManager.levelProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        int bonus = 0;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            bonus = scoreManager.onRowsCleared(clearRow.getLinesRemoved());

            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } else {
            if (event.getEventSource() == EventSource.USER) {
                scoreManager.onSoftDrop();
            }
        }
        return new DownData(clearRow, board.getViewData(), bonus);
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
        scoreManager.reset();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    @Override
    public ViewData onHardDropEvent(MoveEvent event){
        int rowsDropped = board.hardDrop();
        scoreManager.onHardDrop(rowsDropped);
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        board.holdCurrentBrick();
        return board.getViewData();
    }
}