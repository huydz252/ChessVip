package logic;

import logic.board.Board;
import logic.pieces.Piece;

public class GameController {
    private Board board;
    private boolean whiteTurn;

    public GameController() {
        board = new Board();
        whiteTurn = true;
    }

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board.getPiece(fromRow, fromCol);
        if (piece == null || piece.isWhite() != whiteTurn) return false;
        if (!piece.isValidMove(toRow, toCol, board.getBoard())) return false;

        board.movePiece(fromRow, fromCol, toRow, toCol);
        whiteTurn = !whiteTurn;
        return true;
    }

    public Board getBoard() { return board; }
}
