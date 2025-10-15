package logic.pieces;

import java.net.URL;

import javax.swing.ImageIcon;

public class King extends Piece {

    public King(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }

    @Override
    public char getSymbol() {
        return isWhite ? 'K' : 'k';
    }
    
    @Override
    public void loadImage() {
    	String filename = isWhite ? "white_king.png" : "black_king.png";
        URL url = getClass().getResource("/resources/images/" + filename);
        if (url == null) {
            System.out.println("Cannot find: " + filename);
        } else {
            image = new ImageIcon(url);
        }
    }


    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {
        // 1. Không di chuyển nếu trùng vị trí
        if (newRow == row && newCol == col) return false;

        // 2. Kiểm tra 1 ô xung quanh
        int rowDiff = Math.abs(newRow - row);
        int colDiff = Math.abs(newCol - col);

        if (rowDiff > 1 || colDiff > 1) return false; // quá 1 ô → không hợp lệ

        // 3. Kiểm tra ô đích: trống hoặc quân đối phương
        Piece target = board[newRow][newCol];
        return (target == null || target.isWhite() != this.isWhite);
    }
}
