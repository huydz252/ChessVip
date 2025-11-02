package ui;

import logic.GameController;
import logic.move.Move;
import logic.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel[][] cells = new JPanel[8][8];
    private GameController gameController;
    private IGameGUI gui;
    
    private final Color LIGHT_CELL_COLOR = new Color(240, 217, 181); 
    private final Color DARK_CELL_COLOR = new Color(181, 136, 99);   
    private final Color SELECTED_BORDER_COLOR = Color.YELLOW;        
    private final Color MOVE_BORDER_COLOR = new Color(0, 220, 0);  
    private final Color CAPTURE_BORDER_COLOR = Color.RED;            

    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<int[]> highlightCells = new ArrayList<>(); 

    public BoardPanel(GameController gameController, IGameGUI gui) {
        this.gameController = gameController;
        this.gui = gui;
        
        setLayout(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = new JPanel(new BorderLayout()); 
                
                cell.setBackground((row + col) % 2 == 0 ? LIGHT_CELL_COLOR : DARK_CELL_COLOR);
                
                final int r = row;
                final int c = col;

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                    	if (gameController.isClientTurn()) { 
                            handleClick(r, c);
                        }
                    }
                });
                
                cells[row][col] = cell;
            }
        }
        
        if(gameController.isClientWhite()) {
        	//trắng
        	for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    add(cells[r][c]); // A8 (0,0) ở trên cùng
                }
            }
        }else {
        	//đen
        	for (int r = 7; r >= 0; r--) {			// Hàng 7 ở trên cùng
                for (int c = 7; c >= 0; c--) { 		// Cột 7 ở bên trái
                    add(cells[r][c]); 				
                }
            }
        }
        
        updateBoard();
    }

    /**
     * Xử lý click chuột của người chơi (Trắng).
     */
    private void handleClick(int row, int col) {
        Piece clickedPiece = gameController.getBoard().getPiece(row, col);

        if (selectedRow == -1) {
            if (clickedPiece != null && clickedPiece.isWhite() == gameController.isWhiteTurn()) {
                selectPiece(row, col);
            }
        } else {
            if (row == selectedRow && col == selectedCol) {
                deselectPiece();
                updateBoard();
            } else if (clickedPiece != null && clickedPiece.isWhite() == gameController.isWhiteTurn()) {
                selectPiece(row, col); 
            } else {
                
                boolean moved = gameController.move(selectedRow, selectedCol, row, col);
                String moveNotation = getMoveNotation(selectedRow, selectedCol, row, col); 
                
                deselectPiece(); 
                
                if (moved) {
                    gui.updateGame(moveNotation, true);
                    
                    gameController.handleAITurn(); 
                } else {
                    updateBoard(); 
                }
            }
        }
    }

    /**
     * Chọn một quân cờ và tìm nước đi hợp lệ.
     */
    private void selectPiece(int row, int col) {
        if (selectedRow != -1) {
             cells[selectedRow][selectedCol].setBorder(null); // Xóa viền cũ
        }
        
        selectedRow = row;
        selectedCol = col;
        highlightCells.clear();
        
        Piece piece = gameController.getBoard().getPiece(row, col);
        if (piece == null) return;
        
        Piece[][] board = gameController.getBoard().getBoard();
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (piece.isValidMove(r, c, board)) {
                    
                    Piece captured = board[r][c];
                    Move testMove = new Move(piece, r, c, captured);

                    gameController.getBoard().executeMove(testMove);
                    
                    if (!gameController.isCheck(piece.isWhite())) {
                        highlightCells.add(new int[]{r, c});
                    }

                    gameController.getBoard().undoLastMove();
                }
            }
        }
        updateBoard();
    }

    /**
     * Bỏ chọn quân.
     */
    private void deselectPiece() {
        selectedRow = -1;
        selectedCol = -1;
        highlightCells.clear();
    }

    /**
     * Lấy ký hiệu nước đi cơ bản (ví dụ: e2 e4).
     */
    private String getMoveNotation(int r1, int c1, int r2, int c2) {
        char fromCol = (char)('a' + c1);
        int fromRow = 8 - r1;
        char toCol = (char)('a' + c2);
        int toRow = 8 - r2;
        return "" + fromCol + fromRow + " " + toCol + toRow;
    }
    
    public void flipBoard() {
        this.removeAll(); 

        if(gameController.isClientWhite()) {
            //trắng
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    add(cells[r][c]); 
                }
            }
        }else {
            //đen
            for (int r = 7; r >= 0; r--) {
                for (int c = 7; c >= 0; c--) { 
                    add(cells[r][c]); 				
                }
            }
        }

        //Yêu cầu Panel vẽ lại
        this.revalidate();
        this.repaint();
    }

    /**
     * Cập nhật toàn bộ bàn cờ (vẽ lại quân và viền).
     */
    public void updateBoard() {
        Piece[][] boardPieces = gameController.getBoard().getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = cells[row][col];
                cell.removeAll();
                cell.setBorder(null); 

                Color baseColor = (row + col) % 2 == 0 ? LIGHT_CELL_COLOR : DARK_CELL_COLOR;
                cell.setBackground(baseColor);

                for (int[] h : highlightCells) {
                    if (h[0] == row && h[1] == col) {
                        Piece targetPiece = boardPieces[row][col];
                        Color borderColor = (targetPiece != null) ? CAPTURE_BORDER_COLOR : MOVE_BORDER_COLOR;
                        cell.setBorder(BorderFactory.createLineBorder(borderColor, 3));
                        break;
                    }
                }

                if (row == selectedRow && col == selectedCol) {
                    cell.setBorder(BorderFactory.createLineBorder(SELECTED_BORDER_COLOR, 3));
                }
                
                Piece piece = boardPieces[row][col];
                if (piece != null) {
                    ImageIcon icon = piece.getImage();
                    if (icon != null) {
                        JLabel label = new JLabel(icon);
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setVerticalAlignment(SwingConstants.CENTER);
                        cell.add(label, BorderLayout.CENTER); 
                    } else {
                        System.out.println("ImageIcon is null for piece at " + row + "," + col);
                    }
                }

                cell.revalidate();
                cell.repaint();
            }
        }
    }
}