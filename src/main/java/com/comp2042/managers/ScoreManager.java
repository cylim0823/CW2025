package com.comp2042.managers;

import com.comp2042.model.Score;
import com.comp2042.util.GameConfiguration;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ScoreManager {

    private static final int SCORE_PER_LINE = GameConfiguration.SCORE_PER_LINE;
    private static final int LINES_PER_LEVEL_UP = GameConfiguration.LINES_PER_LEVEL_UP;
    private static final String HIGHEST_SCORE_FILE = GameConfiguration.PATH_HIGHEST_SCORE;

    private int totalLinesCleared = 0;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private final Score score = new Score();
    private int highestScore = 0;

    // Constructor
    public ScoreManager() {
        loadHighestScore();
    }

    public void restoreState(int savedScore, int savedLevel) {
        this.score.scoreProperty().set(savedScore);
        this.currentLevel.set(savedLevel);
    }


    private void loadHighestScore() {
        try {
            File file = new File(HIGHEST_SCORE_FILE);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) {
                    highestScore = scanner.nextInt();
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.err.println("Could not load highest score.");
        }
    }

    private void saveHighestScore() {
        try {
            FileWriter writer = new FileWriter(HIGHEST_SCORE_FILE);
            writer.write(String.valueOf(highestScore));
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not save highest score.");
        }
    }

    public boolean checkAndSaveHighestScore() {
        int current = score.scoreProperty().get();
        if (current > highestScore) {
            highestScore = current;
            saveHighestScore();
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
        loadHighestScore();
    }

    public void onRowsCleared(int linesRemoved){
        if (linesRemoved > 0){
            int scoreBonus = SCORE_PER_LINE * linesRemoved * linesRemoved;
            score.add(scoreBonus);
            totalLinesCleared += linesRemoved;
            while (totalLinesCleared >= currentLevel.get() * LINES_PER_LEVEL_UP){
                currentLevel.set(currentLevel.get() + 1);
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