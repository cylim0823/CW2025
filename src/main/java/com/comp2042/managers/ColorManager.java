package com.comp2042.managers;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.Map;

/**
 * Manages the color palette for the game elements.
 * <p>
 * This class centralizes visual styling, decoupling the {@link com.comp2042.ui.GameRenderer}
 * from hardcoded color values. It maps the logical ID of a brick (e.g., 1 for I-Brick)
 * to its corresponding JavaFX {@link Paint} object.
 * </p>
 */
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
     * Retrieves the specific color associated with a brick ID.
     *
     * @param brickId the integer identifier of the brick type.
     * @return the corresponding {@link Paint} object, or {@link Color#WHITE} if the ID is unknown.
     */
    public Paint getPaint(int brickId) {
        return BRICK_COLORS.getOrDefault(brickId, Color.WHITE);
    }

    /**
     * Returns a color for the "Ghost Piece" projection.
     * <p>
     * Note: Currently unused as the GameRenderer uses a static grey overlay,
     * but preserved for potential future features where ghosts match the brick color.
     * </p>
     *
     * @param brickId the integer identifier of the brick type.
     * @return a semi-transparent version of the brick's color.
     */
    public Paint getGhostPaint(int brickId) {
        return Color.rgb(100, 100, 100, 0.4);
    }
}