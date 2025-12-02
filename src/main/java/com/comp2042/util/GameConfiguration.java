package com.comp2042.util;

/**
 * The centralized configuration registry for the entire application.
 * <p>
 * <b>Architectural Role:</b> This class serves as the single source of truth for all
 * game constants. By centralizing these values, the application avoids the
 * "Magic Number" and "Magic String" anti-patterns.
 * </p>
 * <p>
 * <b>Maintainability:</b> This design allows developers to tune game balance (speed,
 * scoring, difficulty), adjust audio levels, or modify UI dimensions by changing
 * values in one file, without needing to hunt through logic classes.
 * </p>
 * @author Chen Yu
 * @version 1.0
 */
public class GameConfiguration {

    // --- BOARD SETTINGS ---

    /** The number of columns in the play grid. */
    public static final int BOARD_WIDTH = 10;

    /** The total number of rows (visible + hidden). */
    public static final int BOARD_HEIGHT = 24;

    /**
     * The number of top rows that are invisible to the player.
     * Bricks spawn here to prevent "Game Over" immediately upon spawning.
     */
    public static final int HIDDEN_ROWS = 4;

    // --- GAME LOGIC RULES ---

    /** The specific number of lines required to trigger a "Tetris" score bonus and sound. */
    public static final int LINES_FOR_TETRIS = 4;

    /** The horizontal offset to center a new brick when it spawns. */
    public static final int SPAWN_X_OFFSET = 2;

    // --- DIFFICULTY ---

    /**
     * Defines the gravity speed (tick interval in milliseconds) for each level.
     * Index 0 = Level 1 (400ms), Index 9 = Level 10 (80ms).
     */
    public static final java.util.List<Long> LEVEL_SPEEDS = java.util.List.of(
            400L, // Level 1
            360L, // Level 2
            320L, // Level 3
            280L, // Level 4
            240L, // Level 5
            200L, // Level 6
            160L, // Level 7
            120L, // Level 8
            100L, // Level 9
            80L   // Level 10+ (Max Speed)
    );

    // --- GAMEPLAY RULES ---

    /**
     * The maximum number of undos allowed in Normal Mode.
     * Used by {@link com.comp2042.logic.mode.NormalMode}.
     */
    public static final int UNDO_LIMIT_NORMAL = 3;

    /** The distance from the top (including hidden rows) that triggers "Danger Mode". */
    public static final int DANGER_ZONE_HEIGHT = 5;

    // --- VISUAL EFFECTS ---

    /** The pixel offset magnitude for the screen shake effect. */
    public static final double SHAKE_INTENSITY = 2.5;

    /** How frequently (in ms) the shake position updates. */
    public static final double SHAKE_DURATION_MS = 50;

    /** Path to the custom digital font resource. */
    public static final String FONT_PATH = "/font/digital.ttf";

    /** Default font size for UI labels. */
    public static final double FONT_SIZE_DEFAULT = 38;

    // --- RENDERING ---

    /** The width/height of a single grid cell in pixels. */
    public static final int BRICK_SIZE = 25;

    /** Number of upcoming pieces to display in the sidebar. */
    public static final int PREVIEW_COUNT = 4;

    /** The dimension (NxN) of the 2D array used for a single brick shape. */
    public static final int BRICK_MATRIX_SIZE = 4;

    /** Thickness of the grid lines drawn on the board. */
    public static final double GRID_STROKE_WIDTH = 1.0;

    /** Hex code for the dark grey grid lines. */
    public static final String COLOR_GRID_HEX = "#2b2b2b";

    // --- AUDIO PATHS ---
    public static final String PATH_MUSIC_BG = "/audio/background_music.mp3";
    public static final String PATH_AUDIO_CLEAR = "/audio/line_clear.wav";
    public static final String PATH_AUDIO_TETRIS = "/audio/tetris_clear.wav";
    public static final String PATH_AUDIO_DROP = "/audio/drop.wav";
    public static final String PATH_AUDIO_GAMEOVER = "/audio/game_over.wav";

    // --- AUDIO VOLUMES (0.0 to 1.0) ---
    public static final double VOL_MUSIC = 0.7;
    public static final double VOL_CLEAR = 0.5;
    public static final double VOL_TETRIS = 0.7;
    public static final double VOL_DROP = 0.3;

    // --- AUDIO SPEEDS ---

    /** Minimum time (ms) between drop sounds to prevent audio distortion. */
    public static final long DROP_SOUND_COOLDOWN_MS = 150;

    /** Standard music playback rate. */
    public static final double MUSIC_SPEED_NORMAL = 1.0;

    /** Accelerated music playback rate during danger state. */
    public static final double MUSIC_SPEED_DANGER = 1.25;

    // --- SCORING & DATA ---

    /** Base score awarded per line cleared (before multipliers). */
    public static final int SCORE_PER_LINE = 50;

    /** Total lines required to advance to the next level. */
    public static final int LINES_PER_LEVEL_UP = 10;

    /** Local file path for persisting high scores. */
    public static final String PATH_HIGHEST_SCORE = "highest_score.txt";
}