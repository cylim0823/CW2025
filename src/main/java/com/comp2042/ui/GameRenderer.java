package com.comp2042.ui;

import com.comp2042.managers.ColorManager;
import com.comp2042.model.ViewData;
import com.comp2042.util.GameConfiguration;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import java.util.ArrayList;
import java.util.List;

public class GameRenderer {

    private static final Color GHOST_COLOR_FILL = Color.rgb(100, 100, 100, 0.4);  // Transparent grey

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

        int hiddenRows = GameConfiguration.HIDDEN_ROWS;
        int brickSize = GameConfiguration.BRICK_SIZE;
        int matrixSize = GameConfiguration.BRICK_MATRIX_SIZE;
        double strokeWidth = GameConfiguration.GRID_STROKE_WIDTH;
        Color gridColor = Color.web(GameConfiguration.COLOR_GRID_HEX);

        for (int i = hiddenRows; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {

                // Grid
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(Color.BLACK);
                rectangle.setStroke(gridColor);
                rectangle.setStrokeWidth(strokeWidth);
                rectangle.setStrokeType(StrokeType.INSIDE);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - hiddenRows);

                // Ghost Piece
                Rectangle ghostRectangle = new Rectangle(brickSize, brickSize);
                ghostRectangle.setFill(Color.TRANSPARENT);
                ghostRectangle.setStroke(Color.BLACK);
                ghostRectangle.setStrokeWidth(strokeWidth);
                ghostRectangle.setStrokeType(StrokeType.INSIDE);
                ghostRectangle.setVisible(false);
                ghostRectangles[i][j] = ghostRectangle;
                gamePanel.add(ghostRectangle, j, i - hiddenRows);

                // Active piece
                Rectangle activeRectangle = new Rectangle(brickSize, brickSize);
                activeRectangle.setFill(Color.TRANSPARENT);
                activeRectangle.setStrokeWidth(strokeWidth);
                activeRectangle.setStrokeType(StrokeType.INSIDE);
                activeRectangles[i][j] = activeRectangle;
                gamePanel.add(activeRectangle, j, i - hiddenRows);
            }
        }

        // Initialize Next Bricks Panel
        nextBricksContainer.getChildren().clear();
        nextBrickRectangleList = new ArrayList<>();

        for (int n = 0; n < GameConfiguration.PREVIEW_COUNT; n++) {
            GridPane previewPanel = new GridPane();
            previewPanel.setStyle("-fx-background-color: transparent;");

            Rectangle[][] previewRectangles = new Rectangle[matrixSize][matrixSize];
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    Rectangle rectangle = new Rectangle(brickSize, brickSize);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setStrokeWidth(strokeWidth);
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    previewRectangles[i][j] = rectangle;
                    previewPanel.add(rectangle, j, i);
                }
            }

            nextBrickRectangleList.add(previewRectangles);
            nextBricksContainer.getChildren().add(previewPanel);
        }

        // Initialize Hold Brick Panel
        holdBrickRectangles = new Rectangle[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                rectangle.setStrokeType(StrokeType.INSIDE);
                holdBrickRectangles[i][j] = rectangle;
                holdBrickPanel.add(rectangle, j, i);
            }
        }
    }

    public void refreshBrick(ViewData brick) {
        int hiddenRows = GameConfiguration.HIDDEN_ROWS;

        // Clear previous state
        for (int i = hiddenRows; i < ghostRectangles.length; i++) {
            for (int j = 0; j < ghostRectangles[i].length; j++) {
                ghostRectangles[i][j].setVisible(false);
                activeRectangles[i][j].setFill(Color.TRANSPARENT);
                activeRectangles[i][j].setStroke(Color.TRANSPARENT);
            }
        }

        int[][] brickData = brick.getBrickData();

        // Draw Ghost (Transparent Grey)
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int x = brick.getxPosition() + j;
                    int y = brick.getGhostYPosition() + i;
                    if (y >= hiddenRows && y < ghostRectangles.length && x >= 0 && x < ghostRectangles[0].length) {
                        ghostRectangles[y][x].setFill(GHOST_COLOR_FILL);
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
                    if (y >= hiddenRows && y < activeRectangles.length && x >= 0 && x < activeRectangles[0].length) {
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
        int matrixSize = GameConfiguration.BRICK_MATRIX_SIZE;

        for (int panelIndex = 0; panelIndex < nextBrickRectangleList.size(); panelIndex++) {
            Rectangle[][] currentPanelRects = nextBrickRectangleList.get(panelIndex);

            if (panelIndex < dataList.size()) {
                int[][] data = dataList.get(panelIndex);
                for (int i = 0; i < matrixSize; i++) {
                    for (int j = 0; j < matrixSize; j++) {
                        currentPanelRects[i][j].setFill(colorManager.getPaint(data[i][j]));
                        if(data[i][j] != 0) currentPanelRects[i][j].setStroke(Color.BLACK);
                        else currentPanelRects[i][j].setStroke(Color.TRANSPARENT);
                    }
                }
            } else {
                for (int i = 0; i < matrixSize; i++) {
                    for (int j = 0; j < matrixSize; j++) {
                        currentPanelRects[i][j].setFill(Color.TRANSPARENT);
                        currentPanelRects[i][j].setStroke(Color.TRANSPARENT);
                    }
                }
            }
        }
    }

    public void refreshHoldBrick(ViewData brick) {
        int matrixSize = GameConfiguration.BRICK_MATRIX_SIZE;

        brick.getHoldBrickData();
        int[][] data = brick.getHoldBrickData();
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                holdBrickRectangles[i][j].setFill(colorManager.getPaint(data[i][j]));
                if(data[i][j] != 0) holdBrickRectangles[i][j].setStroke(Color.BLACK);
                else holdBrickRectangles[i][j].setStroke(Color.TRANSPARENT);
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        int hiddenRows = GameConfiguration.HIDDEN_ROWS;
        for (int i = hiddenRows; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(colorManager.getPaint(color));
    }
}