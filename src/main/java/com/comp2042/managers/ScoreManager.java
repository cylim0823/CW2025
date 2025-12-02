package com.comp2042.managers;

import com.comp2042.model.Score;
import com.comp2042.util.GameConfiguration;
import com.comp2042.util.ScoreFileHandler;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the player's current score, level progression, and high score tracking.
 * <p>
 * <b>Architectural Note (SRP):</b> This class strictly handles the <i>logic</i> of scoring
 * (calculating points, determining level ups). It delegates the actual file persistence to
 * {@link ScoreFileHandler} to adhere to the Single Responsibility Principle.
 * </p>
 * <p>
 * <b>Strategy Pattern Support:</b> This manager exposes configuration flags ({@code savingEnabled},
 * {@code levelingEnabled}) that allow the active {@link com.comp2042.logic.mode.GameMode}
 * to enable or disable features (e.g., disabling leveling in Zen Mode) without changing internal logic.
 * </p>
 * @author Chen Yu
 * @version 1.0
 */
public class ScoreManager {

    private static final int SCORE_PER_LINE = GameConfiguration.SCORE_PER_LINE;
    private static final int LINES_PER_LEVEL_UP = GameConfiguration.LINES_PER_LEVEL_UP;

    private int totalLinesCleared = 0;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private final Score score = new Score();
    private int highestScore = 0;
    private final ScoreFileHandler fileHandler;

    // Configuration flags controlled by the GameMode Strategy
    private boolean savingEnabled = true;
    private boolean levelingEnabled = true;

    /**
     * Constructs a new ScoreManager.
     * <p>
     * Initializes the file handler using the path defined in configuration and
     * immediately loads the current high score from disk.
     * </p>
     */
    public ScoreManager() {
        this.fileHandler = new ScoreFileHandler(GameConfiguration.PATH_HIGHEST_SCORE);
        this.highestScore = fileHandler.loadHighScore();
    }

    /**
     * Configures whether new high scores should be written to disk.
     * <p>
     * Called by the {@link com.comp2042.controllers.GameController} when switching modes.
     * </p>
     *
     * @param enabled true to enable saving (Normal Mode); false to disable (Zen Mode).
     */
    public void setSavingEnabled(boolean enabled) {
        this.savingEnabled = enabled;
    }

    /**
     * Configures whether the game level (and speed) should increase as lines are cleared.
     *
     * @param enabled true to enable leveling (Normal Mode); false for constant speed (Zen Mode).
     */
    public void setLevelingEnabled(boolean enabled) {
        this.levelingEnabled = enabled;
    }

    /**
     * Restores the score and level to a previous state.
     * Part of the Memento Pattern for the Undo feature.
     *
     * @param savedScore the score value to restore.
     * @param savedLevel the level value to restore.
     */
    public void restoreState(int savedScore, int savedLevel) {
        this.score.scoreProperty().set(savedScore);
        this.currentLevel.set(savedLevel);
    }

    /**
     * Checks if the current score beats the high score and saves it if allowed.
     * <p>
     * This operation is skipped if {@code savingEnabled} is false (e.g., in Zen Mode).
     * </p>
     *
     * @return true if a new high score was set and saved; false otherwise.
     */
    public boolean checkAndSaveHighestScore() {
        if (!savingEnabled) return false;

        int current = score.scoreProperty().get();
        if (current > highestScore) {
            highestScore = current;
            fileHandler.saveHighScore(highestScore);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the highest score loaded from the file.
     *
     * @return the high score integer.
     */
    public int getHighestScore() {
        return highestScore;
    }

    /**
     * Exposes the score property for UI binding.
     * @return the generic IntegerProperty for the score.
     */
    public IntegerProperty scoreProperty(){ return score.scoreProperty(); }

    /**
     * Exposes the level property for UI binding.
     * @return the generic IntegerProperty for the level.
     */
    public IntegerProperty levelProperty(){ return currentLevel; }

    /**
     * Resets the score and level to initial values (0 and 1).
     * <p>
     * Also reloads the high score from disk to ensure the session starts with
     * the most up-to-date record.
     * </p>
     */
    public void reset(){
        totalLinesCleared = 0;
        currentLevel.set(1);
        score.reset();
        highestScore = fileHandler.loadHighScore();
    }

    /**
     * Calculates score rewards for clearing lines and handles level progression.
     * <p>
     * If {@code levelingEnabled} is true, this method checks if the total lines cleared
     * exceed the threshold for the next level and updates the level property accordingly.
     * </p>
     *
     * @param linesRemoved the number of lines cleared in a single drop (1-4).
     */
    public void onRowsCleared(int linesRemoved){
        if (linesRemoved > 0){
            int scoreBonus = SCORE_PER_LINE * linesRemoved * linesRemoved;
            score.add(scoreBonus);
            totalLinesCleared += linesRemoved;

            if (levelingEnabled) {
                while (totalLinesCleared >= currentLevel.get() * LINES_PER_LEVEL_UP){
                    currentLevel.set(currentLevel.get() + 1);
                }
            }
        }
    }

    /**
     * Adds points for performing a hard drop.
     *
     * @param rowsDropped the number of rows the brick fell instantly.
     */
    public void onHardDrop(int rowsDropped){
        score.add(rowsDropped);
    }

    /**
     * Adds points for performing a soft drop.
     */
    public void onSoftDrop(){
        score.add(1);
    }
}