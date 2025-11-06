package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import logic.GameController;
import logic.GameMode;

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
        
        String labelText = "";
        URL imageUrl = null;
        
        GameMode mode = gameController.getGameMode();
        
        if (mode == GameMode.PLAYER_VS_AI) {
            labelText = "Agent (Bot)";
            imageUrl = getClass().getResource("/resources/images/bot.png");
            
        } else if (mode == GameMode.PLAYER_VS_PLAYER) {

            labelText = "Player ABC"; 
            imageUrl = getClass().getResource("/resources/images/player.png");
            
        } else {
            labelText = "Đối thủ";
            imageUrl = getClass().getResource("/resources/images/default.png");
        }

        ImageIcon opponentIcon = null; 
        try {
            if (imageUrl == null) {
                throw new Exception("Không tìm thấy tài nguyên ảnh.");
            }
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            opponentIcon = new ImageIcon(scaledImage);
            
        } catch (Exception e) {
             System.err.println("Lỗi khi nạp ảnh: " + e.getMessage());
        }
        
        JLabel opponentLabel = new JLabel(labelText, opponentIcon, SwingConstants.LEADING);
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
        buttonPanel.setOpaque(false); 
        
        GameMode currentMode = gameController.getGameMode();
        if(currentMode == GameMode.PLAYER_VS_AI) {
        	//undo
        	JButton btnUndo = createStyledButton("Undo");
        	btnUndo.addActionListener(this::onUndo);
        	buttonPanel.add(btnUndo);
        	
        	//goi y
        	JButton btnHint = createStyledButton("Gợi ý");
            buttonPanel.add(btnHint);
            btnHint.addActionListener(e -> {
            	gui.showMessage("Gợi ý", "Tính năng đang được phát triển");
            });
        }
        else if (currentMode == GameMode.PLAYER_VS_PLAYER) {
            JButton drawButton = createStyledButton("Xin hòa");
            drawButton.addActionListener(e -> {
                gui.showMessage("Thông báo", "Đã gửi yêu cầu xin hòa!");
                //update sau: gửi yêu cầu xin hòa qua mạng
            });
            buttonPanel.add(drawButton);
        }

        JButton menuButton = createStyledButton("Menu");
        menuButton.addActionListener(e -> {
            gui.restartGame(); 
        });
        buttonPanel.add(menuButton);
        
        JButton resignButton = createStyledButton("Đầu hàng");
        resignButton.addActionListener(e -> {
            if(onResign()) {
            	gameController.setIsGameOver(true);
            	gameController.getBoard().isGameOver(gc);
            }
        });
        buttonPanel.add(resignButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
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
        gameController.undoLastMove();
        gui.updateGame("Undo AI", false); // false = không phải nước đi mới
        gui.updateGame("Undo Player", false);
    }

    private boolean onResign() {
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc muốn chấp nhận thua?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
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