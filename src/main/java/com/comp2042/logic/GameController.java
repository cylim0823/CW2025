package com.comp2042.logic;

import com.comp2042.model.*;
import java.util.ArrayList;
import java.util.List;

public class GameController implements InputEventListener {

    private static final int GAME_HEIGHT = 24;
    private static final int GAME_WIDTH = 10;

    private final Board board;
    private final ScoreManager scoreManager;
    private final List<GameObserver> observers = new ArrayList<>();

    public GameController() {
        this.board = new SimpleBoard(GAME_HEIGHT, GAME_WIDTH);
        this.scoreManager = new ScoreManager();
        this.board.createNewBrick();

        scoreManager.scoreProperty().addListener((obs, oldVal, newVal) -> notifyScore(newVal.intValue()));
        scoreManager.levelProperty().addListener((obs, oldVal, newVal) -> notifyLevel(newVal.intValue()));
    }

    // Observer management

    public void addObserver(GameObserver observer) {
        observers.add(observer);
        observer.onGameBackgroundUpdated(board.getBoardMatrix());
        observer.onBoardUpdated(board.getViewData());
        observer.onScoreUpdated(scoreManager.scoreProperty().get());
        observer.onLevelUpdated(scoreManager.levelProperty().get());
    }

    // Notification helpers

    private void notifyBoard() {
        ViewData data = board.getViewData();
        for (GameObserver o : observers) {
            o.onBoardUpdated(data);
        }
    }

    private void notifyBackground() {
        int[][] matrix = board.getBoardMatrix();
        for (GameObserver o : observers) {
            o.onGameBackgroundUpdated(matrix);
        }
    }

    private void notifyScore(int score) {
        for (GameObserver o : observers) {
            o.onScoreUpdated(score);
        }
    }

    private void notifyLevel(int level) {
        for (GameObserver o : observers) {
            o.onLevelUpdated(level);
        }
    }

    private void notifyGameOver() {
        scoreManager.checkAndSaveHighestScore();

        for (GameObserver o : observers) {
            o.onGameOver();
        }
    }

    private void notifyBrickDropped() {
        for (GameObserver o : observers) {
            o.onBrickDropped();
        }
    }

    private void notifyLineClear(int lines, String message) {
        for (GameObserver o : observers) {
            o.onLineCleared(lines, message);
        }
    }

    // Game logic

    private void handlePieceLanded() {
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        int linesCleared = clearRow.getLinesRemoved();
        scoreManager.onRowsCleared(linesCleared);

        if (linesCleared > 0) {
            String message;
            switch (linesCleared) {
                case 1: message = "SINGLE"; break;
                case 2: message = "DOUBLE"; break;
                case 3: message = "TRIPLE"; break;
                case 4: default: message = "TETRIS!"; break;
            }
            notifyLineClear(linesCleared, message);
        }

        notifyBackground();

        boolean isGameOver = board.createNewBrick();
        if (isGameOver) {
            notifyGameOver();
        } else {
            notifyBoard();
        }
    }

    @Override
    public void onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            handlePieceLanded();
        } else {
            if (event.getEventSource() == EventSource.USER) {
                scoreManager.onSoftDrop();
                notifyBrickDropped();  // Play sound
            }
            notifyBoard();
        }
    }

    @Override
    public void onHardDropEvent(MoveEvent event) {
        int rowsDropped = board.hardDrop();
        scoreManager.onHardDrop(rowsDropped);
        notifyBrickDropped();
        handlePieceLanded();
    }

    @Override
    public void onHoldEvent(MoveEvent event) {
        boolean isGameOver = board.holdCurrentBrick();
        if (isGameOver) {
            notifyGameOver();
        } else {
            notifyBoard();
        }
    }

    @Override
    public void onLeftEvent() {
        board.moveBrickLeft();
        notifyBoard();
    }

    @Override
    public void onRightEvent() {
        board.moveBrickRight();
        notifyBoard();
    }

    @Override
    public void onRotateEvent() {
        board.rotateLeftBrick();
        notifyBoard();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        scoreManager.reset();
        notifyBackground();
        notifyBoard();
    }

    @Override
    public int[][] getBoard() {
        return board.getBoardMatrix();
    }
}