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

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private Brick heldBrick;
    private boolean canHold;

    public SimpleBoard(int height, int width) {
        this.height = height;
        this.width = width;
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        this.heldBrick = null;
        this.canHold = true;
    }

    @Override
    public void restoreState(int[][] savedGrid) {
        for (int i = 0; i < height; i++) {
            System.arraycopy(savedGrid[i], 0, currentGameMatrix[i], 0, width);
        }
    }

    @Override
    public void resetCurrentBrick() {
        this.currentOffset = new Point(getStartX(), 0);
        brickRotator.setBrick(brickRotator.getBrick());
    }

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

    @Override
    public boolean createNewBrick() {
        this.canHold = true;
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(getStartX(), 0);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    private int getStartX() {
        return width / 2 - GameConfiguration.SPAWN_X_OFFSET;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        heldBrick = null;
        canHold = true;
        createNewBrick();
    }

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

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public int hardDrop() {
        int rowsDropped = 0;
        while (moveBrickDown()) {
            rowsDropped++;
        }
        return rowsDropped;
    }

    private int getDropPosition() {
        int y = (int) currentOffset.getY();
        while (!MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), y + 1)) {
            y++;
        }
        return y;
    }

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