package com.comp2042.model;

import java.util.Arrays;

public final class BoardMemento {

    private final int[][] boardState;
    private final int score;
    private final int level;

    public BoardMemento(int[][] board, int score, int level) {
        this.score = score;
        this.level = level;
        this.boardState = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            this.boardState[i] = Arrays.copyOf(board[i], board[i].length);
        }
    }

    public int[][] getBoardState() {
        int[][] copy = new int[boardState.length][];
        for (int i = 0; i < boardState.length; i++) {
            copy[i] = Arrays.copyOf(boardState[i], boardState[i].length);
        }
        return copy;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }
}
