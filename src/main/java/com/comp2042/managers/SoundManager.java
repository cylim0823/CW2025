package com.comp2042.managers;

import com.comp2042.model.GameObserver;
import com.comp2042.model.ViewData;
import com.comp2042.util.GameConfiguration;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * Manages the audio subsystem, handling background music playback and sound effects.
 * <p>
 * <b>Design Pattern: Observer</b><br>
 * This class implements {@link GameObserver}, allowing it to react autonomously to game events
 * (like line clears or game over) triggered by the {@link com.comp2042.controllers.GameController}.
 * This decouples the game logic from the specific audio implementation.
 * </p>
 * <p>
 * It handles resource loading safely and manages global audio states like muting and
 * playback speed adjustments during high-intensity moments.
 * </p>
 */
public class SoundManager implements GameObserver {

    private final MediaPlayer backgroundMusic;
    private final AudioClip clearSound;
    private final AudioClip tetrisSound;
    private final AudioClip dropSound;
    private final AudioClip gameOverSound;

    private boolean isMuted = false;
    private long lastDropTime = 0;

    /**
     * Constructs the SoundManager and loads all audio assets.
     * <p>
     * Paths and volumes are retrieved from {@link GameConfiguration}.
     * Resources are loaded with error handling to ensure the game can continue
     * silently if an audio file is missing.
     * </p>
     */
    public SoundManager() {
        backgroundMusic = loadMediaPlayer(GameConfiguration.PATH_MUSIC_BG, GameConfiguration.VOL_MUSIC);
        if (backgroundMusic != null) {
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        }

        clearSound = loadAudioClip(GameConfiguration.PATH_AUDIO_CLEAR);
        tetrisSound = loadAudioClip(GameConfiguration.PATH_AUDIO_TETRIS);
        dropSound = loadAudioClip(GameConfiguration.PATH_AUDIO_DROP);
        gameOverSound = loadAudioClip(GameConfiguration.PATH_AUDIO_GAMEOVER);
    }

    /**
     * Helper method to safely load a background music file.
     *
     * @param path relative path to the resource
     * @param volume initial volume level (0.0 to 1.0)
     * @return a configured MediaPlayer, or null if loading failed
     */
    private MediaPlayer loadMediaPlayer(String path, double volume) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                MediaPlayer player = new MediaPlayer(new Media(url.toExternalForm()));
                player.setVolume(volume);
                return player;
            } else {
                System.err.println("Audio file not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error loading music (" + path + "): " + e.getMessage());
        }
        return null;
    }

    /**
     * Helper method to safely load short sound effects.
     *
     * @param path relative path to the resource
     * @return a loaded AudioClip, or null if loading failed
     */
    private AudioClip loadAudioClip(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                return new AudioClip(url.toExternalForm());
            } else {
                System.err.println("Audio file not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error loading sound (" + path + "): " + e.getMessage());
        }
        return null;
    }

    /** Starts background music playback if not muted. */
    public void playMusic() {
        if (backgroundMusic != null && !isMuted) {
            backgroundMusic.play();
        }
    }

    /** Stops the background music. */
    public void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    /**
     * Toggles the mute state for all audio.
     * Pauses background music immediately if muted, or resumes it if unmuted.
     */
    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            if (backgroundMusic != null) backgroundMusic.pause();
        } else {
            if (backgroundMusic != null) backgroundMusic.play();
        }
    }

    // Observer Events

    /**
     * plays a sound effect when lines are cleared.
     * Selects a special "Tetris" sound if 4 lines are cleared at once.
     *
     * @param lines number of lines cleared
     * @param message notification message (unused here)
     */
    @Override
    public void onLineCleared(int lines, String message) {
        if (isMuted) return;

        if (lines >= GameConfiguration.LINES_FOR_TETRIS) {
            if (tetrisSound != null) tetrisSound.play(GameConfiguration.VOL_TETRIS);
        } else {
            if (clearSound != null) clearSound.play(GameConfiguration.VOL_CLEAR);
        }
    }

    /**
     * Plays a landing sound when a brick hits the stack.
     * <p>
     * <b>Debouncing Logic:</b> Uses {@code lastDropTime} to enforce a cooldown defined by
     * {@link GameConfiguration#DROP_SOUND_COOLDOWN_MS}. This prevents audio distortion
     * when bricks land in rapid succession (e.g., during hard drops or high speeds).
     * </p>
     */
    @Override
    public void onBrickDropped() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastDropTime > GameConfiguration.DROP_SOUND_COOLDOWN_MS) {
            if (dropSound != null && !isMuted) {
                dropSound.play(GameConfiguration.VOL_DROP);
                lastDropTime = currentTime;
            }
        }
    }

    /** Stops music and plays the Game Over sound effect. */
    @Override
    public void onGameOver() {
        stopMusic();
        if (gameOverSound != null && !isMuted) {
            gameOverSound.play();
        }
    }

    /**
     * Adjusts the background music speed to reflect the game's tension.
     *
     * @param isDanger true to increase playback rate; false to reset to normal speed.
     */
    public void setDangerMode(boolean isDanger) {
        if (backgroundMusic == null) return;

        double targetRate;
        if (isDanger) {
            targetRate = GameConfiguration.MUSIC_SPEED_DANGER;
        } else {
            targetRate = GameConfiguration.MUSIC_SPEED_NORMAL;
        }

        if (backgroundMusic.getRate() == targetRate) {
            return;
        }

        backgroundMusic.setRate(targetRate);
    }

    @Override
    public void onDangerStateChanged(boolean isDanger) {
        setDangerMode(isDanger);
    }

    // Unused Observer methods required by interface contract
    @Override public void onBoardUpdated(ViewData viewData) {}
    @Override public void onGameBackgroundUpdated(int[][] boardMatrix) {}
    @Override public void onScoreUpdated(int score) {}
    @Override public void onLevelUpdated(int level) {}
}