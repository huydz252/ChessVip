package ui;

import javax.swing.*;
import java.awt.*;

public class ChessGUI extends JFrame {

    public ChessGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLayout(new BorderLayout());

        add(new MenuPanel(), BorderLayout.NORTH);
        add(new BoardPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}
