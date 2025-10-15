package logic;

import logic.board.Board;
import logic.pieces.Piece;
import logic.move.Move;  // nhớ import class Move

public class GameController {
    private Board board;
    private boolean whiteTurn;

    public GameController() {
        board = new Board();
        whiteTurn = true;
    }

    // Thực hiện nước đi
    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board.getPiece(fromRow, fromCol);

        // Kiểm tra ô có quân và lượt đi
        if (piece == null || piece.isWhite() != whiteTurn) return false;

        // Kiểm tra nước đi hợp lệ
        if (!piece.isValidMove(toRow, toCol, board.getBoard())) return false;

        // Lưu quân bị bắt (nếu có)
        Piece captured = board.getPiece(toRow, toCol);

        // Tạo Move
        Move move = new Move(piece, toRow, toCol, captured);

        // Thực hiện nước đi và lưu lịch sử
        board.executeMove(move);

        // Đổi lượt
        whiteTurn = !whiteTurn;

        return true;
    }

    // Undo nước đi cuối cùng
    public void undoLastMove() {
        board.undoLastMove();
        whiteTurn = !whiteTurn;  // đảo lại lượt
    }

    public Board getBoard() {
        return board;
    }
}
