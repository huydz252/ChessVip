package logic.ai;

import logic.pieces.Piece;
import logic.pieces.Pawn;
import logic.pieces.Knight;
import logic.pieces.Bishop;
import logic.pieces.Rook;
import logic.pieces.Queen;
import logic.pieces.King;

public class Evaluation {

    private static final double PAWN_VALUE = 100;
    private static final double KNIGHT_VALUE = 320;
    private static final double BISHOP_VALUE = 330;
    private static final double ROOK_VALUE = 500;
    private static final double QUEEN_VALUE = 900;
    private static final double KING_VALUE = 20000;

    // Bảng điểm cho Tốt (Khuyến khích tiến lên)
    private static final double[] PAWN_TABLE = {
        0,  0,  0,  0,  0,  0,  0,  0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
        5,  5, 10, 25, 25, 10,  5,  5,
        0,  0,  0, 20, 20,  0,  0,  0,
        5, -5,-10,  0,  0,-10, -5,  5,
        5, 10, 10,-20,-20, 10, 10,  5,
        0,  0,  0,  0,  0,  0,  0,  0
    };

    // Bảng điểm cho Mã (Thích trung tâm, ghét góc/biên)
    private static final double[] KNIGHT_TABLE = {
        -50,-40,-30,-30,-30,-30,-40,-50,
        -40,-20,  0,  0,  0,  0,-20,-40,
        -30,  0, 10, 15, 15, 10,  0,-30,
        -30,  5, 15, 20, 20, 15,  5,-30,
        -30,  0, 15, 20, 20, 15,  0,-30,
        -30,  5, 10, 15, 15, 10,  5,-30,
        -40,-20,  0,  5,  5,  0,-20,-40,
        -50,-40,-30,-30,-30,-30,-40,-50
    };

    // Bảng điểm cho Tượng (Thích đường chéo dài, tránh góc tù)
    private static final double[] BISHOP_TABLE = {
        -20,-10,-10,-10,-10,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5, 10, 10,  5,  0,-10,
        -10,  5,  5, 10, 10,  5,  5,-10,
        -10,  0, 10, 10, 10, 10,  0,-10,
        -10, 10, 10, 10, 10, 10, 10,-10,
        -10,  5,  0,  0,  0,  0,  5,-10,
        -20,-10,-10,-10,-10,-10,-10,-20
    };
    

    // Bảng điểm cho Vua (Giữa trận: Nấp kỹ ở góc; Tàn cuộc: Ra giữa bàn)
    // Đây là bảng "Giữa trận" (Midgame) - Ưu tiên an toàn
    private static final double[] KING_MID_TABLE = {
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -20,-30,-30,-40,-40,-30,-30,-20,
        -10,-20,-20,-20,-20,-20,-20,-10,
         20, 20,  0,  0,  0,  0, 20, 20,
         20, 30, 10,  0,  0, 10, 30, 20
    };

    public static double evaluateBoard(Piece[][] board) {
        double totalEvaluation = 0.0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null) {
                    double value = getPieceValue(p) + getPositionBonus(p, r, c);
                    
                    if (p.isWhite()) {
                        totalEvaluation += value;
                    } else {
                        totalEvaluation -= value;
                    }
                }
            }
        }
        return totalEvaluation;
    }

    private static double getPieceValue(Piece piece) {
        if (piece instanceof Pawn) return PAWN_VALUE;
        if (piece instanceof Knight) return KNIGHT_VALUE;
        if (piece instanceof Bishop) return BISHOP_VALUE;
        if (piece instanceof Rook) return ROOK_VALUE;
        if (piece instanceof Queen) return QUEEN_VALUE;
        if (piece instanceof King) return KING_VALUE;
        return 0;
    }

    private static double getPositionBonus(Piece piece, int r, int c) {
    	
        // Lưu ý: Bảng điểm được thiết kế cho góc nhìn của quân TRẮNG (hàng 0-7).
        // Nếu là quân ĐEN, chúng ta cần "lật ngược" bàn cờ để tính điểm tương ứng.
        
        int row = piece.isWhite() ? r : 7 - r; // Lật ngược hàng nếu là Đen
        int col = c;
        
        int index = row * 8 + col; // Chuyển tọa độ 2D thành index mảng 1D

        if (piece instanceof Pawn) return PAWN_TABLE[index];
        if (piece instanceof Knight) return KNIGHT_TABLE[index];
        if (piece instanceof Bishop) return BISHOP_TABLE[index];
        if (piece instanceof King) return KING_MID_TABLE[index];
        
        return 0; 
    }
}