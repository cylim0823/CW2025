package com.comp2042.managers;

import com.comp2042.model.Score;
import com.comp2042.util.GameConfiguration;
import com.comp2042.util.ScoreFileHandler;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ScoreManager {

    private static final int SCORE_PER_LINE = GameConfiguration.SCORE_PER_LINE;
    private static final int LINES_PER_LEVEL_UP = GameConfiguration.LINES_PER_LEVEL_UP;

    private int totalLinesCleared = 0;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private final Score score = new Score();
    private int highestScore = 0;
    private final ScoreFileHandler fileHandler;
    private boolean isZenMode = false;

    public ScoreManager() {
        this.fileHandler = new ScoreFileHandler(GameConfiguration.PATH_HIGHEST_SCORE);

        this.highestScore = fileHandler.loadHighScore();
    }

    public void setZenMode(boolean isZenMode){
        this.isZenMode = isZenMode;
    }

    public void restoreState(int savedScore, int savedLevel) {
        this.score.scoreProperty().set(savedScore);
        this.currentLevel.set(savedLevel);
    }

    public boolean checkAndSaveHighestScore() {
        if (isZenMode) return false;

        int current = score.scoreProperty().get();
        if (current > highestScore) {
            highestScore = current;
            fileHandler.saveHighScore(highestScore);
            return true;
        }
        return false;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public IntegerProperty scoreProperty(){ return score.scoreProperty(); }
    public IntegerProperty levelProperty(){ return currentLevel; }

    public void reset(){
        totalLinesCleared = 0;
        currentLevel.set(1);
        score.reset();
        highestScore = fileHandler.loadHighScore();
    }

    public void onRowsCleared(int linesRemoved){
        if (linesRemoved > 0){
            int scoreBonus = SCORE_PER_LINE * linesRemoved * linesRemoved;
            score.add(scoreBonus);
            totalLinesCleared += linesRemoved;

            if (!isZenMode){
                while (totalLinesCleared >= currentLevel.get() * LINES_PER_LEVEL_UP) {
                    currentLevel.set(currentLevel.get() + 1);
            }
            }
        }
    }

    public void onHardDrop(int rowsDropped){
        score.add(rowsDropped);
    }

    public void onSoftDrop(){
        score.add(1);
    }
}