package com.comp2042.controllers;

import com.comp2042.logic.GameHistory;
import com.comp2042.logic.InputEventListener;
import com.comp2042.logic.mode.GameMode;
import com.comp2042.logic.mode.NormalMode;
import com.comp2042.managers.ScoreManager;
import com.comp2042.logic.board.Board;
import com.comp2042.model.BoardMemento;
import com.comp2042.logic.board.SimpleBoard;
import com.comp2042.model.*;
import com.comp2042.util.EventSource;
import com.comp2042.util.GameConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * The central controller for the Tetris game logic.
 * <p>
 * This class acts as the Mediator between the Model (Board, Score, History) and the View (UI, Sound).
 * It manages the game loop, input handling, and notifies observers of state changes.
 * </p>
 * <p>
 * It utilizes the <b>Strategy Pattern</b> via {@link GameMode} to support different gameplay styles
 * (e.g., Normal Mode vs. Zen Mode) without modifying the core logic.
 * </p>
 *  @author Chen Yu
 *  @version 1.0
 */
public class GameController implements InputEventListener {

    private final Board board;
    private final ScoreManager scoreManager;
    private final GameHistory gameHistory;
    private final List<GameObserver> observers = new ArrayList<>();

    private GameMode currentMode;
    private int scoreAtSpawn;

    /**
     * Creates a new GameController with a fresh board, score manager,
     * game history stack, and default game mode.
     *
     * <p>Observers are not created here; they should be added using
     * {@link #addObserver(GameObserver)}.</p>
     */
    public GameController() {
        this.board = new SimpleBoard(GameConfiguration.BOARD_HEIGHT, GameConfiguration.BOARD_WIDTH);
        this.scoreManager = new ScoreManager();
        this.gameHistory = new GameHistory();
        this.board.createNewBrick();
        this.scoreAtSpawn = 0;

        scoreManager.scoreProperty().addListener((obs, oldVal, newVal) -> notifyScore(newVal.intValue()));
        scoreManager.levelProperty().addListener((obs, oldVal, newVal) -> notifyLevel(newVal.intValue()));
        this.currentMode = new NormalMode();
    }

    /**
     * Sets the current game mode (normal, timed, endless, etc.).
     * This affects scoring, leveling, undo limits, and whether
     * high scores can be saved.
     *
     * @param mode the new game mode to apply
     */
    public void setGameMode(GameMode mode) {
        this.currentMode = mode;
        this.scoreManager.setSavingEnabled(mode.isHighScoreEnabled());
        this.scoreManager.setLevelingEnabled(mode.isLevelingEnabled());
    }

    // Observer management
    /**
     * Registers a new GameObserver to receive UI updates.
     * When added, the observer immediately receives:
     * - background matrix
     * - board view data
     * - current score and level
     * - danger state
     *
     * @param observer the observer to register
     */
    public void addObserver(GameObserver observer) {
        observers.add(observer);
        observer.onGameBackgroundUpdated(board.getBoardMatrix());
        observer.onBoardUpdated(board.getViewData());
        observer.onScoreUpdated(scoreManager.scoreProperty().get());
        observer.onLevelUpdated(scoreManager.levelProperty().get());
        observer.onDangerStateChanged(board.isDangerState());
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

    /**
     * Notifies all observers that the game has ended.
     * If the current mode supports high score saving,
     * the controller will store the new record.
     */
    public void notifyGameOver() {
        notifyDanger(false);
        if (currentMode.isHighScoreEnabled()) {
            scoreManager.checkAndSaveHighestScore();
        }
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

    private void notifyDanger(boolean isDanger) {
        if (!currentMode.isDangerAllowed()) {
            isDanger = false;
        }
        for (GameObserver o : observers) {
            o.onDangerStateChanged(isDanger);
        }
    }

    // Game logic

    private void saveState() {
        gameHistory.save(new BoardMemento(
                board.getBoardMatrix(),
                scoreAtSpawn,
                scoreManager.levelProperty().get()
        ));
    }

    /**
     * Restores the previous board, score, and level state
     * from gameHistory, respecting the undo limit defined
     * by the active GameMode.
     *
     * <p>If no previous states are available, nothing happens.</p>
     */
    private void undo() {
        int limit = currentMode.getUndoLimit();
        BoardMemento previousState = gameHistory.popState(limit);

        if (previousState == null) {
            return;
        }
        board.restoreState(previousState.getBoardState());
        scoreManager.restoreState(previousState.getScore(), previousState.getLevel());
        board.resetCurrentBrick();

        scoreAtSpawn = previousState.getScore();

        notifyBackground();
        notifyBoard();
        notifyScore(previousState.getScore());
        notifyLevel(previousState.getLevel());
    }

    /**
     * Handles all logic when a falling piece can no longer move down:
     * <ol>
     *   <li>Save current state for undo</li>
     *   <li>Merge brick into background</li>
     *   <li>Clear completed rows</li>
     *   <li>Update score and notify observers</li>
     *   <li>Spawn a new piece</li>
     *   <li>Detect game over</li>
     * </ol>
     */
    private void handlePieceLanded() {
        saveState();

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        int linesCleared = clearRow.getLinesRemoved();
        scoreManager.onRowsCleared(linesCleared);

        if (linesCleared > 0) {
            String message = switch (linesCleared) {
                case 1 -> "SINGLE";
                case 2 -> "DOUBLE";
                case 3 -> "TRIPLE";
                case GameConfiguration.LINES_FOR_TETRIS -> "TETRIS!";
                default -> "NICE!";
            };
            notifyLineClear(linesCleared, message);
        }

        boolean isDanger = board.isDangerState();
        notifyDanger(isDanger);
        notifyBackground();

        boolean isGameOver = board.createNewBrick();

        if (isGameOver) {
            currentMode.handleGameOver(this);
        } else {
            scoreAtSpawn = scoreManager.scoreProperty().get();
            notifyBoard();
        }
    }

    // Input Events
    /** Undo input event triggered by the player. */
    @Override
    public void onUndoEvent() {
        undo();
    }

    /**
     * Soft drop event. Moves the brick down by 1 cell.
     * If the brick cannot move further, it triggers landing logic.
     *
     * @param event includes whether the move was caused by the user or gravity
     */
    @Override
    public void onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            handlePieceLanded();
        } else {
            if (event.getEventSource() == EventSource.USER) {
                scoreManager.onSoftDrop();
                notifyBrickDropped();
            }
            notifyBoard();
        }
    }

    /**
     * Performs a hard drop (instantly drop to the bottom),
     * updates score, and finalizes the piece.
     *
     * @param event includes information about the event source
     */
    @Override
    public void onHardDropEvent(MoveEvent event) {
        int rowsDropped = board.hardDrop();
        scoreManager.onHardDrop(rowsDropped);
        notifyBrickDropped();
        handlePieceLanded();
    }

    /** Handles holding the current brick. */
    @Override
    public void onHoldEvent(MoveEvent event) {
        boolean isGameOver = board.holdCurrentBrick();
        if (isGameOver) {
            currentMode.handleGameOver(this);
        } else {
            notifyBoard();
        }
    }

    /** Moves the brick left by one cell (if possible). */
    @Override
    public void onLeftEvent() {
        board.moveBrickLeft();
        notifyBoard();
    }

    /** Moves the brick right by one cell (if possible). */
    @Override
    public void onRightEvent() {
        board.moveBrickRight();
        notifyBoard();
    }

    /** Rotates the brick counter-clockwise. */
    @Override
    public void onRotateEvent() {
        board.rotateLeftBrick();
        notifyBoard();
    }

    /**
     * Starts a completely new game.
     * This resets:
     * - board
     * - score
     * - level
     * - undo history
     * - danger state
     *
     * Observers are notified to redraw the board.
     */
    @Override
    public void createNewGame() {
        board.newGame();
        scoreManager.reset();
        gameHistory.reset();
        scoreAtSpawn = 0;

        notifyDanger(false); // Reset music/shake

        notifyBackground();
        notifyBoard();
    }

    /**
     * Returns the current board matrix (without the falling brick).
     *
     * @return 2D array representing background tiles of the board
     */
    @Override
    public int[][] getBoard() {
        return board.getBoardMatrix();
    }
}