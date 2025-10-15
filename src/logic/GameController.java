package logic;

import java.util.List;

import logic.board.Board;
import logic.pieces.King;
import logic.pieces.Piece;
import logic.move.Move;  // nhớ import class Move

public class GameController {
    private Board board;
    private boolean whiteTurn;

    public GameController() {
        board = new Board();
        whiteTurn = true;
    }
    
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    // Thực hiện nước đi
    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board.getPiece(fromRow, fromCol);

        // Kiểm tra ô có quân và lượt đi
        if (piece == null || piece.isWhite() != whiteTurn) return false;

        // Kiểm tra nước đi hợp lệ
        if (!piece.isValidMove(toRow, toCol, board.getBoard())) return false;
        
        //kiểm tra vua có bị check, checkmate sau nước đi đó không?
        if(isCheck(piece.isWhite())) {
        	return false;
        }else if(isCheckMate(piece.isWhite())) {
        	//end game
        }

        // Lưu quân bị bắt (nếu có)
        Piece captured = board.getPiece(toRow, toCol);

        // Tạo Move
        Move move = new Move(piece, toRow, toCol, captured);
        

        // Thực hiện nước đi và lưu lịch sử
        board.executeMove(move);
        

        // Đổi lượt
        whiteTurn = !whiteTurn;

        return true;
    }
    
    
    

    // Undo nước đi cuối cùng
    public void undoLastMove() {
        board.undoLastMove();
        whiteTurn = !whiteTurn;  // đảo lại lượt
    }

    public Board getBoard() {
        return board;
    }
    
    
    
    
    
    public boolean isCheck(boolean whiteKing) {
    	
    	//tim vi tri vua
    	int kingRow = -1, kingCol = -1;
    	Piece[][] b = board.getBoard();
    	for(int r = 0; r < 8; r++) {
    		for(int c = 0; c < 8; c++) {
    			Piece p = b[r][c];
    			//ktra xem vị trí đó có quân cờ ko, có thì có phải là king ko, là king thì có cùng màu với king đang tìm ko
    			if(p != null && p instanceof King && p.isWhite() == whiteKing ) {
    				kingRow = r;
    	            kingCol = c;
    	            break;		//tìm đc thì thoát, ko cần lặp thêm
    			}
    		}
    	}
    	//ktra xem vua có bị check ko
    	for (int r = 0; r < 8; r++ ) {
    		for(int c = 0; c < 8; c++) {
    			Piece p = b[r][c];
    			if(p != null && p.isWhite() != whiteKing) {
    				if(p.isValidMove(kingRow, kingCol, b)) return true;
    			}
    		}
    	}
    	return false;
    }
    
    
    
    
    
    public boolean isCheckMate(boolean whiteKing) {
        // Kiểm tra vua đối phương có đang bị check không
        if (!isCheck(!whiteKing)) return false;

        Piece[][] b = board.getBoard();
        List<Piece> pieces = board.getAllPieces(!whiteKing);

        for (Piece piece : pieces) {
            int originalRow = piece.getRow();
            int originalCol = piece.getCol();

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece target = b[r][c];

                    // Thử nước đi nếu hợp lệ
                    if (piece.isValidMove(r, c, b)) {
                        // Di chuyển tạm thời
                        b[originalRow][originalCol] = null;
                        b[r][c] = piece;
                        piece.setPosition(r, c);	//phải setPosition vì isCheck có gọi đến isValidMove(row,col,board)

                        // Nếu vua không còn bị check → không phải checkmate
                        if (!isCheck(!whiteKing)) {
                            // Khôi phục trạng thái cũ
                            piece.setPosition(originalRow, originalCol);
                            b[originalRow][originalCol] = piece;
                            b[r][c] = target;
                            return false;
                        }

                        // Khôi phục trạng thái cũ
                        piece.setPosition(originalRow, originalCol);
                        b[originalRow][originalCol] = piece;
                        b[r][c] = target;
                    }
                }
            }
        }
        // Không còn nước đi nào thoát check → checkmate
        return true;
    }

}






