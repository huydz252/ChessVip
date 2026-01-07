package logic.ai;

import logic.GameController;
import logic.move.Move;
import logic.pieces.Piece;
import logic.pieces.Pawn;
import logic.pieces.Knight;
import logic.pieces.Bishop;
import logic.pieces.Rook;
import logic.pieces.Queen;
import logic.pieces.King;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChessAI {

    private final GameController gameController;
    private int maxDepth = 3;	

    public ChessAI(GameController gc) {
        this.gameController = gc;
    }
    
    public void setDifficulty(int depth) {
        this.maxDepth = depth;
    }

    /**
     * Hàm chính tìm kiếm nước đi tốt nhất
     */
    public Move findBestMove() {
        long startTime = System.currentTimeMillis();
        
        double bestEval = Double.POSITIVE_INFINITY; 
        Move bestMove = null;

        //lay, sap xep
        List<Move> legalMoves = generateAllLegalMoves(false); //Đen
        orderMoves(legalMoves);

        if (legalMoves.isEmpty()) {
             return null; 
        }

        for (Move move : legalMoves) {
            
            gameController.getBoard().executeMove(move);
            
            double eval = minimax(maxDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true); 

            gameController.getBoard().undoLastMove();

            // Tìm MIN (cho Đen)
            if (eval < bestEval) { 
                bestEval = eval;
                bestMove = move;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("AI Depth: " + maxDepth + " | Time: " + (endTime - startTime) + "ms | Eval: " + bestEval);
        return bestMove;
    }

    /**
     * Minimax với Alpha-Beta 
     */
    private double minimax(int depth, double alpha, double beta, boolean isMaximizingPlayer) {
        
        // điều kiện dừng
        if (depth == 0) { 
        	//ktra -> tránh đi ngu 
            return quiescenceSearch(alpha, beta, isMaximizingPlayer);
        }
        
        if (gameController.getBoard().isGameOver(gameController)) {
             if (gameController.isCheck(isMaximizingPlayer)) {
                 // Checkmate: Ưu tiên thắng sớm (cộng/trừ depth)
                 return isMaximizingPlayer 
                         ? -10000000.0 - depth 
                         :  10000000.0 + depth;
             }
             return 0; // Stalemate
        }

        List<Move> legalMoves = generateAllLegalMoves(isMaximizingPlayer);
        orderMoves(legalMoves);

        if (isMaximizingPlayer) { // Trắng (MAX)
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move move : legalMoves) {
                gameController.getBoard().executeMove(move);
                double eval = minimax(depth - 1, alpha, beta, false);
                gameController.getBoard().undoLastMove();
                
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) break; 
            }
            return maxEval;
        } else { // Đen (MIN)
            double minEval = Double.POSITIVE_INFINITY;
            for (Move move : legalMoves) {
                gameController.getBoard().executeMove(move);
                double eval = minimax(depth - 1, alpha, beta, true);
                gameController.getBoard().undoLastMove();
                
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    /**
     * QUIESCENCE SEARCH (Tìm kiếm yên lặng)
     * Chỉ tìm kiếm tiếp các nước ĂN QUÂN để tránh đánh giá sai tình huống nguy hiểm.
     */
    private double quiescenceSearch(double alpha, double beta, boolean isMaximizingPlayer) {
        // 1. Đánh giá tĩnh (Stand-pat)
        // Giả sử nếu ta không làm gì cả (không ăn quân nữa), thì điểm số là bao nhiêu?
        double standPat = Evaluation.evaluateBoard(gameController.getBoard().getBoard());

        // 2. Cắt tỉa (Pruning) dựa trên Stand-pat
        if (isMaximizingPlayer) {
            if (standPat >= beta) return beta;
            if (standPat > alpha) alpha = standPat;
        } else {
            if (standPat <= alpha) return alpha;
            if (standPat < beta) beta = standPat;
        }

        // 3. Tạo nước đi, NHƯNG CHỈ LẤY CÁC NƯỚC ĂN QUÂN (Captures only)
        List<Move> captureMoves = generateCaptureMoves(isMaximizingPlayer);
        orderMoves(captureMoves); // Luôn ưu tiên ăn quân to trước

        for (Move move : captureMoves) {
            gameController.getBoard().executeMove(move);
            // Đệ quy Quiescence Search
            double eval = quiescenceSearch(alpha, beta, !isMaximizingPlayer);
            gameController.getBoard().undoLastMove();

            if (isMaximizingPlayer) {
                if (eval >= beta) return beta;
                if (eval > alpha) alpha = eval;
            } else {
                if (eval <= alpha) return alpha;
                if (eval < beta) beta = eval;
            }
        }
        
        // Nếu là MAX thì trả về alpha, MIN trả về beta (hoặc standPat đã được update)
        return isMaximizingPlayer ? alpha : beta;
    }

    /**
     * Sắp xếp nước đi (Move Ordering) - MVV-LVA (Most Valuable Victim - Least Valuable Aggressor)
     * Ưu tiên: Ăn quân giá trị cao bằng quân giá trị thấp.
     */
    private void orderMoves(List<Move> moves) {
        Collections.sort(moves, new Comparator<Move>() {
            @Override
            public int compare(Move m1, Move m2) {
                // Điểm càng cao càng ưu tiên
                return Double.compare(scoreMove(m2), scoreMove(m1));
            }
        });
    }

    private double scoreMove(Move move) {
        double score = 0;
        
        // Ưu tiên nước ăn quân
        if (move.getCaptured() != null) {
            // Lấy giá trị quân bị ăn - giá trị quân đi ăn / 10
            // Ví dụ: Tốt ăn Hậu (900 - 10 = 890) > Hậu ăn Tốt (100 - 90 = 10)
            score = 10 * getPieceValue(move.getCaptured()) - getPieceValue(move.getPiece());
        }
        
        // (Có thể thêm ưu tiên phong cấp, chiếu tướng...)
        
        return score;
    }
    
    private double getPieceValue(Piece p) {
        if (p instanceof Pawn) return 100;
        if (p instanceof Knight) return 320;
        if (p instanceof Bishop) return 330;
        if (p instanceof Rook) return 500;
        if (p instanceof Queen) return 900;
        if (p instanceof King) return 20000;
        return 0;
    }

    /**
     * Helper: Chỉ sinh các nước ĂN QUÂN (cho Quiescence Search)
     */
    private List<Move> generateCaptureMoves(boolean isWhite) {
        List<Move> allMoves = generateAllLegalMoves(isWhite);
        List<Move> captures = new ArrayList<>();
        for (Move m : allMoves) {
            if (m.getCaptured() != null) {
                captures.add(m);
            }
        }
        return captures;
    }

    /**
     * Hàm sinh nước đi 
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
                                
                                boolean isLegal = false; 

                                try {
                                    // Giả lập nước đi
                                    board[r1][c1] = null;
                                    board[r2][c2] = piece;
                                    piece.setPosition(r2, c2); 
                                    
                                    // Kiểm tra luật (Vua an toàn?)
                                    if (!gameController.isCheck(isWhite)) {
                                        isLegal = true; // Đánh dấu là hợp lệ
                                    }
                                } finally {
                                    piece.setPosition(oldR, oldC); 
                                    board[r1][c1] = piece;
                                    board[r2][c2] = captured;
                                }

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