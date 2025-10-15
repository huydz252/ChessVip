package ui;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    public JButton localBtn;
    public JButton aiBtn;
    public JButton onlineBtn;

    public MenuPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        localBtn = new JButton("Play vs Player");
        aiBtn = new JButton("Play vs AI");
        onlineBtn = new JButton("Online");

        add(localBtn);
        add(aiBtn);
        add(onlineBtn);
    }
}
