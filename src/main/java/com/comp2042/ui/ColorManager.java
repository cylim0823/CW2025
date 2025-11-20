package com.comp2042.ui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.Map;

public class ColorManager {

    private static final Map<Integer, Paint> BRICK_COLORS = Map.of(
            0, Color.BLACK,
            1, Color.AQUA,       // I-Brick
            2, Color.BLUEVIOLET, // J-Brick
            3, Color.DARKGREEN,  // L-Brick
            4, Color.YELLOW,     // O-Brick
            5, Color.RED,        // S-Brick
            6, Color.BEIGE,      // T-Brick
            7, Color.BURLYWOOD   // Z-Brick
    );

    /**
     * Returns the paint color for a specific brick ID.
     */
    public Paint getPaint(int brickId) {
        return BRICK_COLORS.getOrDefault(brickId, Color.WHITE);
    }

    /**
     * Returns the color for the Ghost piece.
     */
    public Paint getGhostPaint(int brickId) {
        Paint paint = getPaint(brickId);
        if (paint instanceof Color) {
            Color c = (Color) paint;
            // Return same color but with 50% opacity (0.5 alpha)
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.5);
        }
        return paint;
    }
}