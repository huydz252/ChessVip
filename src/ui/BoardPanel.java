package ui;

import logic.GameController;
import logic.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {
    private JPanel[][] cells = new JPanel[8][8];
    private GameController gameController;

    //lấy thông tin ô đc chọn (chưa chọn = -1)
    private int selectedRow = -1;
    private int selectedCol = -1;

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
            cells[row][col].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        } else if (selectedRow != -1) {
            // Chọn ô đích
            boolean moved = gameController.move(selectedRow, selectedCol, row, col);
            // Xóa highlight
            cells[selectedRow][selectedCol].setBorder(BorderFactory.createLineBorder(Color.BLACK));

            selectedRow = -1;
            selectedCol = -1;

            if (moved) {
                updateBoard();
            } else {
                // Di chuyển không hợp lệ
                JOptionPane.showMessageDialog(this, "Invalid move!");
            }
        }
    }

    // Cập nhật giao diện bàn cờ
    public void updateBoard() {
        Piece[][] boardPieces = gameController.getBoard().getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = cells[row][col];
                cell.removeAll();

                Piece piece = boardPieces[row][col];
                if (piece != null) {
                    ImageIcon icon = piece.getImage();
                    if (icon != null) {
                        System.out.println("Loaded image for piece at " + row + "," + col);
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
