package com.comp2042.logic.board;

import com.comp2042.logic.BrickRotator;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.model.ClearRow;
import com.comp2042.model.NextShapeInfo;
import com.comp2042.model.ViewData;
import com.comp2042.util.GameConfiguration;
import com.comp2042.util.MatrixOperations;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the {@link Board} interface representing the standard Tetris grid.
 * <p>
 * This class serves as the core <b>Model</b> component for the gameplay physics. It manages:
 * <ul>
 * <li>The 2D integer matrix representing the grid state (0 for empty, values for colors).</li>
 * <li>The active falling {@link Brick} and its coordinates.</li>
 * <li>Collision detection logic (walls, floor, and other blocks).</li>
 * <li>Complex mechanics like Wall Kicks (SRS) and Hard Drops.</li>
 * </ul>
 * <p>
 * It delegates mathematical matrix operations to {@link MatrixOperations} to keep this class focused on game rules.
 */
public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private Brick heldBrick;
    private boolean canHold;

    /**
     * Constructs a new board with specific dimensions.
     * Initializes the brick generator and spawner.
     *
     * @param height the number of rows (including hidden rows)
     * @param width the number of columns
     */
    public SimpleBoard(int height, int width) {
        this.height = height;
        this.width = width;
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        this.heldBrick = null;
        this.canHold = true;
    }

    /**
     * Overwrites the current board matrix with a saved state.
     * This allows the Memento Pattern to "undo" moves.
     *
     * @param savedGrid the matrix state to restore
     */
    @Override
    public void restoreState(int[][] savedGrid) {
        for (int i = 0; i < height; i++) {
            System.arraycopy(savedGrid[i], 0, currentGameMatrix[i], 0, width);
        }
    }

    /**
     * Resets the active brick to the top-center spawn position.
     * Used when refreshing the game state or undoing a move.
     */
    @Override
    public void resetCurrentBrick() {
        this.currentOffset = new Point(getStartX(), 0);
        brickRotator.setBrick(brickRotator.getBrick());
    }

    /**
     * Swaps the current falling brick with the held brick.
     * <p>
     * If no brick is held, the current brick is stored and a new one spawns.
     * If a swap occurs, the brick position resets to the spawn point.
     * The hold action can only be performed once per turn.
     * </p>
     *
     * @return true if the new/swapped brick immediately collides (Game Over condition)
     */
    @Override
    public boolean holdCurrentBrick() {
        if (!canHold) {
            return false;
        }
        boolean isGameOver = false; // Variable to track game-over state
        if (heldBrick == null) {
            heldBrick = brickRotator.getBrick();
            isGameOver = createNewBrick();
        } else {
            Brick currentBrick = brickRotator.getBrick();
            brickRotator.setBrick(heldBrick);
            heldBrick = currentBrick;
            currentOffset = new Point(getStartX(), 0);

            // Instead of calling newGame(), set the game-over flag.
            if (MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY())) {
                isGameOver = true;
            }
        }

        canHold = false;
        return isGameOver; // Return the final game-over state to the GameController
    }

    /**
     * Spawns a new random brick from the generator at the top of the board.
     *
     * @return true if the new brick immediately collides with existing blocks (Game Over)
     */
    @Override
    public boolean createNewBrick() {
        this.canHold = true;
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(getStartX(), 0);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Calculates the horizontal center for spawning new bricks.
     * Uses config offsets to align correctly.
     */
    private int getStartX() {
        return width / 2 - GameConfiguration.SPAWN_X_OFFSET;
    }

    /**
     * Resets the entire board state for a new game.
     * Clears the matrix, hold slot, and generates a fresh brick.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        heldBrick = null;
        canHold = true;
        createNewBrick();
    }

    /**
     * Packages the current game state into a ViewData object for the UI.
     * Includes calculations for the "Ghost Piece" (where the block would land).
     *
     * @return a snapshot of the board, active piece, and next pieces
     */
    @Override
    public ViewData getViewData() {
        int ghostY = getDropPosition();
        int[][] holdData = heldBrick != null ? heldBrick.getShapeMatrix().getFirst() : new int[4][4];

        List<Brick> upcomingBricks = brickGenerator.getUpcomingBricks();

        List<int[][]> upcomingData = new ArrayList<>();
        for (Brick b : upcomingBricks) {
            upcomingData.add(b.getShapeMatrix().getFirst()); // Get the default shape
        }
        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                upcomingData,
                ghostY,
                holdData
        );
    }

    @Override
    public boolean moveBrickDown() {
        return tryMove(0, 1);
    }

    @Override
    public boolean moveBrickLeft() {
        return tryMove(-1, 0);
    }

    @Override
    public boolean moveBrickRight() {
        return tryMove(1, 0);
    }

    /**
     * Helper method to attempt a move.
     * Checks collision using {@link MatrixOperations#intersect}.
     *
     * @param dx change in x
     * @param dy change in y
     * @return true if move valid and applied
     */
    private boolean tryMove(int dx, int dy) {
        Point p = new Point(currentOffset);
        p.translate(dx, dy);

        boolean conflict = MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) p.getX(),
                (int) p.getY()
        );

        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Rotates the brick counter-clockwise with SRS (Super Rotation System) wall kicks.
     * Attempts to rotate; if blocked, tries specific offset positions (kicks) to find a valid fit.
     *
     * @return true if rotation was successful
     */
    @Override
    public boolean rotateLeftBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int[][] shape = nextShape.getShape();
        Point[] kickOffsets;

        if (brickRotator.getBrick().getClass().getSimpleName().equals("IBrick")) {
            kickOffsets = new Point[]{
                    new Point(0, 0),
                    new Point(-2, 0),
                    new Point(1, 0),
                    new Point(-2, 1),
                    new Point(1, -2)
            };
        } else {
            kickOffsets = new Point[]{
                    new Point(0, 0),
                    new Point(-1, 0),
                    new Point(1, 0),
                    new Point(0, -1),
                    new Point(-1, -1),
                    new Point(1, -1)
            };
        }

        for (Point kick : kickOffsets) {
            Point checkPosition = new Point(currentOffset);
            checkPosition.translate((int) kick.getX(), (int) kick.getY());

            boolean conflict = MatrixOperations.intersect(
                    currentGameMatrix,
                    shape,
                    (int) checkPosition.getX(),
                    (int) checkPosition.getY()
            );

            if (!conflict) {
                brickRotator.setCurrentShape(nextShape.getPosition());
                currentOffset = checkPosition;
                return true;
            }
        }
        return false;
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Locks the current active brick into the board matrix.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Scans the board for filled rows and clears them.
     *
     * @return a ClearRow object containing stats about lines removed
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    /**
     * Instantly drops the current brick to the lowest valid position.
     *
     * @return the number of rows dropped (used for scoring)
     */
    @Override
    public int hardDrop() {
        int rowsDropped = 0;
        while (moveBrickDown()) {
            rowsDropped++;
        }
        return rowsDropped;
    }

    /**
     * Calculates the Y position where the current brick would land if dropped instantly.
     */
    private int getDropPosition() {
        int y = (int) currentOffset.getY();
        while (!MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), y + 1)) {
            y++;
        }
        return y;
    }

    /**
     * Checks if the stack has reached the "Danger Zone" threshold defined in config.
     *
     * @return true if blocks are detected in the upper hidden rows
     */
    @Override
    public boolean isDangerState() {
        int hiddenRows = GameConfiguration.HIDDEN_ROWS;
        int dangerZoneHeight = GameConfiguration.DANGER_ZONE_HEIGHT;

        if (height < hiddenRows + dangerZoneHeight) {
            return false;
        }

        for (int i = hiddenRows; i < hiddenRows + dangerZoneHeight; i++) {
            for (int j = 0; j < width; j++) {
                if (currentGameMatrix[i][j] != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}