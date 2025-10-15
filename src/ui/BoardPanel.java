package ui;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    public BoardPanel() {
        setLayout(new GridLayout(8, 8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = new JPanel();
                cell.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                add(cell);
            }
        }
    }
}
