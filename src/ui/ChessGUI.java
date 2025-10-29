package ui;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;

import logic.GameController;
import logic.GameMode;
import network.NetworkManager;

public class ChessGUI extends JFrame {

    private GameController gameController;
    private BoardPanel boardPanel;
    private SidebarPanel sidebarPanel;       
    private PlayerInfoPanel playerInfoPanel; 
    private NetworkManager networkManager;

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
        
        if (mode == GameMode.PLAYER_VS_PLAYER) {
            this.networkManager = new NetworkManager(gameController);
            gameController.setNetworkManager(networkManager);
        }

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
    
    //hàm yêu cầu lật bàn cờ (client)
    public void flipBoard() {
        if (boardPanel != null) {
            boardPanel.flipBoard();
        }
    }
    
  
    public void startHostGame() {
        if (networkManager != null) {
            networkManager.startHost();
            setTitle("ChessMaster (Host) - Đang chờ đối thủ...");
            
            // (update: thêm 1 JLabel vào GUI để hiển thị IP của Host ở đây)
            // Ví dụ: Lấy IP nội bộ
            try {
                String ip = InetAddress.getLocalHost().getHostAddress();
                sidebarPanel.addMove("Mời bạn bè với IP: " + ip);
            } catch (Exception e) {
                sidebarPanel.addMove("Không thể lấy IP Host.");
            }
        }
    }
    
    public void startJoinGame(String ip) {
        if (networkManager != null) {
            networkManager.startJoin(ip);
            setTitle("ChessMaster (Client) - Đang kết nối tới " + ip + "...");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Chạy qua MainMenu
        SwingUtilities.invokeLater(MainMenu::new);
    }
}