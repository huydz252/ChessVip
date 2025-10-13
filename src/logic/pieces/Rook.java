package logic.pieces;

public class Rook extends Piece {

    public Rook(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }

    @Override
    public char getSymbol() {
        return isWhite ? 'R' : 'r';
    }
    
    
    //override lại hàm isValidMove vì Rook extend Piece:
    //định nghĩa và kiểm tra cách di chuyển của quân xe (Rook)
    
    
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {
        // Không di chuyển nếu trùng vị trí
        if (newRow == row && newCol == col)
            return false;

        // Xe chỉ đi theo hàng hoặc cột
        if (newRow != row && newCol != col)
            return false;

        // Xác định hướng di chuyển (hàng hoặc cột)
        int rowDir = Integer.compare(newRow, row); // 1 nếu a > b, 0 nếu a == b, -1 nếu a < b.
        int colDir = Integer.compare(newCol, col);

        int currentRow = row + rowDir;
        int currentCol = col + colDir;
        
        //kiểm tra có bị chặn hay k (step = dir (=1) kiểm tra từng step
        while (currentRow != newRow || currentCol != newCol) {
            if (board[currentRow][currentCol] != null) {	//có quân cờ -> != null
                return false; // Bị cản
            }
            currentRow += rowDir;
            currentCol += colDir;
        }

        // Ô đến có thể trống hoặc chứa quân khác màu
        Piece target = board[newRow][newCol];
        return (target == null || target.isWhite() != this.isWhite);
    }
}
