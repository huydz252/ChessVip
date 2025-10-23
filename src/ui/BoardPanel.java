package ui;

import logic.GameController;
import logic.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List; // Sửa import java.util.List

public class BoardPanel extends JPanel {
    private JPanel[][] cells = new JPanel[8][8];
    private GameController gameController;
    private ChessGUI chessGUI;

    // SỬA: Định nghĩa màu sắc cho dễ quản lý
    private final Color LIGHT_CELL_COLOR = new Color(240, 217, 181); // Màu Tan
    private final Color DARK_CELL_COLOR = new Color(181, 136, 99);   // Màu Brown
    private final Color SELECTED_BORDER_COLOR = Color.YELLOW;        // Viền quân được chọn
    private final Color MOVE_BORDER_COLOR = new Color(0, 220, 0);  // Viền nước đi
    private final Color CAPTURE_BORDER_COLOR = Color.RED;            // Viền ăn quân

    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<int[]> highlightCells = new ArrayList<>(); // Dùng List thay vì java.util.List

    public BoardPanel(GameController gameController, ChessGUI gui) {
        this.gameController = gameController;
        this.chessGUI = gui;
        
        setLayout(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = new JPanel(new BorderLayout()); // SỬA: Dùng BorderLayout để căn giữa JLabel
                
                // Dùng màu sắc mới
                cell.setBackground((row + col) % 2 == 0 ? LIGHT_CELL_COLOR : DARK_CELL_COLOR);
                
                //Xóa viền đen mặc định, để updateBoard() toàn quyền xử lý
                
                final int r = row;
                final int c = col;

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleClick(r, c);
                    }
                });
                
                cells[row][col] = cell;
                add(cell);
            }
        }
        updateBoard();
    }

    /**
     * SỬA: Cải thiện logic click chuột
     */
    private void handleClick(int row, int col) {
        Piece clickedPiece = gameController.getBoard().getPiece(row, col);

        if (selectedRow == -1) {
            // 1. CHƯA CHỌN GÌ: Thử chọn quân
            if (clickedPiece != null && clickedPiece.isWhite() == gameController.isWhiteTurn()) {
                selectPiece(row, col);
            }
        } else {
            // 2. ĐÃ CHỌN QUÂN
            if (row == selectedRow && col == selectedCol) {
                // 2a. Click lại quân cũ -> Hủy chọn
                deselectPiece();
                updateBoard();
            } else if (clickedPiece != null && clickedPiece.isWhite() == gameController.isWhiteTurn()) {
                // 2b. Click quân cùng màu khác -> Đổi lựa chọn
                selectPiece(row, col); // Tự động hủy chọn cũ và chọn mới
            } else {
                // 2c. Click ô trống hoặc quân địch -> Thử di chuyển
                boolean moved = gameController.move(selectedRow, selectedCol, row, col);
                String moveNotation = getMoveNotation(selectedRow, selectedCol, row, col); // Lấy ký hiệu
                
                deselectPiece(); // Luôn hủy chọn sau khi thử di chuyển
                
                if (moved) {
                    // SỬA: Gọi hàm cập nhật TỔNG của GUI
                    chessGUI.updateGame(moveNotation, true);
                    // updateGame() đã bao gồm updateBoard(), không cần gọi lại
                } else {
                    // Nước đi không hợp lệ (ví dụ: click ra ngoài),
                    // chỉ cần vẽ lại bàn cờ để xóa highlight
                    updateBoard();
                }
            }
        }
    }

    /**
     * Hàm helper mới: Chọn một quân cờ
     */
    private void selectPiece(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        highlightCells.clear();
        
        Piece piece = gameController.getBoard().getPiece(row, col);
        if (piece == null) return;
        
        Piece[][] board = gameController.getBoard().getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                // TODO: Nên dùng gameController.isValidMove(...) nếu có,
                // vì nó kiểm tra cả luật tự chiếu
                if (piece.isValidMove(r, c, board)) {
                    highlightCells.add(new int[]{r, c});
                }
            }
        }
        updateBoard(); // Cập nhật để hiển thị highlight
    }

    /**
     * Hàm helper mới: Bỏ chọn
     */
    private void deselectPiece() {
        selectedRow = -1;
        selectedCol = -1;
        highlightCells.clear();
    }

    /**
     * Hàm helper mới: Lấy ký hiệu nước đi
     */
    private String getMoveNotation(int r1, int c1, int r2, int c2) {
        // Chuyển đổi tọa độ mảng (0-7, 0-7) sang cờ vua (a1-h8)
        char fromCol = (char)('a' + c1);
        int fromRow = 8 - r1;
        char toCol = (char)('a' + c2);
        int toRow = 8 - r2;
        return "" + fromCol + fromRow + " " + toCol + toRow;
    }

    /**
     * SỬA: Cập nhật logic vẽ viền (border) và màu sắc
     */
    public void updateBoard() {
        Piece[][] boardPieces = gameController.getBoard().getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = cells[row][col];
                cell.removeAll();
                cell.setBorder(null); // Xóa viền cũ trước

                // --- 1. Set màu nền ---
                Color baseColor = (row + col) % 2 == 0 ? LIGHT_CELL_COLOR : DARK_CELL_COLOR;
                cell.setBackground(baseColor);

                // --- 2. Kiểm tra highlight (Vẽ viền nước đi) ---
                boolean isHighlight = false;
                for (int[] h : highlightCells) {
                    if (h[0] == row && h[1] == col) {
                        isHighlight = true;
                        Piece targetPiece = boardPieces[row][col];
                        Color borderColor;
                        if (targetPiece != null) {
                            borderColor = CAPTURE_BORDER_COLOR; // ô có quân địch (isValidMove đã check)
                        } else {
                            borderColor = MOVE_BORDER_COLOR;    // ô trống
                        }
                        cell.setBorder(BorderFactory.createLineBorder(borderColor, 3));
                        break;
                    }
                }

                // --- 3. Vẽ viền quân đang CHỌN (Ưu tiên cao hơn) ---
                // (Ghi đè viền highlight nếu click lại)
                if (row == selectedRow && col == selectedCol) {
                    cell.setBorder(BorderFactory.createLineBorder(SELECTED_BORDER_COLOR, 3));
                }
                
                // --- 4. Thêm quân cờ (Ảnh) ---
                Piece piece = boardPieces[row][col];
                if (piece != null) {
                    ImageIcon icon = piece.getImage();
                    if (icon != null) {
                        JLabel label = new JLabel(icon);
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setVerticalAlignment(SwingConstants.CENTER);
                        cell.add(label, BorderLayout.CENTER); // SỬA: Căn giữa ảnh
                    } else {
                        System.out.println("ImageIcon is null for piece at " + row + "," + col);
                    }
                }

                cell.revalidate();
                cell.repaint();
            }
        }
    }
}