package logic;

import java.util.List;

import javax.swing.JOptionPane;

import logic.board.Board;
import logic.pieces.Bishop;
import logic.pieces.King;
import logic.pieces.Knight;
import logic.pieces.Pawn;
import logic.pieces.Piece;
import logic.pieces.Queen;
import logic.pieces.Rook;
import logic.move.Move;

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

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board.getPiece(fromRow, fromCol);

        // Kiểm tra ô có quân và lượt đi
        if (piece == null || piece.isWhite() != whiteTurn) return false;

        // Kiểm tra nước đi hợp lệ theo quân cờ (cần đảm bảo logic isValidMove đã tính đến việc ăn quân)
        if (!piece.isValidMove(toRow, toCol, board.getBoard())) return false;

        // --- Giả lập nước đi để kiểm tra xem vua CỦA NGƯỜI CHƠI này có bị chiếu không ---
        Piece[][] b = board.getBoard();
        Piece captured = b[toRow][toCol];

        // Lưu trạng thái cũ
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();

        // Thực hiện tạm thời
        b[oldRow][oldCol] = null;
        b[toRow][toCol] = piece;
        piece.setPosition(toRow, toCol);

        // Nếu sau khi di chuyển mà vua của người chơi này bị chiếu => nước đi sai
        boolean leaveInCheck = isCheck(piece.isWhite());

        // Khôi phục trạng thái
        piece.setPosition(oldRow, oldCol);
        b[oldRow][oldCol] = piece;
        b[toRow][toCol] = captured;

        if (leaveInCheck) return false;

        //Nếu nước đi hợp lệ và không khiến vua bị chiếu
        Move move = new Move(piece, toRow, toCol, captured);
        board.executeMove(move);
        
        //kiểm tra phong cấp tốt:
        if(isPawnPromotion(piece, toRow)) {
        	Piece promotedPiece = promotePawn(piece.isWhite(), toRow, toCol);
        	
        	//cập nhật quân cờ mới
        	board.getBoard()[toRow][toCol] = promotedPiece;
        	
        	promotedPiece.loadImage();
        }

        whiteTurn = !whiteTurn; // Đổi lượt chơi. whiteTurn BÂY GIỜ là màu của người chơi tiếp theo.

        // Sau khi đổi lượt, kiểm tra xem người chơi BỊ CHIẾU (whiteTurn) có bị check hay checkmate không
        if (isCheck(whiteTurn)) { // SỬA LỖI 1: whiteTurn là màu của người đang bị chiếu
            System.out.println("Check!");
            if (isCheckMate(whiteTurn)) { // SỬA LỖI 1: whiteTurn là màu của người đang bị chiếu
            	String winner = whiteTurn ? "Đen" : "Trắng";
                String message = "Checkmate!! End game. Người chiến thắng: " + winner;
                
                JOptionPane.showMessageDialog(
                    null, // Component cha (dùng null nếu không có)
                    message,
                    "Chiếu Hết!",
                    JOptionPane.INFORMATION_MESSAGE // Loại icon thông báo
                );
            }
        }

        return true;
    }
    
    // Undo nước đi cuối cùng
    public void undoLastMove() {
        if(board.undoLastMove()) {
        	whiteTurn = !whiteTurn; // đảo lại lượt
        }
    }

    public Board getBoard() {
        return board;
    }
    
    // Hàm kiểm tra chiếu (không thay đổi)
    public boolean isCheck(boolean whiteKing) {
    	
    	//tìm vị trí vua
    	int kingRow = -1, kingCol = -1;
    	Piece[][] b = board.getBoard();
    	for(int r = 0; r < 8; r++) {
    		for(int c = 0; c < 8; c++) {
    			Piece p = b[r][c];
    			if(p != null && p instanceof King && p.isWhite() == whiteKing ) {
    				kingRow = r;
    	            kingCol = c;
    	            break;
    			}
    		}
    	}
        if (kingRow == -1) return false; // Không tìm thấy Vua (trường hợp hiếm)

    	//ktra xem vua có bị check ko
    	for (int r = 0; r < 8; r++ ) {
    		for(int c = 0; c < 8; c++) {
    			Piece p = b[r][c];
    			// Kiểm tra tất cả các quân đối phương
    			if(p != null && p.isWhite() != whiteKing) {
    				// Nếu quân đối phương có thể di chuyển đến ô của Vua
    				if(p.isValidMove(kingRow, kingCol, b)) return true;
    			}
    		}
    	}
    	return false;
    }
    
    // Hàm kiểm tra chiếu hết (Đã sửa lỗi logic)
    public boolean isCheckMate(boolean whiteKing) {
        // 1. Kiểm tra vua có đang bị check không
        if (!isCheck(whiteKing)) return false; 
        Piece[][] b = board.getBoard();
        // 2. Lấy tất cả quân cờ CÙNG MÀU với vua đang bị kiểm tra
        List<Piece> pieces = board.getAllPieces(whiteKing); 

        for (Piece piece : pieces) {
            int originalRow = piece.getRow();
            int originalCol = piece.getCol();

            // Thử tất cả các ô trên bàn cờ làm ô đích
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece target = b[r][c];

                    // Thử nước đi nếu hợp lệ theo luật di chuyển của quân cờ
                    if (piece.isValidMove(r, c, b)) {
                        
                        // Di chuyển tạm thời (Giả lập)
                        b[originalRow][originalCol] = null;
                        b[r][c] = piece;
                        piece.setPosition(r, c);

                        // Nếu vua KHÔNG CÒN bị check sau nước đi này → KHÔNG phải checkmate
                        if (!isCheck(whiteKing)) { 
                            
                            // Khôi phục trạng thái cũ
                            piece.setPosition(originalRow, originalCol);
                            b[originalRow][originalCol] = piece;
                            b[r][c] = target;
                            
                            return false; // Đã tìm thấy nước đi hợp lệ thoát check
                        }

                        // Khôi phục trạng thái cũ (Quan trọng)
                        piece.setPosition(originalRow, originalCol);
                        b[originalRow][originalCol] = piece;
                        b[r][c] = target;
                    }
                }
            }
        }
        // Sau khi thử tất cả nước đi và không có nước nào thoát check → checkmate
        return true;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    //------- KIỂM TRA PHONG CẤP 
    
  
    // 1. hàm kiểm tra phong cấp
    public boolean isPawnPromotion(Piece piece, int toRow) {
    	
    	if(!(piece instanceof Pawn)) return false;
    	
    	//tốt trắng
    	if(piece.isWhite() && toRow == 0) return true;
    	//tốt đen
    	if(!piece.isWhite() && toRow == 7) return true;
    	
    	return false; //k phải nước đi phong cấp
    }
    
    
    // 2. hàm yêu cầu người chơi lựa chọn quân thay thế cho tốt
    private Piece promotePawn(boolean isWhite, int row, int col) {
    	String[] options = {"Queen", "Rook", "Knight", "Bishop"};
    	String choice = (String) JOptionPane.showInputDialog(null, 
                "Chọn quân để phong cấp:",
                "Phong Cấp Tốt",
                JOptionPane.QUESTION_MESSAGE,
                null, 
                options,
                options[0]);
    	if (choice == null || choice.isEmpty()) {
            choice = "Queen"; // Mặc định là Hậu
        }
    	switch (choice) {
        case "Rook":
            return new Rook(isWhite, row, col);
        case "Bishop":
            return new Bishop(isWhite, row, col);
        case "Knight":
            return new Knight(isWhite, row, col);
        case "Queen":
        default:
            return new Queen(isWhite, row, col);
    }
    	
    	
    		
    }
}








