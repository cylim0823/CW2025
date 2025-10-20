package com.comp2042.logic;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.model.ClearRow;
import com.comp2042.model.NextShapeInfo;
import com.comp2042.model.Score;
import com.comp2042.model.ViewData;
import com.comp2042.util.MatrixOperations;

import java.awt.Point;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick heldBrick;
    private boolean canHold;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        this.heldBrick = null;
        this.canHold = true;
    }

    public void holdCurrentBrick() {
        if (!canHold) {
            return;
        }

        if (heldBrick == null) {
            heldBrick = brickRotator.getBrick();
            createNewBrick();
        } else {
            Brick currentBrick = brickRotator.getBrick();
            brickRotator.setBrick(heldBrick);
            heldBrick = currentBrick;
            currentOffset = new Point(4, 0);
            if (MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY())) {
                newGame();
            }
        }
        canHold = false;
    }

    @Override
    public boolean createNewBrick() {
        this.canHold = true;
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 0);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        heldBrick = null;
        canHold = true;
        createNewBrick();
    }

    @Override
    public ViewData getViewData() {
        int ghostY = getDropPosition();
        int[][] holdData = heldBrick != null ? heldBrick.getShapeMatrix().get(0) : new int[4][4];
        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                brickGenerator.getNextBrick().getShapeMatrix().get(0),
                ghostY,
                holdData
        );
    }

    @Override
    public boolean moveBrickDown() {
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
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
    public Score getScore() {
        return score;
    }

    @Override
    public void hardDrop() {
        int rowsDropped = 0;
        while (moveBrickDown()) {
            rowsDropped++;
        }
        score.add(rowsDropped * 2);
    }

    private int getDropPosition() {
        int y = (int) currentOffset.getY();
        while (!MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), y + 1)) {
            y++;
        }
        return y;
    }
}