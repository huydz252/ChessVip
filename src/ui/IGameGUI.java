package ui;

public interface IGameGUI {

	void updateGame(String moveNotation, boolean isNewMove);
	
	void restartGame();
	
	void flipBoard();
	
	void showMessage(String title, String message);
	
	String showPromotionDialog();
	void showGameOverDialog(String title, String message);
	
	
}
