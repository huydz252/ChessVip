package logic.pieces;

import java.io.Serializable;
import java.net.URL;

import javax.swing.ImageIcon;

public class Knight extends Piece implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Knight(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }

    @Override
    public char getSymbol() {
        return isWhite ? 'K' : 'k';
    }
    
    @Override
    public void loadImage() {
        String filename = isWhite ? "white_knight.png" : "black_knight.png";
        URL url = getClass().getResource("/resources/images/" + filename);
        if (url == null) {
            System.out.println("Cannot find: " + filename);
        } else {
            image = new ImageIcon(url);
        }
    }

    
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {
        // Không di chuyển nếu trùng vị trí
        if (newRow == row && newCol == col) return false;

        // 8 hướng đi của Knight
        int[] dRow = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] dCol = {1, 2, 2, 1, -1, -2, -2, -1};

        boolean valid = false;
        for (int i = 0; i < 8; i++) {
            if (row + dRow[i] == newRow && col + dCol[i] == newCol) {
                valid = true;
                break;
            }
        }

        if (!valid) return false;

        // Kiểm tra ô đến: trống hoặc quân khác màu
        Piece target = board[newRow][newCol];
        return (target == null || target.isWhite() != this.isWhite);
    }

}
