package ui;

import java.awt.*;
import javax.swing.*;

public class PlayerInfoPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JLabel playerNameLabel;
    private JLabel turnLabel;
    private JLabel materialLabel;

    public PlayerInfoPanel() {
        setBackground(ChessGUI.COLOR_PANEL);
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY)); // Viền trên

        playerNameLabel = new JLabel("Chơi ẩn danh");
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerNameLabel.setForeground(ChessGUI.COLOR_TEXT);
        add(playerNameLabel);

        turnLabel = new JLabel("Trắng đi");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setForeground(Color.RED);
        add(turnLabel);

    }

    public void updateTurn(boolean isWhiteTurn) {
        turnLabel.setText(isWhiteTurn ? "Lượt Trắng" : "Lượt Đen");
    }

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
