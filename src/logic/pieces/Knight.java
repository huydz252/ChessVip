package logic.pieces;

public class Knight extends Piece{
	public Knight(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }

    @Override
    public char getSymbol() {
        return isWhite ? 'K' : 'k';
    }
    
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {
    	//không di chuyển nếu trùng vị trí
    	if(newRow == row && newCol == col) {
    		return false;
    	}
    	
    	//xác định tọa độ mới, khác với xe (di chuyển theo 1 hàng or 1 cột)
    	//mà di chuyển theo hình chữ L, có thể "nhảy" qua các quân cờ chắn đường
    	//8 hướng đi của mã tại 1 vị trí bất kì (chưa xét ngoài vùng bàn cờ)
    	int[] dRow = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] dCol = {1, 2, 2, 1, -1, -2, -2, -1};
        
        for(int i = 0; i <8; i++) {
        	row += dRow[i];
        	col += dCol[i];
        	///k cần check 
        }
  
    	//return true nếu 1 trong 2 điều kiện đúng.
    	Piece target = board[newRow][newCol];
    	return (target == null || target.isWhite != this.isWhite);
    }
}
