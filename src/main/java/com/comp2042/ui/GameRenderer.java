package com.comp2042.ui;

import com.comp2042.model.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GameRenderer {

    private static final int BRICK_SIZE = 25;
    private static final int HIDDEN_ROWS = 4;
    private static final int GAP = 1;

    private final GridPane gamePanel;
    private final GridPane nextBrickPanel;
    private final GridPane holdBrickPanel;

    // For drawing
    private Rectangle[][] displayMatrix;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] activeRectangles;
    private Rectangle[][] nextBrickRectangles;
    private Rectangle[][] holdBrickRectangles;

    // Constructor
    public GameRenderer(GridPane gamePanel, GridPane nextBrickPanel, GridPane holdBrickPanel){
        this.gamePanel = gamePanel;
        this.nextBrickPanel = nextBrickPanel;
        this.holdBrickPanel = holdBrickPanel;
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {

        gamePanel.getStyleClass().clear(); // Clear old styles
        gamePanel.getStyleClass().add("gameBoard");

        nextBrickPanel.getStyleClass().clear();
        nextBrickPanel.getStyleClass().add("nextBrick");

        holdBrickPanel.getStyleClass().clear();
        holdBrickPanel.getStyleClass().add("holdBrick");


        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        ghostRectangles = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        activeRectangles = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        for (int i = HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.BLACK);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - HIDDEN_ROWS);

                Rectangle ghostRectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                ghostRectangle.setFill(Color.TRANSPARENT);
                ghostRectangles[i][j] = ghostRectangle;
                gamePanel.add(ghostRectangle, j, i - HIDDEN_ROWS);

                Rectangle activeRectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                activeRectangle.setFill(Color.TRANSPARENT);
                activeRectangles[i][j] = activeRectangle;
                gamePanel.add(activeRectangle, j, i - HIDDEN_ROWS);
            }
        }

        // Initialize next/hold panels
        nextBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                nextBrickRectangles[i][j] = rectangle;
                nextBrickPanel.add(rectangle, j, i);
            }
        }
        holdBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                holdBrickRectangles[i][j] = rectangle;
                holdBrickPanel.add(rectangle, j, i);
            }
        }
    }

    public void refreshBrick(ViewData brick) {
        // REPLACE the old clearing loop
        for (int i = HIDDEN_ROWS; i < ghostRectangles.length; i++) {
            for (int j = 0; j < ghostRectangles[i].length; j++) {
                ghostRectangles[i][j].setFill(Color.TRANSPARENT);
                activeRectangles[i][j].setFill(Color.TRANSPARENT); // Also clear active
            }
        }

        int[][] brickData = brick.getBrickData();

        // Draw Ghost (add boundary checks)
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int x = brick.getxPosition() + j;
                    int y = brick.getGhostYPosition() + i;
                    if (y >= HIDDEN_ROWS && y < ghostRectangles.length && x >= 0 && x < ghostRectangles[0].length) {
                        ghostRectangles[y][x].setFill(getGhostFillColor(brickData[i][j]));
                    }
                }
            }
        }

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int x = brick.getxPosition() + j;
                    int y = brick.getyPosition() + i;
                    if (y >= HIDDEN_ROWS && y < activeRectangles.length && x >= 0 && x < activeRectangles[0].length) {
                        activeRectangles[y][x].setFill(getFillColor(brickData[i][j]));
                    }
                }
            }
        }

        refreshNextBrickPreview(brick);
        refreshHoldBrick(brick);
    }

    public void refreshNextBrickPreview(ViewData brick) {
        if (brick.getNextBrickData() != null) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    nextBrickRectangles[i][j].setFill(getFillColor(brick.getNextBrickData()[i][j]));
                }
            }
        }
    }

    public void refreshHoldBrick(ViewData brick) {
        if (brick.getHoldBrickData() != null) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    holdBrickRectangles[i][j].setFill(getFillColor(brick.getHoldBrickData()[i][j]));
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    holdBrickRectangles[i][j].setFill(Color.TRANSPARENT);
                }
            }
        }
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.BLACK;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }

    private Paint getGhostFillColor(int i) {
        Paint returnPaint = getFillColor(i);
        if (returnPaint instanceof Color) {
            Color color = (Color) returnPaint;
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.5);
        }
        return returnPaint;
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = HIDDEN_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(0);
        rectangle.setArcWidth(0);
    }
}