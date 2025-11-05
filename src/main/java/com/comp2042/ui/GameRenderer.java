package com.comp2042.ui;

import com.comp2042.model.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.w3c.dom.css.Rect;

public class GameRenderer {

    private static final int BRICK_SIZE = 20;

    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final GridPane nextBrickPanel;
    private final GridPane holdBrickPanel;

    // For drawing
    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] nextBrickRectangles;
    private Rectangle[][] holdBrickRectangles;

    // Constructor
    public GameRenderer(GridPane gamePanel, GridPane brickPanel, GridPane nextBrickPanel, GridPane holdBrickPanel){
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.nextBrickPanel = nextBrickPanel;
        this.holdBrickPanel = holdBrickPanel;
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        ghostRectangles = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
                Rectangle ghostRectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                ghostRectangle.setFill(Color.TRANSPARENT);
                ghostRectangles[i][j] = ghostRectangle;
                gamePanel.add(ghostRectangle, j, i - 2);
            }
        }
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
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
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
    }

    public void refreshBrick(ViewData brick) { // Changed to public
        // if (isPause.getValue() == Boolean.FALSE) { // Logic will stay in GuiController
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
        }
        for (int i = 2; i < ghostRectangles.length; i++) {
            for (int j = 0; j < ghostRectangles[i].length; j++) {
                ghostRectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int x = brick.getxPosition() + j;
                    int y = brick.getGhostYPosition() + i;
                    if (y >= 2) {
                        ghostRectangles[y][x].setFill(getGhostFillColor(brickData[i][j]));
                    }
                }
            }
        }
        refreshNextBrickPreview(brick);
        refreshHoldBrick(brick);
        // }
    }

    public void refreshNextBrickPreview(ViewData brick) { // Changed to public
        if (brick.getNextBrickData() != null) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    nextBrickRectangles[i][j].setFill(getFillColor(brick.getNextBrickData()[i][j]));
                }
            }
        }
    }

    public void refreshHoldBrick(ViewData brick) { // Changed to public
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
                returnPaint = Color.TRANSPARENT;
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
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.3);
        }
        return returnPaint;
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
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
