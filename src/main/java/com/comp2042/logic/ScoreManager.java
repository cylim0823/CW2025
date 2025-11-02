package com.comp2042.logic;

import com.comp2042.model.Score;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ScoreManager {

    private int totalLinesCleared = 0;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private final Score score = new Score();

    // Constructor
    public ScoreManager() {

    }

    // Getters
    public IntegerProperty scoreProperty(){
        return score.scoreProperty();
    }

    public IntegerProperty levelProperty(){
        return currentLevel;
    }

    public void reset(){
        totalLinesCleared = 0;
        currentLevel.set(1);
        score.reset();
    }

    public int onRowsCleared(int linesRemoved){
        if (linesRemoved > 0){
            int scoreBonus = 50 * linesRemoved * linesRemoved;
            score.add(scoreBonus);

            totalLinesCleared += linesRemoved;
            while (totalLinesCleared >= currentLevel.get() * 10){
                currentLevel.set(currentLevel.get() + 1);
            }
            return scoreBonus;
        }
        return 0;
    }

    public void onHardDrop(int rowsDropped){
        int scoreBonus = rowsDropped * 2;
        score.add(scoreBonus);
    }

    public void onSoftDrop(){
        score.add(1);
    }

}
