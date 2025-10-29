package logic.pieces;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Pawn extends Piece implements Serializable{
	public Pawn(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }

    @Override
    public char getSymbol() {
        return isWhite ? 'P' : 'p';
    }
    
    @Override
    public void loadImage() {
        String filename = isWhite ? "white_pawn.png" : "black_pawn.png";
        image = new ImageIcon(getClass().getResource("/resources/images/" + filename));
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {
        int direction = isWhite ? -1 : 1;
        // Bước thẳng
        if (col == newCol && board[newRow][newCol] == null) {
            if (newRow == row + direction) return true;
            // Lần đầu có thể đi 2 ô
            if ((isWhite && row == 6 || !isWhite && row == 1) &&
                newRow == row + 2 * direction && board[row + direction][col] == null)
                return true;
        }
        // Ăn chéo
        if (Math.abs(col - newCol) == 1 && newRow == row + direction &&
            board[newRow][newCol] != null &&
            board[newRow][newCol].isWhite() != isWhite)
            return true;

        return false;
    }
}
