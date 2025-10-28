package ui;

import javax.swing.*;
import java.awt.*;

import logic.GameController;
import logic.GameMode;

public class ChessGUI extends JFrame {

    private GameController gameController;
    private BoardPanel boardPanel;
    private SidebarPanel sidebarPanel;       // Panel mới bên phải
    private PlayerInfoPanel playerInfoPanel; // Panel mới bên dưới

    // Định nghĩa màu sắc chủ đạo
    public static final Color COLOR_BACKGROUND = new Color(211, 211, 211);
    public static final Color COLOR_PANEL = Color.WHITE;
    public static final Color COLOR_TEXT = Color.BLACK;

    public ChessGUI(GameMode mode) {
        gameController = new GameController(this, mode);
        setTitle("ChessMaster");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Layout chính 

        add(new MenuPanel(), BorderLayout.NORTH);

        boardPanel = new BoardPanel(gameController, this);
        add(boardPanel, BorderLayout.CENTER);

        sidebarPanel = new SidebarPanel(gameController, this);
        add(sidebarPanel, BorderLayout.EAST);

        playerInfoPanel = new PlayerInfoPanel();
        add(playerInfoPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(COLOR_BACKGROUND);
        pack(); 
        setMinimumSize(new Dimension(900, 700)); 
        setLocationRelativeTo(null);
        setVisible(true);

        updateGame("Bắt đầu ván cờ...", true);
    }

    /**
     * Hàm trung tâm để cập nhật toàn bộ GUI sau một nước đi.
     * Sẽ được gọi bởi BoardPanel sau khi một nước đi thành công.
     * @param moveNotation Ký hiệu nước đi (ví dụ: "e4")
     */
    public void updateGame(String moveNotation, boolean isNewMove) {
        boardPanel.updateBoard();
        playerInfoPanel.updateTurn(gameController.isWhiteTurn());
        
        // Cập nhật danh sách nước đi
        if (isNewMove) {
            sidebarPanel.addMove(moveNotation);
        } else {
            // Xử lý cho Undo
            sidebarPanel.removeLastMove();
        }

    }
    
    public void restartGame() {
        dispose();
        SwingUtilities.invokeLater(MainMenu::new);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(MainMenu::new);
    }
}