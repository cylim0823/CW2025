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

/**
 * Handles the graphical rendering of the game state onto the JavaFX scene.
 * <p>
 * This class acts as the <b>View</b> component in the MVC architecture. It is responsible for:
 * <ul>
 * <li>Drawing the main game grid and the static background blocks.</li>
 * <li>Rendering the active falling piece and its "Ghost" projection.</li>
 * <li>Displaying the queue of upcoming bricks.</li>
 * <li>Displaying the currently held brick.</li>
 * </ul>
 * <p>
 * It uses the {@link ColorManager} to determine the visual style of each block ID.
 */
public class GameRenderer {

    private final GridPane gamePanel;
    private final VBox nextBricksContainer;
    private final GridPane holdBrickPanel;
    private final ColorManager colorManager;

    // Drawing buffers (Rectangle objects are reused to improve performance)
    private Rectangle[][] displayMatrix;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] activeRectangles;
    private List<Rectangle[][]> nextBrickRectangleList;
    private Rectangle[][] holdBrickRectangles;

    /**
     * Constructs a new GameRenderer.
     *
     * @param gamePanel the JavaFX grid for the main board
     * @param nextBricksContainer the VBox to hold next piece previews
     * @param holdBrickPanel the grid for the hold piece
     * @param colorManager the manager providing color definitions
     */
    public GameRenderer(GridPane gamePanel, VBox nextBricksContainer, GridPane holdBrickPanel, ColorManager colorManager){
        this.gamePanel = gamePanel;
        this.nextBricksContainer = nextBricksContainer;
        this.holdBrickPanel = holdBrickPanel;
        this.colorManager = colorManager;
    }

    /**
     * Initializes the grid of Rectangle objects for the game view.
     * <p>
     * This creates the scene graph nodes (Rectangles) once at startup to avoid
     * object creation overhead during the game loop. It sets up layers for the background,
     * ghost piece, and active piece.
     * </p>
     *
     * @param boardMatrix the initial state of the board (used to size the grid)
     */
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

                // Layer 1: Background Grid (Static blocks)
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(Color.BLACK);
                rectangle.setStroke(gridColor);
                rectangle.setStrokeWidth(strokeWidth);
                rectangle.setStrokeType(StrokeType.INSIDE);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - hiddenRows);

                // Layer 2: Ghost Piece (Transparent overlay)
                Rectangle ghostRectangle = new Rectangle(brickSize, brickSize);
                ghostRectangle.setFill(Color.TRANSPARENT);
                ghostRectangle.setStroke(Color.BLACK);
                ghostRectangle.setStrokeWidth(strokeWidth);
                ghostRectangle.setStrokeType(StrokeType.INSIDE);
                ghostRectangle.setVisible(false);
                ghostRectangles[i][j] = ghostRectangle;
                gamePanel.add(ghostRectangle, j, i - hiddenRows);

                // Layer 3: Active Piece (Falling block)
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

    /**
     * Redraws the dynamic elements of the game (Active Piece, Ghost Piece, Next/Hold queues).
     * This is called every frame or whenever the board state changes.
     *
     * @param brick the current snapshot of the game state (ViewData)
     */
    public void refreshBrick(ViewData brick) {
        int hiddenRows = GameConfiguration.HIDDEN_ROWS;

        // Clear previous ghost/active rendering
        for (int i = hiddenRows; i < ghostRectangles.length; i++) {
            for (int j = 0; j < ghostRectangles[i].length; j++) {
                ghostRectangles[i][j].setVisible(false);
                activeRectangles[i][j].setFill(Color.TRANSPARENT);
                activeRectangles[i][j].setStroke(Color.TRANSPARENT);
            }
        }

        int[][] brickData = brick.getBrickData();

        // Draw Ghost Piece
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int x = brick.getxPosition() + j;
                    int y = brick.getGhostYPosition() + i;
                    if (y >= hiddenRows && y < ghostRectangles.length && x >= 0 && x < ghostRectangles[0].length) {

                        ghostRectangles[y][x].setFill(colorManager.getGhostPaint(brickData[i][j]));
                        ghostRectangles[y][x].setVisible(true);
                    }
                }
            }
        }

        // Draw Active Piece
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

    /**
     * Updates the "Next Pieces" sidebar.
     *
     * @param brick the current game state containing the list of upcoming bricks
     */
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

    /**
     * Updates the "Hold" panel with the currently held brick.
     *
     * @param brick the current game state containing the hold brick data
     */
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

    /**
     * Updates the static background grid (locked blocks).
     *
     * @param board the matrix of locked blocks
     */
    public void refreshGameBackground(int[][] board) {
        int hiddenRows = GameConfiguration.HIDDEN_ROWS;
        for (int i = hiddenRows; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Sets the color of a specific grid cell based on its value.
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(colorManager.getPaint(color));
    }
}