package ui;

import logic.GameController;
import logic.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class BoardPanel extends JPanel {
    private JPanel[][] cells = new JPanel[8][8];
    private GameController gameController;

    //lấy thông tin ô đc chọn (chưa chọn = -1)
    private int selectedRow = -1;
    private int selectedCol = -1;
    
    private java.util.List<int[]> highlightCells = new ArrayList<>();

    public BoardPanel(GameController gameController) {
        this.gameController = gameController;
        setLayout(new GridLayout(8, 8));

        // Tạo các ô
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = new JPanel();
                cell.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                final int r = row;
                final int c = col;

                // Click listener cho từng ô
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleClick(r, c);
                    }
                });
                
                // từng phần tử trong mảng 2 chiều cells lần lượt đc gán bằng 1 cell (JPanel)
                cells[row][col] = cell;
                add(cell);
            }
        }

        // Cập nhật ban đầu
        updateBoard();
    }

    private void handleClick(int row, int col) {
    Piece clickedPiece = gameController.getBoard().getPiece(row, col);

    if (selectedRow == -1 && clickedPiece != null && clickedPiece.isWhite() == gameController.isWhiteTurn()) {
        // Chọn quân cờ của mình
        selectedRow = row;
        selectedCol = col;
        cells[row][col].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

        // Xóa danh sách highlight cũ
        highlightCells.clear();

        Piece[][] board = gameController.getBoard().getBoard();

        // Duyệt tất cả ô, check nước đi hợp lệ
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (clickedPiece.isValidMove(r, c, board)) {
                    highlightCells.add(new int[]{r, c});
                }
            }
        }

        // Vẽ lại bàn cờ với highlight
        updateBoard();

    } else if (selectedRow != -1) {
        // Chọn ô đích
        boolean moved = gameController.move(selectedRow, selectedCol, row, col);

        // Xóa highlight và border
        highlightCells.clear();
        cells[selectedRow][selectedCol].setBorder(BorderFactory.createLineBorder(Color.BLACK));

        selectedRow = -1;
        selectedCol = -1;

        // Cập nhật bàn cờ
        updateBoard();

        if (!moved) {
            // Di chuyển không hợp lệ
            JOptionPane.showMessageDialog(this, "Invalid move!");
        }
    }
}

    public void updateBoard() {
        Piece[][] boardPieces = gameController.getBoard().getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = cells[row][col];
                cell.removeAll();

                // --- Set màu nền ---
                Color baseColor = (row + col) % 2 == 0 ? Color.WHITE : Color.GRAY;
                cell.setBackground(baseColor);

                // --- Kiểm tra highlight để set viền ---
                boolean isHighlight = false;
                Color borderColor = Color.BLACK; // default

                for (int[] h : highlightCells) {
                    if (h[0] == row && h[1] == col) {
                        isHighlight = true;
                        Piece targetPiece = boardPieces[row][col];
                        if (targetPiece != null && targetPiece.isWhite() != boardPieces[selectedRow][selectedCol].isWhite()) {
                            borderColor = Color.RED;   // ô có quân địch
                        } else {
                        	borderColor = new Color(0, 220, 0); // xanh lá đậm  // ô trống hoặc quân cùng màu
                        }
                        break;
                    }
                }

                // Nếu ô đang được highlight thì set viền
                if (isHighlight) {
                    cell.setBorder(BorderFactory.createLineBorder(borderColor, 3));
                } else {
                    cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                }

                // --- Thêm quân cờ ---
                Piece piece = boardPieces[row][col];
                if (piece != null) {
                    ImageIcon icon = piece.getImage();
                    if (icon != null) {
                        JLabel label = new JLabel(icon);
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setVerticalAlignment(SwingConstants.CENTER);
                        cell.add(label);
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
