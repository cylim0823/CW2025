package com.comp2042.ui;

import com.comp2042.managers.ColorManager;
import com.comp2042.model.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import java.util.ArrayList;
import java.util.List;

public class GameRenderer {

    private static final int BRICK_SIZE = 25;
    private static final int HIDDEN_ROWS = 4;
    private static final int PREVIEW_COUNT = 4;

    private final GridPane gamePanel;
    private final VBox nextBricksContainer;
    private final GridPane holdBrickPanel;
    private final ColorManager colorManager;

    // For drawing
    private Rectangle[][] displayMatrix;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] activeRectangles;
    private List<Rectangle[][]> nextBrickRectangleList;
    private Rectangle[][] holdBrickRectangles;

    public GameRenderer(GridPane gamePanel, VBox nextBricksContainer, GridPane holdBrickPanel, ColorManager colorManager){
        this.gamePanel = gamePanel;
        this.nextBricksContainer = nextBricksContainer;
        this.holdBrickPanel = holdBrickPanel;
        this.colorManager = colorManager;
    }

    public void initGameView(int[][] boardMatrix) {

        holdBrickPanel.getStyleClass().clear();
        holdBrickPanel.getStyleClass().add("holdBrick");

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        ghostRectangles = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        activeRectangles = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        for (int i = HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {

                // Grid
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.BLACK);
                rectangle.setStroke(Color.web("#2b2b2b"));
                rectangle.setStrokeWidth(1);
                rectangle.setStrokeType(StrokeType.INSIDE);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - HIDDEN_ROWS);

                // Ghost Piece
                Rectangle ghostRectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                ghostRectangle.setFill(Color.TRANSPARENT);
                ghostRectangle.setStroke(Color.BLACK);
                ghostRectangle.setStrokeWidth(1);
                ghostRectangle.setStrokeType(StrokeType.INSIDE);
                ghostRectangle.setVisible(false);
                ghostRectangles[i][j] = ghostRectangle;
                gamePanel.add(ghostRectangle, j, i - HIDDEN_ROWS);

                // Active piece
                Rectangle activeRectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                activeRectangle.setFill(Color.TRANSPARENT);
                activeRectangle.setStrokeWidth(1);
                activeRectangle.setStrokeType(StrokeType.INSIDE);
                activeRectangles[i][j] = activeRectangle;
                gamePanel.add(activeRectangle, j, i - HIDDEN_ROWS);
            }
        }

        // Initialize next/hold panels
        nextBricksContainer.getChildren().clear();
        nextBrickRectangleList = new ArrayList<>();

        for (int n = 0; n < PREVIEW_COUNT; n++) {
            GridPane previewPanel = new GridPane();
            previewPanel.setStyle("-fx-background-color: transparent;");

            Rectangle[][] previewRectangles = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setStrokeWidth(1);
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    previewRectangles[i][j] = rectangle;
                    previewPanel.add(rectangle, j, i);
                }
            }

            nextBrickRectangleList.add(previewRectangles);
            nextBricksContainer.getChildren().add(previewPanel);
        }

        holdBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(1);
                rectangle.setStrokeType(StrokeType.INSIDE);
                holdBrickRectangles[i][j] = rectangle;
                holdBrickPanel.add(rectangle, j, i);
            }
        }
    }

    public void refreshBrick(ViewData brick) {
        for (int i = HIDDEN_ROWS; i < ghostRectangles.length; i++) {
            for (int j = 0; j < ghostRectangles[i].length; j++) {
                ghostRectangles[i][j].setVisible(false);
                activeRectangles[i][j].setFill(Color.TRANSPARENT);
                activeRectangles[i][j].setStroke(Color.TRANSPARENT);
            }
        }

        int[][] brickData = brick.getBrickData();

        // Draw ghost
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int x = brick.getxPosition() + j;
                    int y = brick.getGhostYPosition() + i;
                    if (y >= HIDDEN_ROWS && y < ghostRectangles.length && x >= 0 && x < ghostRectangles[0].length) {
                        ghostRectangles[y][x].setFill(colorManager.getGhostPaint(brickData[i][j]));
                        ghostRectangles[y][x].setVisible(true);
                    }
                }
            }
        }

        // Draw active piece
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int x = brick.getxPosition() + j;
                    int y = brick.getyPosition() + i;
                    if (y >= HIDDEN_ROWS && y < activeRectangles.length && x >= 0 && x < activeRectangles[0].length) {
                        activeRectangles[y][x].setFill(colorManager.getPaint(brickData[i][j]));
                        activeRectangles[y][x].setStroke(Color.BLACK);
                    }
                }
            }
        }

        refreshUpcomingBricks(brick);
        refreshHoldBrick(brick);
    }

    public void refreshUpcomingBricks(ViewData brick) {
        List<int[][]> dataList = brick.getUpcomingBricksData();

        for (int panelIndex = 0; panelIndex < nextBrickRectangleList.size(); panelIndex++) {
            Rectangle[][] currentPanelRects = nextBrickRectangleList.get(panelIndex);

            if (panelIndex < dataList.size()) {
                int[][] data = dataList.get(panelIndex);
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        currentPanelRects[i][j].setFill(colorManager.getPaint(data[i][j]));
                        if(data[i][j] != 0) currentPanelRects[i][j].setStroke(Color.BLACK);
                        else currentPanelRects[i][j].setStroke(Color.TRANSPARENT);
                    }
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        currentPanelRects[i][j].setFill(Color.TRANSPARENT);
                        currentPanelRects[i][j].setStroke(Color.TRANSPARENT);
                    }
                }
            }
        }
    }

    public void refreshHoldBrick(ViewData brick) {
        if (brick.getHoldBrickData() != null) {
            int[][] data = brick.getHoldBrickData();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    holdBrickRectangles[i][j].setFill(colorManager.getPaint(data[i][j]));
                    if(data[i][j] != 0) holdBrickRectangles[i][j].setStroke(Color.BLACK);
                    else holdBrickRectangles[i][j].setStroke(Color.TRANSPARENT);
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    holdBrickRectangles[i][j].setFill(Color.TRANSPARENT);
                    holdBrickRectangles[i][j].setStroke(Color.TRANSPARENT);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = HIDDEN_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(colorManager.getPaint(color));
    }
}