package ui;

import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Splash extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

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
        setTitle("ChessMaster"); // đặt tên game
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);
        setLocationRelativeTo(null); // căn giữa màn hình

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 50));
        setContentPane(contentPane);

        // Tiêu đề
        JLabel lblTitle = new JLabel("CHESSMASTER");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        contentPane.add(lblTitle, BorderLayout.CENTER);

        // Nút Bắt Đầu
        JButton btnStart = new JButton("Bắt Đầu");
        btnStart.setFont(new Font("Arial", Font.BOLD, 24));
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Mở ChessGUI
                new ChessGUI();
                // Đóng Splash
                dispose();
            }
        });
        contentPane.add(btnStart, BorderLayout.SOUTH);
    }
}
