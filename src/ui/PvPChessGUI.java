package ui;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import logic.GameController;
import logic.GameMode;
import network.NetworkManager;

// LỚP NÀY DÀNH RIÊNG CHO CHƠI MẠNG (PvP)
public class PvPChessGUI extends JFrame implements IGameGUI{

	private static final long serialVersionUID = 1L;
	private GameController gameController;
    private BoardPanel boardPanel;
    private SidebarPanel sidebarPanel;       
    private PlayerInfoPanel playerInfoPanel; 
    private NetworkManager networkManager; // Logic mạng nằm ở đây

    public static final Color COLOR_BACKGROUND = new Color(211, 211, 211);
    public static final Color COLOR_PANEL = Color.WHITE;
    public static final Color COLOR_TEXT = Color.BLACK;

    public PvPChessGUI() {
        
        gameController = new GameController(this, GameMode.PLAYER_VS_PLAYER);
        
        setTitle("ChessMaster (PvP)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); 

        add(new MenuPanel(), BorderLayout.NORTH);
        boardPanel = new BoardPanel(gameController, this);
        add(boardPanel, BorderLayout.CENTER);
        sidebarPanel = new SidebarPanel(gameController, this);
        add(sidebarPanel, BorderLayout.EAST);
        playerInfoPanel = new PlayerInfoPanel();
        add(playerInfoPanel, BorderLayout.SOUTH);
        
        this.networkManager = new NetworkManager(gameController);
        gameController.setNetworkManager(networkManager);

        getContentPane().setBackground(COLOR_BACKGROUND);
        pack(); 
        setMinimumSize(new Dimension(900, 700)); 
        setLocationRelativeTo(null);
        setVisible(true);

        updateGame("Bắt đầu ván cờ PvP...", true);
    }

    @Override
    public void updateGame(String moveNotation, boolean isNewMove) {
        boardPanel.updateBoard();
        playerInfoPanel.updateTurn(gameController.isWhiteTurn());
        if (isNewMove) {
            sidebarPanel.addMove(moveNotation);
        } else {
            sidebarPanel.removeLastMove();
        }
    }
    
    @Override
    public void restartGame() {
        dispose();
        SwingUtilities.invokeLater(MainMenu::new);
    }
    
    @Override
    public void flipBoard() {
        if (boardPanel != null) {
            boardPanel.flipBoard();
        }
    }
    
    public void startHostGame() {
        if (networkManager != null) {
            networkManager.startHost();
            setTitle("ChessMaster (Host) - Đang chờ đối thủ...");
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

	@Override
	public void showMessage(String title, String message) {
		JOptionPane.showMessageDialog(
	            this, 
	            message, 
	            title, 
	            JOptionPane.INFORMATION_MESSAGE
	        );
	}
	
	@Override
    public String showPromotionDialog() {
        String[] options = {"Queen", "Rook", "Knight", "Bishop"};
        String choice = (String) JOptionPane.showInputDialog(
                this, 
                "Chọn quân để phong cấp:",
                "Phong Cấp Tốt",
                JOptionPane.QUESTION_MESSAGE,
                null, 
                options,
                options[0]);
        
        if (choice == null || choice.isEmpty()) {
            choice = "Queen"; // Mặc định là Hậu
        }
        return choice;
    }

}