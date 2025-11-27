package com.comp2042.managers;

import com.comp2042.model.GameObserver;
import com.comp2042.model.ViewData;
import com.comp2042.util.GameConfiguration;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager implements GameObserver {

    private final MediaPlayer backgroundMusic;
    private final AudioClip clearSound;
    private final AudioClip tetrisSound;
    private final AudioClip dropSound;
    private final AudioClip gameOverSound;

    private boolean isMuted = false;
    private long lastDropTime = 0;

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
     * Helper method to load MediaPlayers safely.
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
     * Helper method to load AudioClips safely.
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

    public void playMusic() {
        if (backgroundMusic != null && !isMuted) {
            backgroundMusic.play();
        }
    }

    public void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            if (backgroundMusic != null) backgroundMusic.pause();
        } else {
            if (backgroundMusic != null) backgroundMusic.play();
        }
    }

    // Observer Events

    @Override
    public void onLineCleared(int lines, String message) {
        if (isMuted) return;

        if (lines >= GameConfiguration.LINES_FOR_TETRIS) {
            if (tetrisSound != null) tetrisSound.play(GameConfiguration.VOL_TETRIS);
        } else {
            if (clearSound != null) clearSound.play(GameConfiguration.VOL_CLEAR);
        }
    }

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

    @Override
    public void onGameOver() {
        stopMusic();
        if (gameOverSound != null && !isMuted) {
            gameOverSound.play();
        }
    }

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

    // Unused Observer methods
    @Override public void onBoardUpdated(ViewData viewData) {}
    @Override public void onGameBackgroundUpdated(int[][] boardMatrix) {}
    @Override public void onScoreUpdated(int score) {}
    @Override public void onLevelUpdated(int level) {}
}