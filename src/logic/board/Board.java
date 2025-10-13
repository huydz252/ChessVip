package logic.board;

import logic.pieces.Rook;
import logic.pieces.Pawn;
import logic.pieces.Piece;

public class Board {
    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        setupPieces();
    }

    private void setupPieces() {
        // --- Quân trắng ---
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Pawn(true, 6, col);
        }
        board[7][0] = new Rook(true, 7, 0);
        board[7][7] = new Rook(true, 7, 7);

        // --- Quân đen ---
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(false, 1, col);
        }
        board[0][0] = new Rook(false, 0, 0);
        board[0][7] = new Rook(false, 0, 7);
    }

    public Piece getPiece(int row, int col) {
        if (!isValidPosition(row, col)) return null;
        return board[row][col];
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) return;

        Piece piece = board[fromRow][fromCol];
        if (piece != null) {
            board[toRow][toCol] = piece;
            board[fromRow][fromCol] = null;
            piece.setPosition(toRow, toCol);
        }
    }

    public Piece[][] getBoard() {
        return board;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
