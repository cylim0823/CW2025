package com.comp2042.logic;

import com.comp2042.model.*;
import com.comp2042.ui.GuiController;
import javafx.beans.property.IntegerProperty;

import java.util.ArrayList;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(24, 10);
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

        scoreManager.levelProperty().addListener((obs, oldLevel, newLevel) -> {
            viewGuiController.updateLevel(newLevel.intValue());
        });
    }

    private DownData handlePieceLanded(ViewData dataBeforeSpawn) {
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        int linesCleared = clearRow.getLinesRemoved();
        int bonus = scoreManager.onRowsCleared(linesCleared);

        if (linesCleared > 0) {
            String message = "";
            switch (linesCleared) {
                case 1: message = "SINGLE"; break;
                case 2: message = "DOUBLE"; break;
                case 3: message = "TRIPLE"; break;
                case 4: default: message = "TETRIS!"; break;
            }
            viewGuiController.showLineClearNotification(message);
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        boolean isGameOver = board.createNewBrick();

        if (isGameOver) {
            viewGuiController.gameOver();
            ViewData gameOverData = new ViewData(
                    new int[4][4],
                    dataBeforeSpawn.getxPosition(),
                    dataBeforeSpawn.getyPosition(),
                    new ArrayList<>(),
                    dataBeforeSpawn.getGhostYPosition(),
                    dataBeforeSpawn.getHoldBrickData()
            );
            return new DownData(clearRow, gameOverData, bonus);
        } else {
            return new DownData(clearRow, board.getViewData(), bonus);
        }
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {

        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            ViewData dataBeforeSpawn = board.getViewData();
            return handlePieceLanded(dataBeforeSpawn);
        } else {
            if (event.getEventSource() == EventSource.USER) {
                scoreManager.onSoftDrop(); // score calculator
            }
            return new DownData(null, board.getViewData(), 0);
        }
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        int rowsDropped = board.hardDrop();
        scoreManager.onHardDrop(rowsDropped); // score calculator

        ViewData dataBeforeSpawn = board.getViewData();
        return handlePieceLanded(dataBeforeSpawn);
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
                    new ArrayList<>(),
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
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return board.getViewData();
    }

    @Override
    public int[][] getBoard() {
        return board.getBoardMatrix();
    }
}