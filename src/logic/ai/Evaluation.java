package logic.ai;

import logic.pieces.Bishop;
import logic.pieces.Knight;
import logic.pieces.Pawn;
import logic.pieces.Piece;
import logic.pieces.Queen;
import logic.pieces.Rook;

public class Evaluation {

    // Gán giá trị cho từng loại quân (điểm cơ bản)
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 300;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;

    /**
     * Hàm đánh giá tĩnh: Gán điểm cho trạng thái bàn cờ hiện tại.
     * @param board Mảng 2 chiều chứa các quân cờ.
     * @return Giá trị đánh giá: Dương nếu có lợi cho Đen (AI), Âm nếu có lợi cho Trắng.
     */
    public static double evaluateBoard(Piece[][] board) {
        double evaluation = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board[r][c];
                if (piece == null) continue;

                int value = 0;
                // Sử dụng 'instanceof' để xác định loại quân
                if (piece instanceof Pawn) value = PAWN_VALUE;
                else if (piece instanceof Knight) value = KNIGHT_VALUE;
                else if (piece instanceof Bishop) value = BISHOP_VALUE;
                else if (piece instanceof Rook) value = ROOK_VALUE;
                else if (piece instanceof Queen) value = QUEEN_VALUE;
                
                // AI (Đen) là người Tối đa hóa (MAX), người chơi (Trắng) là Tối thiểu hóa (MIN)
                if (!piece.isWhite()) { // Quân Đen (AI) -> CỘNG điểm
                    evaluation += value;
                } else { // Quân Trắng (Người chơi) -> TRỪ điểm
                    evaluation -= value;
                }
            }
        }
        return evaluation;
    }
}