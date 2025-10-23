package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import logic.GameController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public class SidebarPanel extends JPanel {

    private GameController gameController;
    private ChessGUI chessGUI; // Tham chiếu để gọi restart
    private JTextArea moveListArea;
    private DefaultListModel<String> moveListModel;

    public SidebarPanel(GameController gc, ChessGUI gui) {
        this.gameController = gc;
        this.chessGUI = gui;

        setPreferredSize(new Dimension(280, 600));
        setBackground(ChessGUI.COLOR_PANEL); // Màu nền tối
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel opponentLabel;
        
        try {
            // Tải ảnh bot.png từ thư mục resources (src/images)
            URL imageUrl = getClass().getResource("/resources/images/bot.png");
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                // Điều chỉnh kích thước ảnh (ví dụ: 40x40 pixel)
                Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                ImageIcon botIcon = new ImageIcon(scaledImage);
                
                // Tạo JLabel với cả ảnh và chữ
                opponentLabel = new JLabel("Agent (Bot)", botIcon, SwingConstants.LEADING); 
            } else {
                opponentLabel = new JLabel("Agent (Bot)"); // Dùng chữ nếu không tìm thấy ảnh
                System.err.println("Không tìm thấy ảnh: /images/bot.png");
            }
        } catch (Exception e) {
             opponentLabel = new JLabel("Agent (Bot)");
             System.err.println("Lỗi khi nạp ảnh bot: " + e.getMessage());
        }

        opponentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        opponentLabel.setForeground(ChessGUI.COLOR_TEXT);
        opponentLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        // CĂN LỀ: Đặt LEFT thay vì CENTER (để chữ Agent không bị lệch)
        opponentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(opponentLabel, BorderLayout.NORTH);

        // --- 2. Danh sách nước đi ---
        moveListModel = new DefaultListModel<>();
        JList<String> moveList = new JList<>(moveListModel);
        moveList.setBackground(ChessGUI.COLOR_BACKGROUND);
        moveList.setForeground(ChessGUI.COLOR_TEXT);
        moveList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(moveList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Panel Nút bấm ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setOpaque(false); // Trong suốt

        JButton btnUndo = createStyledButton("Undo");
        JButton btnRestart = createStyledButton("Restart");
        JButton btnResign = createStyledButton("Đầu hàng");
        JButton btnHint = createStyledButton("Gợi ý");

        // Thêm sự kiện
        btnUndo.addActionListener(this::onUndo);
        btnRestart.addActionListener(e -> chessGUI.restartGame());
        btnResign.addActionListener(this::onResign);

        buttonPanel.add(btnUndo);
        buttonPanel.add(btnHint);
        buttonPanel.add(btnRestart);
        buttonPanel.add(btnResign);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Hàm tiện ích để tạo nút cho đẹp
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        
        button.setFont(new Font("Arial", Font.BOLD, 16));
        
        button.setBackground(new Color(200, 200, 200));
        button.setForeground(ChessGUI.COLOR_TEXT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Sự kiện Undo
    private void onUndo(ActionEvent e) {
        gameController.undoLastMove();
        chessGUI.updateGame(null, false); // false = không phải nước đi mới
    }

    // Sự kiện Chấp nhận thua
    private void onResign(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(
            chessGUI, 
            "Bạn có chắc muốn chấp nhận thua?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(chessGUI, "Bạn đã thua. Ván cờ kết thúc.");
            // (Thêm logic khóa bàn cờ ở đây)
        }
    }

    // Được gọi từ ChessGUI
    public void addMove(String moveNotation) {
        moveListModel.addElement(moveNotation);
    }
    
    // Được gọi từ ChessGUI khi Undo
    public void removeLastMove() {
        if (!moveListModel.isEmpty()) {
            moveListModel.removeElementAt(moveListModel.getSize() - 1);
        }
    }
}