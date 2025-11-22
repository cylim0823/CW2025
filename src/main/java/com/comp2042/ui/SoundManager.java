package com.comp2042.ui;

import com.comp2042.model.GameObserver;
import com.comp2042.model.ViewData;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager implements GameObserver {

    private MediaPlayer backgroundMusic;
    private AudioClip clearSound;
    private AudioClip tetrisSound;
    private AudioClip dropSound;
    private AudioClip gameOverSound;
    private boolean isMuted = false;
    private long lastDropTime = 0;

    public SoundManager() {
        // Background Music
        try {
            URL musicUrl = getClass().getResource("/sound/background_music.mp3");
            if (musicUrl != null) {
                backgroundMusic = new MediaPlayer(new Media(musicUrl.toExternalForm()));
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.setVolume(0.7);
            } else {
                System.err.println("Background music not found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }

        // Line Clear Sound
        try {
            URL clearUrl = getClass().getResource("/sound/line_clear.wav");
            if (clearUrl != null) {
                clearSound = new AudioClip(clearUrl.toExternalForm());
            } else {
                System.err.println("Line clear sound not found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading line clear sound: " + e.getMessage());
        }

        // Tetris Clear Sound
        try {
            URL tetrisUrl = getClass().getResource("/sound/tetris_clear.wav");
            if (tetrisUrl != null) {
                tetrisSound = new AudioClip(tetrisUrl.toExternalForm());
            } else {
                System.err.println("Tetris sound not found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading tetris sound: " + e.getMessage());
        }

        // Drop Sound
        try {
            URL dropUrl = getClass().getResource("/sound/drop.wav");
            if (dropUrl != null) {
                dropSound = new AudioClip(dropUrl.toExternalForm());
            } else {
                System.err.println("Drop sound not found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading drop sound: " + e.getMessage());
        }

        // Game Over Sound
        try {
            URL overUrl = getClass().getResource("/sound/game_over.wav");
            if (overUrl != null) {
                gameOverSound = new AudioClip(overUrl.toExternalForm());
            } else {
                System.err.println("Game over sound not found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading game over sound: " + e.getMessage());
        }
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

        if (lines >= 4) {
            if (tetrisSound != null) tetrisSound.play(0.7);
        } else {
            if (clearSound != null) clearSound.play(0.5);
        }
    }

    @Override
    public void onBrickDropped() {
        long currentTime = System.currentTimeMillis();
        // 150ms cooldown
        if (currentTime - lastDropTime > 150) {
            if (dropSound != null && !isMuted) {
                dropSound.play(0.3);
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

    // Unused Observer methods
    @Override public void onBoardUpdated(ViewData viewData) {}
    @Override public void onGameBackgroundUpdated(int[][] boardMatrix) {}
    @Override public void onScoreUpdated(int score) {}
    @Override public void onLevelUpdated(int level) {}
}