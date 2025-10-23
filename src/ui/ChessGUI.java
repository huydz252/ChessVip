package ui;

import javax.swing.*;
import logic.GameController;
import java.awt.*;

public class ChessGUI extends JFrame {

    private GameController gameController;
    private BoardPanel boardPanel;
    private SidebarPanel sidebarPanel;       // Panel mới bên phải
    private PlayerInfoPanel playerInfoPanel; // Panel mới bên dưới

    // Định nghĩa màu sắc chủ đạo
    public static final Color COLOR_BACKGROUND = new Color(211, 211, 211);
    public static final Color COLOR_PANEL = Color.WHITE;
    public static final Color COLOR_TEXT = Color.BLACK;

    public ChessGUI() {
        gameController = new GameController();
        setTitle("ChessMaster");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Layout chính là BorderLayout

        // ----- 1. THANH MENU (NORTH) -----
        // Giữ nguyên MenuPanel của bạn
        add(new MenuPanel(), BorderLayout.NORTH);

        // ----- 2. BÀN CỜ (CENTER) -----
        // SỬA ĐỔI: boardPanel cần một tham chiếu đến ChessGUI để gọi update
        boardPanel = new BoardPanel(gameController, this);
        add(boardPanel, BorderLayout.CENTER);

        // ----- 3. THANH BÊN (EAST) -----
        // SỬA ĐỔI: Thay thế JPanel cũ bằng SidebarPanel mới
        sidebarPanel = new SidebarPanel(gameController, this);
        add(sidebarPanel, BorderLayout.EAST);

        // ----- 4. THANH NGƯỜI CHƠI (SOUTH) -----
        // SỬA ĐỔI: Thêm panel mới cho thông tin người chơi
        playerInfoPanel = new PlayerInfoPanel();
        add(playerInfoPanel, BorderLayout.SOUTH);

        // Hoàn thiện Frame
        getContentPane().setBackground(COLOR_BACKGROUND); // Màu nền cho các khoảng trống
        pack(); // Tự động điều chỉnh kích thước
        setMinimumSize(new Dimension(900, 700)); // Đặt kích thước tối thiểu
        setLocationRelativeTo(null);
        setVisible(true);

        // Cập nhật GUI lần đầu
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
    
    /**
     * Hàm riêng cho nút Restart
     */
    public void restartGame() {
        dispose();
        SwingUtilities.invokeLater(ChessGUI::new);
    }

    public static void main(String[] args) {
        // Cài đặt Look and Feel cho đẹp hơn 
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}