package logic.move;
import java.io.Serializable;

import logic.pieces.Piece;

public class Move implements Serializable{
	private Piece piece; 
    private int fromRow, fromCol; 
    private int toRow, toCol;    
    private Piece captured;       

    public Move(Piece piece, int toRow, int toCol, Piece captured) {
        this.piece = piece;
        this.toRow = toRow;
        this.toCol = toCol;
        this.captured = captured;
        this.fromRow = piece.getRow();
        this.fromCol = piece.getCol();
    }
    

    public Piece getPiece() { return piece; }
    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }
    public Piece getCaptured() { return captured; }


    public void execute(Piece[][] board) {
        board[toRow][toCol] = piece;    
        board[fromRow][fromCol] = null;  
        piece.setPosition(toRow, toCol); 
    }

    public void undo(Piece[][] board) {
        board[fromRow][fromCol] = piece;
        board[toRow][toCol] = captured;
        piece.setPosition(fromRow, fromCol);
    }
    
}

