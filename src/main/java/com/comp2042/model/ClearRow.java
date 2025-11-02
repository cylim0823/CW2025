package com.comp2042.model;

import com.comp2042.util.MatrixOperations;

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;

    public ClearRow(int linesRemoved, int[][] newMatrix) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix); }
}
