package logic;

import java.util.List;

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
import network.NetworkManager;
import ui.IGameGUI;

public class GameController {
	
	private Board board;
    private boolean whiteTurn;
    private ChessAI chessAI;		
    private IGameGUI gui;
    private GameMode gameMode;
    private NetworkManager networkManager;
    
    private boolean isClientWhite = true;
    

    public GameController(IGameGUI gui, GameMode mode) {

        board = new Board(); 
        whiteTurn = true;    
        this.gui = gui;      
        this.gameMode = mode;

        if (this.gameMode == GameMode.PLAYER_VS_AI) {
            chessAI = new ChessAI(this);
            
            //check AI
            System.out.println("-> ĐÃ TẠO AI. (chessAI có null không? " + (chessAI == null) + ")");
            
            isClientWhite = true;
        } else {
        	System.out.println("-> Chế độ PvP. Bỏ qua AI.");
            chessAI = null;
        }
        System.out.println("--- KẾT THÚC KHỞI TẠO GAMECONTROLLER ---");
    }
    
    
    public boolean isWhiteTurn() {
        return whiteTurn;
    }
    
    public void setNetworkManager(NetworkManager manager) {
        this.networkManager = manager;
    }
    
    public void setClientColor(boolean isWhite) {
        this.isClientWhite = isWhite;
     // BÁO CHO GUI LẬT BÀN CỜ
        if (!isWhite && gui != null) {
        	gui.flipBoard();
        }
    }
    
    public boolean isClientTurn() {
        if (gameMode == GameMode.PLAYER_VS_AI) {
            return whiteTurn;
        }
        
        return (whiteTurn == isClientWhite); 
    }
    
    public boolean isClientWhite() {
        return this.isClientWhite;
    }

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board.getPiece(fromRow, fromCol);

   
        if (piece == null || piece.isWhite() != whiteTurn) return false;
        if (!piece.isValidMove(toRow, toCol, board.getBoard())) return false;

        Piece captured = board.getPiece(toRow, toCol);
        Move move = new Move(piece, toRow, toCol, captured);
        
        board.executeMove(move); 

        boolean leaveInCheck = isCheck(piece.isWhite());

        if (leaveInCheck) {
            board.undoLastMove(); 
            return false;
        }

        if(isPawnPromotion(piece, toRow)) {
        	Piece promotedPiece = promotePawn(piece.isWhite(), toRow, toCol);
        	board.getBoard()[toRow][toCol] = promotedPiece;
        	promotedPiece.loadImage();
        }
        
        if (gameMode == GameMode.PLAYER_VS_PLAYER && networkManager != null) {
            networkManager.sendMove(move);
        }

        whiteTurn = !whiteTurn;

        if (isCheck(whiteTurn)) { 
            System.out.println("Check!");
            if (isCheckMate(whiteTurn)) { 
            	String winner = whiteTurn ? "Đen" : "Trắng";
                String message = "Checkmate!! End game. Người chiến thắng: " + winner;
                
                if (gui != null) {
                    gui.showMessage("Chiếu hết!", message);
                }
            }
        }

        return true;
    }
    
    public void undoLastMove() {
        if(board.undoLastMove()) {
        	whiteTurn = !whiteTurn; 
        }
    }

    public Board getBoard() {
        return board;
    }
    
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
    
    
    //LOGIC PHONG CẤP

    public boolean isPawnPromotion(Piece piece, int toRow) {
    	
    	if(!(piece instanceof Pawn)) return false;
    	if(piece.isWhite() && toRow == 0) return true;
    	if(!piece.isWhite() && toRow == 7) return true;
    	
    	return false;
    }
    
    
    private Piece promotePawn(boolean isWhite, int row, int col) {
    	String choice = gui.showPromotionDialog();
    	if (choice == null || choice.isEmpty()) {
            choice = "Queen"; 
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
        if (gui == null || whiteTurn || chessAI == null) return; 

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
                            
                            String notation = (char)('a' + fromCol) + String.valueOf(8 - fromRow) + 
                                              (aiMove.getCaptured() != null ? "x" : "-") + 
                                              (char)('a' + toCol) + String.valueOf(8 - toRow);
                                              
                            gui.updateGame(notation, true); 
                        }
                    } else {
                        String msg;
                        if (isCheck(false)) { 
                            msg = "Trắng thắng (Checkmate)! Chúc mừng.";
                        } else {
                            msg = "Hòa (Stalemate)! Ván cờ kết thúc.";
                        }
                        if(gui != null) {
                        	gui.showMessage("Thông báo!", msg);
                        }
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if(gui != null) {
                    	gui.showMessage("Thông báo!", "Lỗi chưa xác định");
                    }                }
            }
        }.execute(); 
    }
    
    
    public void applyNetworkMove(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        Piece pieceToMove = board.getPiece(fromRow, fromCol);
        
        if (pieceToMove == null) {
            System.err.println("Lỗi đồng bộ mạng: Không tìm thấy quân cờ tại " + fromRow + "," + fromCol);
            return;
        }

        Piece captured = board.getPiece(toRow, toCol);
        Move localMove = new Move(pieceToMove, toRow, toCol, captured);

        board.executeMove(localMove);
        
        if(isPawnPromotion(pieceToMove, toRow)) {
            Piece promotedPiece = new logic.pieces.Queen(pieceToMove.isWhite(), toRow, toCol);
            board.getBoard()[toRow][toCol] = promotedPiece;
            promotedPiece.loadImage();
        }
        
        whiteTurn = !whiteTurn;
        
        String moveNotation = (char)('a' + fromCol) + String.valueOf(8 - fromRow) + " " + 
                              (char)('a' + toCol) + String.valueOf(8 - toRow);
        
        if (gui != null) {
             gui.updateGame(moveNotation, true);
        }
        
        if (isCheck(whiteTurn)) { 
             System.out.println("Check!");
             if (isCheckMate(whiteTurn)) {
                 String winner = whiteTurn ? "Đen" : "Trắng";
                 String message = "Checkmate!! End game. Người chiến thắng: " + winner;
                 
                 if (gui != null) {
                     gui.showMessage("Chiếu hết!", message);
                 }
             }
        }
    }
    
}