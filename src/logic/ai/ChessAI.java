package logic.ai;

import logic.GameController;
import logic.move.Move;
import logic.pieces.Piece;

import java.util.List;
import java.util.ArrayList;

public class ChessAI {

    private final GameController gameController;
    private final int MAX_DEPTH = 3; 

    public ChessAI(GameController gc) {
        this.gameController = gc;
    }

    /**
     * Hàm chính tìm kiếm và trả về nước đi tốt nhất cho bên AI (Đen)
     */
    public Move findBestMove() {
        long startTime = System.currentTimeMillis();
        
        boolean isAIMoving = false; 
        
        Move bestMove = null;
        double maxEval = Double.NEGATIVE_INFINITY;

        List<Move> legalMoves = generateAllLegalMoves(isAIMoving); 

        if (legalMoves.isEmpty()) {
             return null; 
        }

        for (Move move : legalMoves) {
            
            gameController.getBoard().executeMove(move);
            
            // Gọi Minimax (depth-1, isMaximizingPlayer=false: lượt của Trắng/MIN)
            double evaluation = minimax(MAX_DEPTH - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false); 

            gameController.getBoard().undoLastMove();

            if (evaluation > maxEval) {
                maxEval = evaluation;
                bestMove = move;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("AI Search Time: " + (endTime - startTime) + "ms");
        return bestMove;
    }

    /**
     * Triển khai Minimax với Alpha-Beta Pruning
     */
    private double minimax(int depth, double alpha, double beta, boolean isMaximizingPlayer) {
        //Điều kiện Dừng
        if (depth == 0 || gameController.getBoard().isGameOver()) { 
            return Evaluation.evaluateBoard(gameController.getBoard().getBoard());
        }
        
        //Tạo nước đi hợp lệ 
        List<Move> legalMoves = generateAllLegalMoves(!isMaximizingPlayer);

        // Kiểm tra Stalemate/Checkmate
        if (legalMoves.isEmpty()) {
             if (gameController.isCheck(isMaximizingPlayer)) {
                 // Checkmate: Trả về giá trị cực đại/cực tiểu tùy theo phe
                 return isMaximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
             } else {
                 // Stalemate: Hòa
                 return 0;
             }
        }
        
        // Logic MAX (AI - Đen) 
        if (isMaximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move move : legalMoves) {
                gameController.getBoard().executeMove(move);
                double eval = minimax(depth - 1, alpha, beta, false);
                gameController.getBoard().undoLastMove();
                
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) { // Cắt tỉa Beta
                    break; 
                }
            }
            return maxEval;
        } 
        
        // Logic MIN (Người chơi - Trắng) 
        else {
            double minEval = Double.POSITIVE_INFINITY;
            for (Move move : legalMoves) {
                gameController.getBoard().executeMove(move);
                double eval = minimax(depth - 1, alpha, beta, true);
                gameController.getBoard().undoLastMove();
                
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) { // Cắt tỉa Alpha
                    break;
                }
            }
            return minEval;
        }
    }

    /**
     * Hàm helper: Lấy tất cả nước đi hợp lệ cho phe hiện tại
     * (Giả lập và kiểm tra luật Tự chiếu)
     */
    private List<Move> generateAllLegalMoves(boolean isWhite) {
        List<Move> allMoves = new ArrayList<>();
        Piece[][] board = gameController.getBoard().getBoard();

        for (int r1 = 0; r1 < 8; r1++) {
            for (int c1 = 0; c1 < 8; c1++) {
                Piece piece = board[r1][c1];
                
                if (piece != null && piece.isWhite() == isWhite) { 
                    
                    for (int r2 = 0; r2 < 8; r2++) {
                        for (int c2 = 0; c2 < 8; c2++) {
                            
                            if (piece.isValidMove(r2, c2, board)) {
                                
                                Piece captured = board[r2][c2];
                                
                                int oldR = piece.getRow(); 
                                int oldC = piece.getCol(); 
                                
                                board[r1][c1] = null;
                                board[r2][c2] = piece;
                                piece.setPosition(r2, c2); 
                                
                                boolean isLegal = !gameController.isCheck(isWhite);

                                piece.setPosition(oldR, oldC); // (Đặt lại vị trí piece về r1, c1)
                                board[r1][c1] = piece;
                                board[r2][c2] = captured;
                                
                                if (isLegal) {
                                    allMoves.add(new Move(piece, r2, c2, captured)); 
                                }
                            }
                        }
                    }
                }
            }
        }
        return allMoves;
    }
}