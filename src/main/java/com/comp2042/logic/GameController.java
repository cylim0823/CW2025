package com.comp2042.logic;

import com.comp2042.model.*;
import com.comp2042.ui.GuiController;
import javafx.beans.property.IntegerProperty;

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

        if (canMove) {
            if (event.getEventSource() == EventSource.USER) {
                scoreManager.onSoftDrop();
            }
            return new DownData(null, board.getViewData(), 0);
        }

        ViewData dataBeforeSpawn = board.getViewData();
        board.mergeBrickToBackground();
        clearRow = board.clearRows();
        bonus = scoreManager.onRowsCleared(clearRow.getLinesRemoved());
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        boolean isGameOver = board.createNewBrick();

        if (isGameOver) {
            viewGuiController.gameOver();

            // Send empty, invisible data to prevent visual bug
            ViewData gameOverData = new ViewData(
                    new int[4][4],
                    dataBeforeSpawn.getxPosition(),
                    dataBeforeSpawn.getyPosition(),
                    new int[4][4],
                    dataBeforeSpawn.getGhostYPosition(),
                    dataBeforeSpawn.getHoldBrickData()
            );
            return new DownData(clearRow, gameOverData, bonus);
        } else {
            return new DownData(clearRow, board.getViewData(), bonus);
        }
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        int rowsDropped = board.hardDrop();
        scoreManager.onHardDrop(rowsDropped);

        ViewData dataBeforeSpawn = board.getViewData();
        board.mergeBrickToBackground();

        ClearRow clearRow = board.clearRows();
        int bonus = scoreManager.onRowsCleared(clearRow.getLinesRemoved());
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        boolean isGameOver = board.createNewBrick();

        if (isGameOver) {
            viewGuiController.gameOver();
            // Send empty, invisible data to prevent visual bug
            ViewData gameOverData = new ViewData(
                    new int[4][4],
                    dataBeforeSpawn.getxPosition(),
                    dataBeforeSpawn.getyPosition(),
                    new int[4][4],
                    dataBeforeSpawn.getGhostYPosition(),
                    dataBeforeSpawn.getHoldBrickData()
            );
            return new DownData(clearRow, gameOverData, bonus);
        } else {
            return new DownData(clearRow, board.getViewData(), bonus);
        }
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        ViewData dataBeforeHold = board.getViewData();
        boolean isGameOver = board.holdCurrentBrick();

        if (isGameOver) {
            viewGuiController.gameOver();
            ViewData gameOverData = new ViewData(
                    new int[4][4],
                    dataBeforeHold.getxPosition(),
                    dataBeforeHold.getyPosition(),
                    new int[4][4],
                    dataBeforeHold.getGhostYPosition(),
                    dataBeforeHold.getHoldBrickData()
            );
            return gameOverData;
        } else {
            return board.getViewData();
        }
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
    public ViewData createNewGame() {
        board.newGame();
        scoreManager.reset();
        return board.getViewData();
    }

    @Override
    public int[][] getBoard() {
        return board.getBoardMatrix();
    }
}