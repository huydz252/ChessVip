package ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Image;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;

public class Splash extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private final Color COLOR_BACKGROUND = new Color(48, 46, 43);
    private final Color COLOR_GREEN = new Color(118, 185, 71);
    private final Color COLOR_WHITE = Color.WHITE;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Splash frame = new Splash();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public Splash() {
        setTitle("ChessMaster");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 450);
        setLocationRelativeTo(null);
        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBackground(COLOR_BACKGROUND);
        setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblImage = new JLabel();
        try {

            Image img = ImageIO.read(getClass().getResource("/resources/images/logo.png"));
            ImageIcon icon = new ImageIcon(img.getScaledInstance(380, 380, Image.SCALE_AREA_AVERAGING));
            lblImage.setIcon(icon);
        } catch (Exception e) {
            lblImage.setText("Không tìm thấy ảnh");
            lblImage.setForeground(COLOR_WHITE);
            System.err.println("Không tìm thấy ảnh /resources/images/logo.png");
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 20);
        contentPane.add(lblImage, gbc);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 0;
        gbcRight.anchor = GridBagConstraints.WEST;

        Font textFont = new Font("Arial", Font.BOLD, 28);

        JLabel lblLine1 = new JLabel("ChessMaster");
        lblLine1.setFont(textFont);
        lblLine1.setForeground(COLOR_WHITE);
        gbcRight.gridy = 0;
        gbcRight.insets = new Insets(0, 0, 5, 0);
        rightPanel.add(lblLine1, gbcRight);

        JLabel lblLine2 = new JLabel("");
        lblLine2.setFont(textFont);
        lblLine2.setForeground(COLOR_WHITE);
        gbcRight.gridy = 1;
        rightPanel.add(lblLine2, gbcRight);

        JLabel lblLine3 = new JLabel("");
        lblLine3.setFont(textFont);
        lblLine3.setForeground(COLOR_WHITE);
        gbcRight.gridy = 2;
        gbcRight.insets = new Insets(0, 0, 40, 0);
        rightPanel.add(lblLine3, gbcRight);

        JButton btnStart = new JButton("Bắt đầu");
        btnStart.setFont(new Font("Arial", Font.BOLD, 20));
        btnStart.setForeground(COLOR_WHITE);
        btnStart.setBackground(COLOR_GREEN);
        btnStart.setFocusPainted(false);
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStart.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MainMenu();
                dispose();
            }
        });

        gbcRight.gridy = 3;
        gbcRight.anchor = GridBagConstraints.CENTER;
        rightPanel.add(btnStart, gbcRight);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(20, 0, 20, 40);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        contentPane.add(rightPanel, gbc);
    }
}
