package logic.pieces;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Bishop extends Piece implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Bishop(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }

    @Override
    public char getSymbol() {
        return isWhite ? 'B' : 'b';
    }

    @Override
    public void loadImage() {
        String filename = isWhite ? "white_bishop.png" : "black_bishop.png";
        java.net.URL url = getClass().getResource("/resources/images/" + filename);
        if (url == null) {
            System.out.println("Cannot find: " + filename);
        } else {
            image = new ImageIcon(url);
        }
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {
        if (newRow == row && newCol == col)
            return false;

        // Tượng đi chéo
        if (Math.abs(newRow - row) != Math.abs(newCol - col))
            return false;

        int rowDir = Integer.compare(newRow, row);
        int colDir = Integer.compare(newCol, col);

        int currentRow = row + rowDir;
        int currentCol = col + colDir;

        while (currentRow != newRow && currentCol != newCol) {
            if (board[currentRow][currentCol] != null)
                return false;
            currentRow += rowDir;
            currentCol += colDir;
        }

        Piece target = board[newRow][newCol];
        return (target == null || target.isWhite() != this.isWhite);
    }
}
