package com.comp2042.logic;

import com.comp2042.model.*;
import com.comp2042.ui.GuiController;
import javafx.beans.property.IntegerProperty;

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
    public DownData onDownEvent(MoveEvent event) {
        // Determine how many rows to attempt to move
        int multiplier = 1;

        // Only apply the multiplier for automatic drops (THREAD)
        // User soft drop (USER) should always move 1 row.
        if (event.getEventSource() == EventSource.THREAD) {
            // Get the multiplier from the ScoreManager
            multiplier = scoreManager.getDropMultiplier();
        }

        boolean canMove = true;
        for (int i = 0; i < multiplier; i++) {
            if (!board.moveBrickDown()) {
                canMove = false;
                break; // Stop trying to move if it hits something
            }
        }

        if (!canMove) {
            ViewData dataBeforeSpawn = board.getViewData();
            return handlePieceLanded(dataBeforeSpawn);
        } else {
            if (event.getEventSource() == EventSource.USER) {
                scoreManager.onSoftDrop();
            }
            return new DownData(null, board.getViewData(), 0);
        }
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        int rowsDropped = board.hardDrop();
        scoreManager.onHardDrop(rowsDropped);

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