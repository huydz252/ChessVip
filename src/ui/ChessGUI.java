package ui;

import javax.swing.*;
import java.awt.*;

import logic.GameController;
import logic.GameMode;
public class ChessGUI extends JFrame implements IGameGUI{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GameController gameController;
    private BoardPanel boardPanel;
    private SidebarPanel sidebarPanel;       
    private PlayerInfoPanel playerInfoPanel; 
    
    public static final Color COLOR_BACKGROUND = new Color(211, 211, 211);
    public static final Color COLOR_PANEL = Color.WHITE;
    public static final Color COLOR_TEXT = Color.BLACK;

    public ChessGUI() {
    	
        gameController = new GameController(this, GameMode.PLAYER_VS_AI);
        
        setTitle("Chess AI Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());  

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
		// TODO Auto-generated method stub
		
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

	@Override
	public void showGameOverDialog(String title, String message) {
		String [] options = {"Chơi lại", "Về Menu"};
		int choice = JOptionPane.showOptionDialog(
	            this, 
	            message, 
	            title, 
	            JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
		if(choice == JOptionPane.YES_OPTION) {
			restartGame();
		}else if (choice == JOptionPane.NO_OPTION){
			dispose();
			SwingUtilities.invokeLater(MainMenu::new);
		}   
	}
   
}