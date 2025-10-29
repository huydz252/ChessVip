package logic.pieces;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Queen extends Piece implements Serializable{

    public Queen(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }

    @Override
    public char getSymbol() {
        return isWhite ? 'Q' : 'q';
    }

    @Override
    public void loadImage() {
        String filename = isWhite ? "white_queen.png" : "black_queen.png";
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

        int rowDiff = Math.abs(newRow - row);
        int colDiff = Math.abs(newCol - col);

        // Hướng đi hợp lệ: theo hàng/cột (như Rook) hoặc chéo (như Bishop)
        if (rowDiff != 0 && colDiff != 0 && rowDiff != colDiff)
            return false;

        int rowDir = Integer.compare(newRow, row);
        int colDir = Integer.compare(newCol, col);

        int currentRow = row + rowDir;
        int currentCol = col + colDir;

        while (currentRow != newRow || currentCol != newCol) {
            if (board[currentRow][currentCol] != null)
                return false;
            currentRow += rowDir;
            currentCol += colDir;
        }

        Piece target = board[newRow][newCol];
        return (target == null || target.isWhite() != this.isWhite);
    }
}
