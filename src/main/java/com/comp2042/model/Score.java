package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Encapsulates the game score using JavaFX properties to support data binding.
 * <p>
 * <b>Design Pattern: Observer (via JavaFX)</b><br>
 * By wrapping the score in an {@link IntegerProperty}, this class allows the View
 * ({@link com.comp2042.ui.GameRenderer}) to listen for changes automatically.
 * When the score is updated here, the UI label updates itself without manual intervention.
 * </p>
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Retrieves the observable property object for the score.
     * <p>
     * Callers can attach listeners to this property to react to score changes
     * in real-time.
     * </p>
     *
     * @return the {@link IntegerProperty} wrapping the score value.
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Increases the current score by a specific amount.
     *
     * @param i the points to add to the current total.
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score to zero.
     * Called when starting a new game or restarting in Zen Mode.
     */
    public void reset() {
        score.setValue(0);
    }
}