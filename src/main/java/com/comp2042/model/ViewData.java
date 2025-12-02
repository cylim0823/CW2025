package com.comp2042.model;

import com.comp2042.util.MatrixOperations;
import java.util.ArrayList;
import java.util.List;

/**
 * An immutable Data Transfer Object (DTO) containing a snapshot of the board's dynamic state.
 * <p>
 * <b>Architectural Role:</b> This class acts as the bridge between the Model ({@link com.comp2042.logic.board.Board})
 * and the View ({@link com.comp2042.ui.GameRenderer}). It packages all necessary rendering data
 * (active piece, ghost position, hold piece, next queue) into a single object.
 * </p>
 * <p>
 * <b>Immutability:</b> To ensure thread safety and data integrity, this class performs
 * defensive copying on all mutable array fields. This prevents the View from accidentally
 * modifying the internal state of the Model.
 * </p>
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> upcomingBricksData;
    private final int ghostYPosition;
    private final int[][] holdBrickData;

    /**
     * Constructs a new snapshot of the board state.
     *
     * @param brickData matrix representing the current falling brick shape.
     * @param xPosition current X coordinate of the falling brick.
     * @param yPosition current Y coordinate of the falling brick.
     * @param upcomingBricksData list of matrices for the "Next Piece" preview.
     * @param ghostYPosition calculated Y coordinate where the brick would land (Ghost Piece).
     * @param holdBrickData matrix representing the brick currently in the "Hold" slot.
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, List<int[][]> upcomingBricksData, int ghostYPosition, int[][] holdBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.upcomingBricksData = upcomingBricksData;
        this.ghostYPosition = ghostYPosition;
        this.holdBrickData = holdBrickData;
    }

    /**
     * Retrieves the shape matrix of the currently falling brick.
     *
     * @return a safe copy of the 2D integer array.
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Retrieves the current horizontal position of the falling brick.
     * @return the grid column index.
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Retrieves the current vertical position of the falling brick.
     * @return the grid row index.
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Retrieves the list of shapes for the "Next Piece" preview sidebar.
     * <p>
     * <b>Deep Copy:</b> Iterates through the list and copies every matrix to ensure
     * full encapsulation.
     * </p>
     *
     * @return a new list containing safe copies of the upcoming brick matrices.
     */
    public List<int[][]> getUpcomingBricksData() {
        List<int[][]> copiedList = new ArrayList<>();
        for (int[][] matrix : upcomingBricksData) {
            copiedList.add(MatrixOperations.copy(matrix));
        }
        return copiedList;
    }

    /**
     * Retrieves the calculated Y-coordinate for the Ghost Piece.
     * <p>
     * The renderer uses this to draw a semi-transparent hint showing where the
     * brick will land if a Hard Drop is performed.
     * </p>
     *
     * @return the projected landing row index.
     */
    public int getGhostYPosition() {
        return ghostYPosition;
    }

    /**
     * Retrieves the shape matrix of the held brick.
     *
     * @return a safe copy of the hold piece matrix, or an empty matrix if nothing is held.
     */
    public int[][] getHoldBrickData() {
        return MatrixOperations.copy(holdBrickData);
    }
}