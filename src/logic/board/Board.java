package logic.board;

import logic.pieces.Rook;
import logic.pieces.Pawn;
import logic.pieces.Piece;
import logic.move.Move;   // nhớ import class Move
import java.util.ArrayList;
import java.util.List;

public class Board {
    private Piece[][] board;
    private List<Move> moveHistory;  // lịch sử nước đi

    public Board() {
        board = new Piece[8][8];
        moveHistory = new ArrayList<>();
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

    public void executeMove(Move move) {
        move.execute(board);          // thực hiện nước đi
        moveHistory.add(move);        // lưu lịch sử
    }

    public void undoLastMove() {
        if (moveHistory.isEmpty()) return;
        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        lastMove.undo(board);
    }

    public Piece[][] getBoard() {
        return board;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }
}
