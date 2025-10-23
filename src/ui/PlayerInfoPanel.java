package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {

    private JLabel playerNameLabel;
    private JLabel turnLabel;
    private JLabel materialLabel;

    public PlayerInfoPanel() {
        setBackground(ChessGUI.COLOR_PANEL);
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10)); // Căn trái
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY)); // Viền trên

        // (Thêm avatar của bạn ở đây)
        
        playerNameLabel = new JLabel("Chơi ẩn danh");
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerNameLabel.setForeground(ChessGUI.COLOR_TEXT);
        add(playerNameLabel);

        // Nhãn lượt đi
        turnLabel = new JLabel("Trắng đi");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setForeground(Color.RED); // Nổi bật
        add(turnLabel);

       
    }

    // Được gọi từ ChessGUI
    public void updateTurn(boolean isWhiteTurn) {
        turnLabel.setText(isWhiteTurn ? "Lượt Trắng" : "Lượt Đen");
    }

    // Được gọi từ ChessGUI
    public void updateMaterial(int materialAdvantage) {
        if (materialAdvantage > 0) {
            materialLabel.setText("+" + materialAdvantage);
            materialLabel.setForeground(Color.GREEN);
        } else if (materialAdvantage < 0) {
            materialLabel.setText(String.valueOf(materialAdvantage));
            materialLabel.setForeground(Color.RED);
        } else {
            materialLabel.setText("+0");
            materialLabel.setForeground(Color.LIGHT_GRAY);
        }
    }
}