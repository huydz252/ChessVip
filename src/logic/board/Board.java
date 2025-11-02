package logic.board;

import logic.pieces.King;
import logic.pieces.Queen;
import logic.pieces.Piece;
import logic.move.Move;   // nhớ import class Move
import java.util.ArrayList;
import java.util.List;

public class Board {
    private Piece[][] board;
    private List<Move> moveHistory;  // lịch sử nước đi

    public Board() {
        board = new Piece[8][8];
        moveHistory = new ArrayList<>();
        setupPieces();
    }

    private void setupPieces() {
    	
    	
    	/*
        // --- Quân trắng ---
        // Hàng 6: Pawn
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Pawn(true, 6, col);
            board[6][col].loadImage();
        }
        // Hàng 7: Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
        board[7][0] = new Rook(true, 7, 0);
        board[7][0].loadImage();
        
        board[7][1] = new Knight(true, 7, 1);
        board[7][1].loadImage();
        
        board[7][2] = new Bishop(true, 7, 2);
        board[7][2].loadImage();
        
        board[7][3] = new Queen(true, 7, 3);
        board[7][3].loadImage();
        
        board[7][4] = new King(true, 7, 4);
        board[7][4].loadImage();
        
        board[7][5] = new Bishop(true, 7, 5);
        board[7][5].loadImage();
        
        board[7][6] = new Knight(true, 7, 6);
        board[7][6].loadImage();
        
        board[7][7] = new Rook(true, 7, 7);
        board[7][7].loadImage();

        // --- Quân đen ---
        // Hàng 1: Pawn
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(false, 1, col);
            board[1][col].loadImage();
        }
        // Hàng 0: Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
        board[0][0] = new Rook(false, 0, 0);
        board[0][0].loadImage();
        
        board[0][1] = new Knight(false, 0, 1);
        board[0][1].loadImage();
        
        board[0][2] = new Bishop(false, 0, 2);
        board[0][2].loadImage();
        
        board[0][3] = new Queen(false, 0, 3);
        board[0][3].loadImage();
        
        board[0][4] = new King(false, 0, 4);
        board[0][4].loadImage();
        
        board[0][5] = new Bishop(false, 0, 5);
        board[0][5].loadImage();
        
        board[0][6] = new Knight(false, 0, 6);
        board[0][6].loadImage();
        
        board[0][7] = new Rook(false, 0, 7);
        board[0][7].loadImage();
        
        */
    	
    	
        //test chiếu tướng:
    	/*
        
        // Vua Đen bị cô lập ở góc 
        board[0][7] = new King(false, 0, 7); // h8
        board[0][7].loadImage(); 
        
        // Tốt đen cản đường thoát của Vua (tùy chọn)
        board[1][7] = new Pawn(false, 1, 7); // h7
        board[1][7].loadImage();

        // --- Quân Trắng (White) ---
        
        // Hậu Trắng ở e5 - Vị trí để tấn công ô f7
        board[4][4] = new Queen(true, 4, 4); // e5 
        board[4][4].loadImage();
        
        // Tượng Trắng ở d6 - Kiểm soát g8, ô thoát tiềm năng.
        board[2][3] = new Bishop(true, 2, 3); // d6
        board[2][3].loadImage();
        
        // Vua Trắng (ở một vị trí an toàn, không tham gia tấn công)
        board[7][4] = new King(true, 7, 4); // e1
        board[7][4].loadImage();
        
        board[7][6] = new Rook(true, 7, 6); // e1
        board[7][6].loadImage();
        
        */
        
        
        
        // --- TEST HÒA CỜ (STALEMATE) ---
        // Kịch bản: Trắng đi 1 nước (Hậu c6 -> b6) sẽ dẫn đến hòa cờ.
        
        // --- Quân Đen ---
        // Vua Đen ở góc a8
        board[0][0] = new King(false, 0, 0); // a8
        board[0][0].loadImage(); 
        
        // --- Quân Trắng ---
        
        // Vua Trắng ở c7, kiểm soát ô b7 và b8
        board[1][2] = new King(true, 1, 2); // c7
        board[1][2].loadImage();
        
        // Hậu Trắng ở c6, CHUẨN BỊ di chuyển đến b6
        board[2][2] = new Queen(true, 2, 2); // c6
        board[2][2].loadImage();
        
        // MỤC TIÊU: Bạn (Trắng) di chuyển Hậu từ c6 -> b6.
        // Sau nước đi đó:
        // 1. Vua Đen (a8) KHÔNG bị chiếu.
        // 2. Các ô Vua Đen có thể đi (a7, b7, b8) đều bị Hậu (b6) và Vua (c7) kiểm soát.
        // -> HÒA CỜ (Stalemate).
        
    }
    
    
    public Piece getPiece(int row, int col) {
        if (!isValidPosition(row, col)) return null;
        return board[row][col];
    }

    public void executeMove(Move move) {
        move.execute(board);          // thực hiện nước đi
        moveHistory.add(move);        // lưu lịch sử
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) return false;
        else {
        	Move lastMove = moveHistory.remove(moveHistory.size() - 1);
            lastMove.undo(board);
            return true;
        }
    }

    public Piece[][] getBoard() {
        return board;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }
    
    //lay danh sach quan 1 phia
    public List<Piece> getAllPieces(boolean whiteKing){
    	
    	List<Piece> pieces = new ArrayList<Piece>();
    	for(int r = 0; r < 8; r++) {
    		for(int c = 0; c < 8; c++) {
    			if(board[r][c]!= null && board[r][c].isWhite() == whiteKing) {
    				pieces.add(board[r][c]);
    			}
    		}
    	}
    	return pieces;
    }

	public boolean isGameOver() {
		
		return false;
	} 
}




















