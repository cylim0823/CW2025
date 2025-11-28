package com.comp2042.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ScoreFileHandler {

    private final String filePath;

    public ScoreFileHandler(String filePath) {
        this.filePath = filePath;
    }

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
        return 0;
    }

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