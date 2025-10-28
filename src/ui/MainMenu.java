package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import logic.GameMode; // Import GameMode

public class MainMenu extends JFrame {

    private static final int MENU_WIDTH = 800;
    private static final int MENU_HEIGHT = 600;

    // Kích thước mong muốn cho icon (ví dụ: 40x40 pixel)
    private static final int ICON_SIZE = 40; 

    // Định nghĩa màu sắc cho theme (Theme tối)
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

        // Tạo Panel nền
        MenuBackgroundPanel mainPanel = new MenuBackgroundPanel();
        mainPanel.setLayout(new GridBagLayout()); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Giảm khoảng cách 1 chút
        gbc.gridwidth = GridBagConstraints.REMAINDER; 
        gbc.anchor = GridBagConstraints.CENTER;     
        gbc.fill = GridBagConstraints.HORIZONTAL;   // Kéo dãn nút

        // --- Tiêu đề ---
        JLabel titleLabel = new JLabel("ChessMaster", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 72));
        titleLabel.setForeground(COLOR_TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Giảm đệm
        
        // --- SỬA ĐỔI: Tạo các nút bấm có icon ---
        // (Giả sử bạn đã đặt 4 ảnh này vào src/resources/images/)
        JButton tutorialButton = createStyledButton("Hướng dẫn", "icon_tutorial.png");
        JButton pvaiButton = createStyledButton("Chơi với máy", "icon_ai.png");
        JButton pvpButton = createStyledButton("Chơi với bạn", "icon_pvp.png");
        JButton exitButton = createStyledButton("Thoát", "icon_exit.png");
        
        // --- Thêm các thành phần vào panel ---
        mainPanel.add(titleLabel, gbc);
        mainPanel.add(tutorialButton, gbc); // Nút Hướng dẫn
        mainPanel.add(pvaiButton, gbc);     // Nút Chơi vs AI
        mainPanel.add(pvpButton, gbc);      // Nút Chơi vs Bạn
        mainPanel.add(exitButton, gbc);     // Nút Thoát

        // Thêm panel chính vào Frame
        add(mainPanel);

        // --- Xử lý sự kiện ---
        tutorialButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng hướng dẫn đang được phát triển!");
        });
        
        pvaiButton.addActionListener(e -> startGame(GameMode.PLAYER_VS_AI));
        
        pvpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng (PvP) đang được phát triển!");
            // Khi sẵn sàng, hãy dùng dòng này:
            // startGame(GameMode.PLAYER_VS_PLAYER);
        });
        
        exitButton.addActionListener(e -> System.exit(0));

        // Hiển thị
        setVisible(true);
    }

    /**
     * THÊM MỚI: Hàm helper để tải và thay đổi kích thước icon
     */
    private ImageIcon loadIcon(String iconName, int size) {
        try {
            // Tải ảnh từ thư mục resources/images
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/images/" + iconName));
            
            // Thay đổi kích thước ảnh
            Image img = originalIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            
            return new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Không thể tải icon: " + iconName);
            // Trả về một icon rỗng (hoặc null) nếu lỗi
            return new ImageIcon(new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB));
        }
    }


    /**
     * SỬA ĐỔI: Hàm helper để tạo JButton, nay chấp nhận cả tên icon
     */
    private JButton createStyledButton(String text, String iconName) {
        
        // 1. Tải icon
        ImageIcon icon = loadIcon(iconName, ICON_SIZE);
        
        // 2. Tạo nút với text và icon
        JButton button = new JButton(text, icon);
        
        // 3. Căn chỉnh (Quan trọng)
        button.setHorizontalAlignment(SwingConstants.LEFT); // Căn icon và text về bên trái
        button.setIconTextGap(25); // Khoảng cách giữa icon và text
        
        // 4. Style (Giữ nguyên)
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setForeground(COLOR_TEXT);
        button.setBackground(COLOR_BUTTON);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // 5. Sửa đổi Padding (Đệm)
        // Thêm đệm bên trái để icon không bị sát mép
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 30)); 

        // 6. Hiệu ứng Hover (Giữ nguyên)
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

    /**
     * Đóng Menu và mở ChessGUI với chế độ đã chọn. (Giữ nguyên)
     */
    private void startGame(GameMode mode) {
        dispose();
        SwingUtilities.invokeLater(() -> new ChessGUI(mode));
    }

    /**
     * Lớp nội (inner class) để làm Panel nền (Giữ nguyên)
     */
    class MenuBackgroundPanel extends JPanel {
        private Image backgroundImage;

        public MenuBackgroundPanel() {
            try {
                backgroundImage = new ImageIcon(getClass().getResource("/images/menu_background.png")).getImage();
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