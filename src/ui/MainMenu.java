package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import logic.GameMode; // Import GameMode

public class MainMenu extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final int MENU_WIDTH = 800;
    private static final int MENU_HEIGHT = 600;

    private static final int ICON_SIZE = 40;

    //theme (tối)
    private static final Color COLOR_BACKGROUND = new Color(34, 40, 49);
    private static final Color COLOR_TEXT = new Color(238, 238, 238);
    private static final Color COLOR_BUTTON = new Color(57, 62, 70);
    private static final Color COLOR_BUTTON_HOVER = new Color(0, 173, 181);

    public MainMenu() {
        setTitle("ChessMaster - Chọn Chế Độ Chơi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MENU_WIDTH, MENU_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);

        MenuBackgroundPanel mainPanel = new MenuBackgroundPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("ChessMaster", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 72));
        titleLabel.setForeground(COLOR_TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton tutorialButton = createStyledButton("Hướng dẫn", "icon_tutorial.png");
        JButton pvaiButton = createStyledButton("Chơi với máy", "icon_ai.png");
        JButton pvpButton = createStyledButton("Chơi với bạn", "icon_pvp.png");
        JButton exitButton = createStyledButton("Thoát", "icon_exit.png");

        mainPanel.add(titleLabel, gbc);
        mainPanel.add(tutorialButton, gbc);
        mainPanel.add(pvaiButton, gbc);
        mainPanel.add(pvpButton, gbc);
        mainPanel.add(exitButton, gbc);

        add(mainPanel);

        tutorialButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng hướng dẫn đang được phát triển!");
        });

        pvaiButton.addActionListener(e -> startGame(GameMode.PLAYER_VS_AI));

        pvpButton.addActionListener(e -> {
            String[] options = {"Host (Tạo phòng)", "Join (Vào phòng)"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Bạn muốn làm chủ phòng hay tham gia?",
                    "Chơi với bạn (PvP)",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]); // (Mặc định chọn Host)

            if (choice == JOptionPane.YES_OPTION) { // 0 = Host
                startGame(GameMode.PLAYER_VS_PLAYER, 0, null);

            } else if (choice == JOptionPane.NO_OPTION) { // 1 = Join
                String ip = JOptionPane.showInputDialog(
                        this,
                        "Nhập địa chỉ IP của Host:",
                        "Vào phòng",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (ip != null && !ip.trim().isEmpty()) {
                    startGame(GameMode.PLAYER_VS_PLAYER, 1, ip.trim());
                }
                // Nếu hủy, không làm gì cả
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private ImageIcon loadIcon(String iconName, int size) {
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/images/" + iconName));
            Image img = originalIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);

            return new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Không thể tải icon: " + iconName);
            return new ImageIcon(new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB));
        }
    }

    private JButton createStyledButton(String text, String iconName) {

        ImageIcon icon = loadIcon(iconName, ICON_SIZE);

        JButton button = new JButton(text, icon);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(25);

        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setForeground(COLOR_TEXT);
        button.setBackground(COLOR_BUTTON);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(COLOR_BUTTON);
            }
        });
        return button;
    }

    private void startGame(GameMode mode, int pvpRole, String ip) {
        dispose();
        SwingUtilities.invokeLater(() -> {

            if (mode == GameMode.PLAYER_VS_PLAYER) {
                PvPChessGUI pvpGui = new PvPChessGUI();
                if (pvpRole == 0) { // Host
                    pvpGui.startHostGame();
                } else if (pvpRole == 1) { // Join
                    pvpGui.startJoinGame(ip);
                }
            } else if (mode == GameMode.PLAYER_VS_AI) {
                new ChessGUI();
            }
        });
    }

    private void startGame(GameMode mode) {
        startGame(mode, -1, null);
    }

    /**
     * (inner class) làm Panel nền
     */
    class MenuBackgroundPanel extends JPanel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private Image backgroundImage;

        public MenuBackgroundPanel() {
            try {
                backgroundImage = new ImageIcon(getClass().getResource("/resources/images/menu_background.png")).getImage();
            } catch (Exception e) {
                System.err.println("Không thể tải ảnh nền. Sử dụng màu mặc định.");
                backgroundImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                g.setColor(COLOR_BACKGROUND);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        }
    }
}
