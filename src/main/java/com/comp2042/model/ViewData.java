package com.comp2042.model;

import com.comp2042.util.MatrixOperations;
import java.util.ArrayList;
import java.util.List;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> upcomingBricksData;
    private final int ghostYPosition;
    private final int[][] holdBrickData;

    public ViewData(int[][] brickData, int xPosition, int yPosition, List<int[][]> upcomingBricksData, int ghostYPosition, int[][] holdBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.upcomingBricksData = upcomingBricksData;
        this.ghostYPosition = ghostYPosition;
        this.holdBrickData = holdBrickData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public List<int[][]> getUpcomingBricksData() {
        List<int[][]> copiedList = new ArrayList<>();
        for (int[][] matrix : upcomingBricksData) {
            copiedList.add(MatrixOperations.copy(matrix));
        }
        return copiedList;
    }

    public int getGhostYPosition() {
        return ghostYPosition;
    }

    public int[][] getHoldBrickData() {
        return MatrixOperations.copy(holdBrickData);
    }
}