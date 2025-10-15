package logic.pieces;

import javax.swing.ImageIcon;

public abstract class Piece {
	protected Boolean isWhite;
	protected int row, col;
	protected ImageIcon image;
	
	public Piece(boolean isWhite, int row, int col) {
        this.isWhite = isWhite;
        this.row = row;
        this.col = col;
        this.image = image;
    }

    public boolean isWhite() { 
    	return isWhite; 
    }
    
    public int getRow() { 
    	return row; 
    }
    
    public int getCol() { 
    	return col; 
    }
    
    public ImageIcon getImage() { 
    	return image; 
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public abstract void loadImage();

    public abstract char getSymbol(); 	//nhận diện quân nào và màu nào 
    
    public abstract boolean isValidMove(int newRow, int newCol, Piece[][] board);
}


