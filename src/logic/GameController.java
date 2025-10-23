package logic;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker; 

import logic.ai.ChessAI;
import logic.board.Board;
import logic.pieces.Bishop;
import logic.pieces.King;
import logic.pieces.Knight;
import logic.pieces.Pawn;
import logic.pieces.Piece;
import logic.pieces.Queen;
import logic.pieces.Rook;
import logic.move.Move;
import ui.ChessGUI;

public class GameController {
	private Board board;
    private boolean whiteTurn;
    private ChessAI chessAI;
    private ChessGUI chessGUI;

    // SỬA: Constructor chính, cần tham chiếu GUI
    public GameController(ChessGUI gui) { 
        this.chessGUI = gui;
        board = new Board();
        whiteTurn = true;
        chessAI = new ChessAI(this);
    }
    
    public GameController() {
        this(null); // Gọi constructor chính với tham chiếu GUI là null
    }
    
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board.getPiece(fromRow, fromCol);

        // --- 1. Kiểm tra cơ bản ---
        if (piece == null || piece.isWhite() != whiteTurn) return false;
        if (!piece.isValidMove(toRow, toCol, board.getBoard())) return false;

        // --- 2. Tạo nước đi và Thực hiện ---
        Piece captured = board.getPiece(toRow, toCol);
        Move move = new Move(piece, toRow, toCol, captured);
        
        board.executeMove(move); // Thực hiện nước đi trên bàn cờ

        // --- 3. Kiểm tra luật Tự chiếu (Check) ---
        boolean leaveInCheck = isCheck(piece.isWhite());

        if (leaveInCheck) {
            board.undoLastMove(); // Hoàn tác nếu tự chiếu
            return false;
        }

        // --- 4. Xử lý Phong cấp (Promotion) ---
        if(isPawnPromotion(piece, toRow)) {
        	Piece promotedPiece = promotePawn(piece.isWhite(), toRow, toCol);
        	board.getBoard()[toRow][toCol] = promotedPiece;
        	promotedPiece.loadImage();
        }

        // --- 5. Đổi lượt và Kiểm tra đối thủ ---
        whiteTurn = !whiteTurn;

        if (isCheck(whiteTurn)) { 
            System.out.println("Check!");
            if (isCheckMate(whiteTurn)) { 
            	String winner = whiteTurn ? "Đen" : "Trắng";
                String message = "Checkmate!! End game. Người chiến thắng: " + winner;
                
                if (chessGUI != null) {
                    JOptionPane.showMessageDialog(
                        chessGUI, message, "Chiếu Hết!",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
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
    
    // Hàm kiểm tra chiếu (giữ nguyên)
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
        if (kingRow == -1) return false;

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
    
    // Hàm kiểm tra chiếu hết (giữ nguyên)
    public boolean isCheckMate(boolean whiteKing) {
        if (!isCheck(whiteKing)) return false; 
        Piece[][] b = board.getBoard();
        List<Piece> pieces = board.getAllPieces(whiteKing); 

        for (Piece piece : pieces) {
            int originalRow = piece.getRow();
            int originalCol = piece.getCol();

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece target = b[r][c];
                    if (piece.isValidMove(r, c, b)) {
                        
                        b[originalRow][originalCol] = null;
                        b[r][c] = piece;
                        piece.setPosition(r, c);

                        if (!isCheck(whiteKing)) { 
                            
                            piece.setPosition(originalRow, originalCol);
                            b[originalRow][originalCol] = piece;
                            b[r][c] = target;
                            
                            return false; 
                        }

                        piece.setPosition(originalRow, originalCol);
                        b[originalRow][originalCol] = piece;
                        b[r][c] = target;
                    }
                }
            }
        }
        return true;
    }
    
    
    //------- LOGIC PHONG CẤP -------
    
    // 1. hàm kiểm tra phong cấp
    public boolean isPawnPromotion(Piece piece, int toRow) {
    	
    	if(!(piece instanceof Pawn)) return false;
    	
    	//tốt trắng
    	if(piece.isWhite() && toRow == 0) return true;
    	//tốt đen
    	if(!piece.isWhite() && toRow == 7) return true;
    	
    	return false;
    }
    
    
    // 2. hàm yêu cầu người chơi lựa chọn quân thay thế cho tốt
    private Piece promotePawn(boolean isWhite, int row, int col) {
    	String[] options = {"Queen", "Rook", "Knight", "Bishop"};
    	String choice = (String) JOptionPane.showInputDialog(
                chessGUI, // SỬA: Dùng chessGUI làm component cha
                "Chọn quân để phong cấp:",
                "Phong Cấp Tốt",
                JOptionPane.QUESTION_MESSAGE,
                null, 
                options,
                options[0]);
    	if (choice == null || choice.isEmpty()) {
            choice = "Queen"; // Mặc định là Hậu
        }
        
        Piece newPiece;
    	switch (choice) {
            case "Rook":
                newPiece = new Rook(isWhite, row, col);
                break;
            case "Bishop":
                newPiece = new Bishop(isWhite, row, col);
                break;
            case "Knight":
                newPiece = new Knight(isWhite, row, col);
                break;
            case "Queen":
            default:
                newPiece = new Queen(isWhite, row, col);
                break;
    	}	
        return newPiece;
    }
    
    
    // AI
    
    public void handleAITurn() {
        // KIỂM TRA: Chỉ xử lý nếu GUI đã được gán và đến lượt Đen
        if (chessGUI == null || whiteTurn) return; 

        new SwingWorker<Move, Void>() {
            
            @Override
            protected Move doInBackground() throws Exception {
                return chessAI.findBestMove();
            }

            @Override
            protected void done() {
                try {
                    Move aiMove = get();
                    
                    if (aiMove != null) {
                        
                        int fromRow = aiMove.getFromRow();
                        int fromCol = aiMove.getFromCol();
                        int toRow = aiMove.getToRow();
                        int toCol = aiMove.getToCol();

                        if (move(fromRow, fromCol, toRow, toCol)) {
                            
                            // Tạo ký hiệu (Đã được chuẩn hóa)
                            String notation = (char)('a' + fromCol) + String.valueOf(8 - fromRow) + 
                                              (aiMove.getCaptured() != null ? "x" : "-") + 
                                              (char)('a' + toCol) + String.valueOf(8 - toRow);
                                              
                            // Cập nhật GUI
                            chessGUI.updateGame(notation, true); 
                        }
                    } else {
                        // Xử lý Checkmate/Stalemate
                        String msg;
                        if (isCheck(false)) { 
                            msg = "Trắng thắng (Checkmate)! Chúc mừng.";
                        } else {
                            msg = "Hòa (Stalemate)! Ván cờ kết thúc.";
                        }
                        JOptionPane.showMessageDialog(chessGUI, msg);
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(chessGUI, "Lỗi AI: " + ex.getMessage());
                }
            }
        }.execute(); 
    }
}