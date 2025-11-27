package com.comp2042.util;

/**
 * Centralized configuration for the entire game.
 * Holds constants for file paths, physics settings, and audio settings.
 * This prevents "Magic Numbers" and "Magic Strings" from scattering across all the files.
 */
public class GameConfiguration {

    // Board Settings
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 24;
    public static final int HIDDEN_ROWS = 4; // The top rows where bricks spawn

    // Game Logic Rules
    public static final int LINES_FOR_TETRIS = 4;
    public static final int SPAWN_X_OFFSET = 2;

    // Level Difficulty
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

    // Gameplay rules
    public static final int MAX_UNDO_PER_GAME = 3;
    public static final int DANGER_ZONE_HEIGHT = 5;

    // Visual effects
    public static final double SHAKE_INTENSITY = 2.5;
    public static final double SHAKE_DURATION_MS = 50; // Shake update speed
    public static final String FONT_PATH = "/font/digital.ttf";
    public static final double FONT_SIZE_DEFAULT = 38;

    // Rendering
    public static final int BRICK_SIZE = 25;
    public static final int PREVIEW_COUNT = 4;
    public static final int BRICK_MATRIX_SIZE = 4; // Size of the brick grid (4x4)
    public static final double GRID_STROKE_WIDTH = 1.0;
    public static final String COLOR_GRID_HEX = "#2b2b2b";

    // Audio Paths
    public static final String PATH_MUSIC_BG = "/audio/background_music.mp3";
    public static final String PATH_AUDIO_CLEAR = "/audio/line_clear.wav";
    public static final String PATH_AUDIO_TETRIS = "/audio/tetris_clear.wav";
    public static final String PATH_AUDIO_DROP = "/audio/drop.wav";
    public static final String PATH_AUDIO_GAMEOVER = "/audio/game_over.wav";

    // Audio Volumes
    public static final double VOL_MUSIC = 0.7;
    public static final double VOL_CLEAR = 0.5;
    public static final double VOL_TETRIS = 0.7;
    public static final double VOL_DROP = 0.3;

    // Audio Speeds
    public static final long DROP_SOUND_COOLDOWN_MS = 150;
    public static final double MUSIC_SPEED_NORMAL = 1.0;
    public static final double MUSIC_SPEED_DANGER = 1.25;

    // Scoring and Data
    public static final int SCORE_PER_LINE = 50;
    public static final int LINES_PER_LEVEL_UP = 10;
    public static final String PATH_HIGHEST_SCORE = "highest_score.txt";
}