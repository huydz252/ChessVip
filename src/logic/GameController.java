package logic;

import java.util.List;
import javax.swing.SwingWorker;

import logic.ai.ChessAI;
import logic.board.Board;
import logic.move.Move;
import logic.pieces.Bishop;
import logic.pieces.King;
import logic.pieces.Knight;
import logic.pieces.Pawn;
import logic.pieces.Piece;
import logic.pieces.Queen;
import logic.pieces.Rook;
import network.NetworkManager;
import ui.IGameGUI;

public class GameController {

    private Board board;
    private boolean whiteTurn;
    private ChessAI chessAI;
    private IGameGUI gui;
    private GameMode gameMode;

    private NetworkManager networkManager;
    private boolean iWantToReplay = false;
    private boolean opponentWantsToReplay = false;

    private boolean isGameOver;
    private boolean isClientWhite = true;

    public GameController(IGameGUI gui, GameMode mode) {
        System.out.println("--- BẮT ĐẦU KHỞI TẠO GAMECONTROLLER ---");
        
        board = new Board();
        whiteTurn = true;
        this.gui = gui;
        this.gameMode = mode;

        // Khởi tạo AI nếu đúng chế độ
        if (this.gameMode == GameMode.PLAYER_VS_AI) {
            chessAI = new ChessAI(this);
            isClientWhite = true;
            System.out.println("-> ĐÃ TẠO AI.");
        } else {
            System.out.println("-> Chế độ PvP. Bỏ qua AI.");
            chessAI = null;
        }
        System.out.println("--- KẾT THÚC KHỞI TẠO GAMECONTROLLER ---");
    }

    // --- CÁC HÀM GETTER/SETTER ---

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

    public boolean getIsGameOver() {
        return this.isGameOver;
    }

    public void setIsGameOver(boolean over) {
        this.isGameOver = over;
    }

    public void setAIDifficulty(int depth) {
        if (chessAI != null) {
            chessAI.setDifficulty(depth);
            System.out.println("Đã thiết lập độ khó AI: Depth " + depth);
        }
    }

    public Board getBoard() {
        return board;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    // --- LOGIC CHÍNH (MOVE) ---

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board.getPiece(fromRow, fromCol);

        // Kiểm tra cơ bản
        if (piece == null || piece.isWhite() != whiteTurn) {
            return false;
        }
        if (!piece.isValidMove(toRow, toCol, board.getBoard())) {
            return false;
        }

        // Thực hiện nước đi
        Piece captured = board.getPiece(toRow, toCol);
        Move move = new Move(piece, toRow, toCol, captured);
        board.executeMove(move);

        // Kiểm tra Tự chiếu (Luật bắt buộc)
        boolean leaveInCheck = isCheck(piece.isWhite());
        if (leaveInCheck) {
            board.undoLastMove(); // Hoàn tác nếu tự chiếu
            return false;
        }

        // Xử lý Phong cấp
        if (isPawnPromotion(piece, toRow)) {
            Piece promotedPiece = promotePawn(piece.isWhite(), toRow, toCol);
            board.getBoard()[toRow][toCol] = promotedPiece;
            promotedPiece.loadImage();
        }

        // Gửi mạng (Nếu là PvP)
        if (gameMode == GameMode.PLAYER_VS_PLAYER && networkManager != null) {
            networkManager.sendMove(move);
        }

        // Đổi lượt
        whiteTurn = !whiteTurn;

        // Kiểm tra kết thúc game (Checkmate/Stalemate) bằng hàm chung
        checkGameStateAfterTurn();

        return true;
    }

    

    // --- HELPER: KIỂM TRA KẾT THÚC GAME ---

    /**
     * Hàm chung để kiểm tra trạng thái game sau mỗi lượt đi.
     * Được gọi bởi cả move() và applyNetworkMove().
     */
    private void checkGameStateAfterTurn() {
        // Kiểm tra xem phe hiện tại (whiteTurn) có còn nước đi không
        boolean hasMoves = hasAnyLegalMove(whiteTurn);

        if (!hasMoves) {
            isGameOver = true; // Khóa bàn cờ

            if (isCheck(whiteTurn)) {
                // Hết nước + Bị chiếu = CHECKMATE
                String winner = whiteTurn ? "Đen" : "Trắng"; // whiteTurn là người bị chiếu, nên người kia thắng
                String message = "Checkmate!! Người chiến thắng: " + winner;
                if (gui != null) {
                    gui.showGameOverDialog("Kết thúc ván đấu", message);
                }
            } else {
                // Hết nước + Không bị chiếu = STALEMATE
                if (gui != null) {
                    gui.showGameOverDialog("Kết thúc ván đấu", "Game đấu hòa (Stalemate)!");
                }
            }
        }
    }

    // --- AI LOGIC ---

    public void handleAITurn() {
        if (gui == null || whiteTurn || chessAI == null) {
            return;
        }

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

                        // Thực hiện nước đi của AI
                        if (move(fromRow, fromCol, toRow, toCol)) {
                            String notation = (char) ('a' + fromCol) + String.valueOf(8 - fromRow)
                                    + (aiMove.getCaptured() != null ? "x" : "-") 
                                    + (char) ('a' + toCol) + String.valueOf(8 - toRow);

                            gui.updateGame(notation, true);
                            
                            // Lưu ý: Hàm move() đã tự gọi checkGameStateAfterTurn() rồi
                            // nên không cần kiểm tra lại ở đây nữa.
                        }
                    } 
                    // Nếu aiMove == null -> AI đầu hàng hoặc bị kẹt (thường do checkmate/stalemate đã được phát hiện trước đó)
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (gui != null) {
                        gui.showMessage("Thông báo!", "Lỗi AI: " + ex.getMessage());
                    }
                }
            }
        }.execute();
    }

    // --- CÁC HÀM LOGIC KHÁC ---

    public void undoLastMove() {
        if (board.undoLastMove()) {
            System.out.println("Undo thành công");
            // Nếu undo, cần mở lại game và đảo lượt
            this.isGameOver = false;
            this.whiteTurn = !this.whiteTurn;
        }
    }

    public boolean isCheck(boolean whiteKing) {
        int kingRow = -1, kingCol = -1;
        Piece[][] b = board.getBoard();
        
        // Tìm Vua
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = b[r][c];
                if (p != null && p instanceof King && p.isWhite() == whiteKing) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }
        if (kingRow == -1) return false;

        // Kiểm tra bị tấn công
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = b[r][c];
                if (p != null && p.isWhite() != whiteKing) {
                    if (p.isValidMove(kingRow, kingCol, b)) {
                        // Debug line (có thể xóa sau này)
                        // System.out.println("CẢNH BÁO: Vua đang bị chiếu bởi " + p.getClass().getSimpleName());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Hàm này giờ chỉ dùng để AI tham khảo, logic chính đã chuyển sang hasAnyLegalMove
    public boolean isCheckMate(boolean whiteKing) {
        if (!isCheck(whiteKing)) return false;
        return !hasAnyLegalMove(whiteKing);
    }

    public boolean hasAnyLegalMove(boolean whiteKing) {
        Piece[][] b = board.getBoard();
        List<Piece> pieces = board.getAllPieces(whiteKing);

        for (Piece piece : pieces) {
            int originalRow = piece.getRow();
            int originalCol = piece.getCol();

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece target = b[r][c];
                    if (piece.isValidMove(r, c, b)) {
                        // Try
                        b[originalRow][originalCol] = null;
                        b[r][c] = piece;
                        piece.setPosition(r, c);

                        boolean stillCheck = isCheck(whiteKing);

                        // Finally (Undo)
                        piece.setPosition(originalRow, originalCol);
                        b[originalRow][originalCol] = piece;
                        b[r][c] = target;

                        if (!stillCheck) return true; // Tìm thấy ít nhất 1 nước đi
                    }
                }
            }
        }
        return false;
    }

    public boolean isPawnPromotion(Piece piece, int toRow) {
        if (!(piece instanceof Pawn)) return false;
        if (piece.isWhite() && toRow == 0) return true;
        if (!piece.isWhite() && toRow == 7) return true;
        return false;
    }

    private Piece promotePawn(boolean isWhite, int row, int col) {
        // AI (Đen) tự động chọn Hậu
        if (!isWhite) {
            return new Queen(false, row, col);
        }
        
        String choice = gui.showPromotionDialog();
        if (choice == null || choice.isEmpty()) choice = "Queen";

        Piece newPiece;
        switch (choice) {
            case "Rook": newPiece = new Rook(isWhite, row, col); break;
            case "Bishop": newPiece = new Bishop(isWhite, row, col); break;
            case "Knight": newPiece = new Knight(isWhite, row, col); break;
            case "Queen": default: newPiece = new Queen(isWhite, row, col); break;
        }
        return newPiece;
    }

    // --- NETWORKING---
    /**
     * Xử lý nước đi nhận được từ mạng (PvP)
     */
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

        // Xử lý phong cấp (Mặc định Hậu cho đối thủ qua mạng)
        if (isPawnPromotion(pieceToMove, toRow)) {
            Piece promotedPiece = new Queen(pieceToMove.isWhite(), toRow, toCol);
            board.getBoard()[toRow][toCol] = promotedPiece;
            promotedPiece.loadImage();
        }

        whiteTurn = !whiteTurn;

        // Cập nhật GUI
        String moveNotation = (char) ('a' + fromCol) + String.valueOf(8 - fromRow) + " " 
                            + (char) ('a' + toCol) + String.valueOf(8 - toRow);

        if (gui != null) {
            gui.updateGame(moveNotation, true);
        }

        // Kiểm tra kết thúc game bằng hàm chung
        checkGameStateAfterTurn();
    }

    public void applyNetworkCommand(String command) {
        if (command.equals("REPLAY_REQUEST")) {
            this.opponentWantsToReplay = true;
            checkReplayStatus();
        } else if (command.equals("RESTART_NOW")) {
            gui.restartGame();
        }
    }

    public void userClickedReplay() {
        this.iWantToReplay = true;
        if (networkManager != null) {
            networkManager.sendCommand("REPLAY_REQUEST");
        }
        checkReplayStatus();
    }

    private void checkReplayStatus() {
        if (iWantToReplay && opponentWantsToReplay) {
            this.iWantToReplay = false;
            this.opponentWantsToReplay = false;

            // Chỉ Host ra lệnh Restart để đồng bộ
            if (isClientWhite) {
                networkManager.sendCommand("RESTART_NOW");
                gui.restartGame();
            }
            // Client chờ lệnh RESTART_NOW
        }
    }
}