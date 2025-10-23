package logic.move;
import logic.pieces.Piece;

public class Move {
	private Piece piece; // Quân cờ được di chuyển (nên dùng biến này)
    private int fromRow, fromCol; // Tọa độ cũ
    private int toRow, toCol;     // Tọa độ mới
    private Piece captured;       // Quân bị ăn

    public Move(Piece piece, int toRow, int toCol, Piece captured) {
        this.piece = piece;
        this.toRow = toRow;
        this.toCol = toCol;
        this.captured = captured;
        this.fromRow = piece.getRow();
        this.fromCol = piece.getCol();
    }
    

 // --- Getters Cần thiết (giữ lại và làm private) ---
    public Piece getPiece() { return piece; }
    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }
    public Piece getCaptured() { return captured; }



    // Thực hiện nước đi
    public void execute(Piece[][] board) {
        board[toRow][toCol] = piece;    
        board[fromRow][fromCol] = null;  
        piece.setPosition(toRow, toCol); 
    }

    // Undo 
    public void undo(Piece[][] board) {
        board[fromRow][fromCol] = piece;
        board[toRow][toCol] = captured;
        piece.setPosition(fromRow, fromCol);
    }
    
}

