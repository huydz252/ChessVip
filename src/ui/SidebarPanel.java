package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import logic.GameController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public class SidebarPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GameController gameController;
    private IGameGUI gui;
    private DefaultListModel<String> moveListModel;

    public SidebarPanel(GameController gc, IGameGUI gui) {
        this.gameController = gc;
        this.gui = gui;

        setPreferredSize(new Dimension(280, 600));
        setBackground(ChessGUI.COLOR_PANEL); 
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel opponentLabel;
        
        try {
            URL imageUrl = getClass().getResource("/resources/images/bot.png");
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                ImageIcon botIcon = new ImageIcon(scaledImage);
                
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
        
        opponentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(opponentLabel, BorderLayout.NORTH);

        moveListModel = new DefaultListModel<>();
        JList<String> moveList = new JList<>(moveListModel);
        moveList.setBackground(ChessGUI.COLOR_BACKGROUND);
        moveList.setForeground(ChessGUI.COLOR_TEXT);
        moveList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(moveList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setOpaque(false); // Trong suốt

        JButton btnUndo = createStyledButton("Undo");
        JButton btnRestart = createStyledButton("Menu");
        JButton btnResign = createStyledButton("Đầu hàng");
        JButton btnHint = createStyledButton("Gợi ý");

        btnUndo.addActionListener(this::onUndo);
        btnRestart.addActionListener(e -> gui.restartGame());
        btnResign.addActionListener(this::onResign);

        buttonPanel.add(btnUndo);
        buttonPanel.add(btnHint);
        buttonPanel.add(btnRestart);
        buttonPanel.add(btnResign);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

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

    private void onUndo(ActionEvent e) {
        gameController.undoLastMove();
        gui.updateGame(null, false); // false = không phải nước đi mới
    }

    private void onResign(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc muốn chấp nhận thua?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Bạn đã thua. Ván cờ kết thúc.");
            // (Thêm logic khóa bàn cờ ở đây)
        }
    }

    public void addMove(String moveNotation) {
        moveListModel.addElement(moveNotation);
    }
    
    public void removeLastMove() {
        if (!moveListModel.isEmpty()) {
            moveListModel.removeElementAt(moveListModel.getSize() - 1);
        }
    }
}