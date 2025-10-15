package logic.move;
import logic.pieces.Piece;

public class Move {
    public int fromRow, fromCol;
    public int toRow, toCol;
    public Piece movedPiece;
    public Piece captured;

    public Move(Piece movedPiece, int toRow, int toCol, Piece captured) {
        this.movedPiece = movedPiece;
        this.fromRow = movedPiece.getRow();
        this.fromCol = movedPiece.getCol();
        this.toRow = toRow;
        this.toCol = toCol;
        this.captured = captured;
    }

    // Thực hiện nước đi
    public void execute(Piece[][] board) {
        board[toRow][toCol] = movedPiece;     // đặt quân đến ô mới
        board[fromRow][fromCol] = null;       // xoá ô cũ
        movedPiece.setPosition(toRow, toCol);
    }

    // Undo (nếu cần)
    public void undo(Piece[][] board) {
        board[fromRow][fromCol] = movedPiece;
        board[toRow][toCol] = captured;
        movedPiece.setPosition(fromRow, fromCol);
    }
}

