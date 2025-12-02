package com.comp2042.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles the persistence of high score data to the local file system.
 * <p>
 * <b>Design Principle: Single Responsibility (SRP)</b><br>
 * This class isolates the File I/O logic from the {@link com.comp2042.managers.ScoreManager}.
 * This ensures that the ScoreManager can focus purely on game logic (points, levels)
 * while this handler deals with the low-level details of reading and writing text files.
 * </p>
 */
public class ScoreFileHandler {

    private final String filePath;

    /**
     * Constructs a new handler for a specific file path.
     *
     * @param filePath the relative or absolute path to the high score text file.
     */
    public ScoreFileHandler(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads the high score from the file system.
     * <p>
     * If the file does not exist or cannot be read, this method fails gracefully
     * by returning 0, ensuring the game can continue without crashing.
     * </p>
     *
     * @return the integer high score, or 0 if no valid record is found.
     */
    public int loadHighScore() {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) {
                    int score = scanner.nextInt();
                    scanner.close();
                    return score;
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.err.println("Could not load highest score: " + e.getMessage());
        }
        return 0; // Default if file missing or error
    }

    /**
     * Writes a new high score to the file system.
     * <p>
     * This overwrites the existing file with the new score value.
     * Exceptions are caught and logged to standard error to prevent game interruption.
     * </p>
     *
     * @param score the new high score integer to save.
     */
    public void saveHighScore(int score) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(String.valueOf(score));
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not save highest score: " + e.getMessage());
        }
    }
}