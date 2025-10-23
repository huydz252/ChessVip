package ui;

import javax.swing.*;
import logic.GameController;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChessGUI extends JFrame {
    private GameController gameController;
    private BoardPanel boardPanel;
    private JLabel turnLabel;  // hiển thị lượt đi

    public ChessGUI() {
        gameController = new GameController();
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        // Panel menu trên cùng (giữ nguyên)
        add(new MenuPanel(), BorderLayout.NORTH);

        // Bàn cờ (2/3 trái)
        boardPanel = new BoardPanel(gameController);

        // Panel bên phải (1/3 phải)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Thông tin & Điều khiển"));
        rightPanel.setPreferredSize(new Dimension(300, 800));

        // Thông tin người chơi
        JLabel playerInfo = new JLabel("<html><b>Người chơi 1:</b> Trắng<br><b>Người chơi 2:</b> Đen</html>");
        playerInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nhãn lượt đi
        turnLabel = new JLabel(gameController.isWhiteTurn() ? "White to move" : "Black to move");
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Các nút điều khiển
        JButton btnUndo = new JButton("Undo");
        JButton btnRestart = new JButton("Restart");
        JButton btnExit = new JButton("Exit");

        // Căn giữa các nút
        btnUndo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRestart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thêm hành vi Undo
        btnUndo.addActionListener((ActionEvent e) -> {
            gameController.undoLastMove();
            boardPanel.updateBoard();  // vẽ lại bàn cờ
            turnLabel.setText(gameController.isWhiteTurn() ? "White to move" : "Black to move");
        });

        // Hành vi Restart
        btnRestart.addActionListener((ActionEvent e) -> {
            dispose();          // đóng cửa sổ hiện tại
            SwingUtilities.invokeLater(ChessGUI::new); // mở lại GUI mới
        });

        // Hành vi Exit
        btnExit.addActionListener((ActionEvent e) -> System.exit(0));

        // Thêm vào panel điều khiển
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(playerInfo);
        rightPanel.add(turnLabel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(btnUndo);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(btnRestart);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(btnExit);
        rightPanel.add(Box.createVerticalGlue());

        // Chia layout chính: trái là bàn cờ, phải là panel điều khiển
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                boardPanel,
                rightPanel
        );
        splitPane.setDividerLocation(0.67); // 2/3 - 1/3
        splitPane.setResizeWeight(0.67);
        splitPane.setDividerSize(4);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}
