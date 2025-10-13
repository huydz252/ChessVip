package ui;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    public MenuPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton localBtn = new JButton("Play vs Player");
        JButton aiBtn = new JButton("Play vs AI");
        JButton onlineBtn = new JButton("Online");

        add(localBtn);
        add(aiBtn);
        add(onlineBtn);
    }
}
