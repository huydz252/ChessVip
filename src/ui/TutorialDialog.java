package ui; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TutorialDialog extends JDialog {

    public TutorialDialog(JFrame parent) {
        super(parent, "Hướng dẫn chơi cờ vua", true); // true = Modal (tắt bảng này mới bấm được cái khác)
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Luật chơi cơ bản", createBasicsPanel());
        tabbedPane.addTab("Cách di chuyển quân", createPiecesPanel());
        tabbedPane.addTab("Điều kiện Thắng/Thua", createWinLossPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Đã hiểu");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    //LUẬT CƠ BẢN ---
    private JPanel createBasicsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String content = "<html><body style='width: 380px; font-family: Arial; font-size: 13px;'>" +
                "<h2 style='color: #2980b9;'>1. Mục tiêu</h2>" +
                "<p>Mục tiêu tối thượng là <b>Chiếu hết (Checkmate)</b> Vua đối phương.</p>" +
                "<br>" +
                "<h2 style='color: #2980b9;'>2. Thiết lập</h2>" +
                "<ul>" +
                "<li>Bàn cờ 8x8, ô trắng đen xen kẽ.</li>" +
                "<li>Quân <b>Trắng</b> luôn đi trước.</li>" +
                "<li>Hai bên luân phiên đi từng nước một.</li>" +
                "</ul>" +
                "<br>" +
                "<h2 style='color: #2980b9;'>3. Nguyên tắc</h2>" +
                "<p>Không được đi vào ô có quân mình.</p>" +
                "<p>Đi vào ô có quân đối phương để <b>ăn quân</b> đó.</p>" +
                "</body></html>";

        JLabel label = new JLabel(content);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.add(new JScrollPane(label), BorderLayout.CENTER);
        return panel;
    }

    //CÁCH DI CHUYỂN QUÂN ---
    private JPanel createPiecesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(createPieceRow("♙", "Tốt (Pawn)", "Đi thẳng 1 ô (nước đầu được đi 2). Ăn chéo 1 ô."));
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Khoảng cách
        panel.add(createPieceRow("♖", "Xe (Rook)", "Đi theo hàng ngang hoặc cột dọc, không giới hạn số ô."));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createPieceRow("♘", "Mã (Knight)", "Đi theo hình chữ L (2 ô dọc + 1 ô ngang hoặc ngược lại). Là quân duy nhất nhảy qua đầu quân khác."));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createPieceRow("♗", "Tượng (Bishop)", "Đi theo đường chéo, không giới hạn số ô."));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createPieceRow("♕", "Hậu (Queen)", "Kết hợp sức mạnh của Xe và Tượng: Đi ngang, dọc, chéo tùy ý."));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createPieceRow("♔", "Vua (King)", "Đi từng ô một về mọi hướng (ngang, dọc, chéo). Là quân quan trọng nhất."));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Tăng tốc độ cuộn chuột
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane);
        return wrapper;
    }

    // Helper tạo dòng mô tả cho từng quân
    private JPanel createPieceRow(String symbol, String name, String desc) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));

        JLabel iconLabel = new JLabel(symbol);
        iconLabel.setFont(new Font("Serif", Font.PLAIN, 40)); // Font to cho icon
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String htmlDesc = "<html><body style='width: 300px'>" + 
                "<b>" + name + ":</b> " + desc + 
                "</body></html>";

		JLabel textLabel = new JLabel(htmlDesc);
		textLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        textLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        row.add(iconLabel, BorderLayout.WEST);
        row.add(textLabel, BorderLayout.CENTER);
        return row;
    }

    //THẮNG / THUA ---
    private JPanel createWinLossPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String content = "<html><body style='width: 380px; font-family: Arial; font-size: 13px;'>" +
                "<h2 style='color: #e74c3c;'>1. Chiếu (Check)</h2>" +
                "<p>Khi Vua bị tấn công. Bắt buộc phải chạy Vua hoặc đỡ đòn.</p>" +
                "<br>" +
                "<h2 style='color: #27ae60;'>2. Chiếu hết (Checkmate) - THẮNG</h2>" +
                "<p>Khi Vua bị Chiếu mà <b>không còn đường thoát</b>. Ván cờ kết thúc ngay lập tức.</p>" +
                "<br>" +
                "<h2 style='color: #f39c12;'>3. Hòa cờ (Draw)</h2>" +
                "<ul>" +
                "<li><b>Hết nước đi (Stalemate):</b> Vua không bị chiếu nhưng bên đi không còn nước hợp lệ.</li>" +
                "<li><b>Thỏa thuận:</b> Hai bên đồng ý bắt tay hòa.</li>" +
                "<li><b>Bất biến:</b> Thế cờ lặp lại 3 lần.</li>" +
                "</ul>" +
                "</body></html>";

        JLabel label = new JLabel(content);
        label.setBorder(new EmptyBorder(15, 20, 15, 20));
        label.setVerticalAlignment(SwingConstants.TOP);
        panel.add(new JScrollPane(label), BorderLayout.CENTER);
        return panel;
    }
}