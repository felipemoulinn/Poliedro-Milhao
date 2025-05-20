package src.main;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaSom extends JFrame {

    public TelaSom() {
        setTitle("Configuração de Volume");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(0xDB9C26));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Painel do ícone e slider
        JPanel volumePanel = new JPanel();
        volumePanel.setBackground(new Color(0xDB9C26));
        volumePanel.setLayout(new BoxLayout(volumePanel, BoxLayout.X_AXIS));
        volumePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ícone de volume
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/som.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            volumePanel.add(iconLabel);
            volumePanel.add(Box.createRigidArea(new Dimension(15, 0)));
        } catch (Exception e) {
            JLabel somIcone = new JLabel("🔊");
            somIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            volumePanel.add(somIcone);
        }

        // Slider personalizado
        JSlider volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setPreferredSize(new Dimension(200, 40));
        volumeSlider.setMaximumSize(new Dimension(250, 40));
        volumeSlider.setOpaque(false);
        
        // Classe CustomSliderUI interna
        volumeSlider.setUI(new BasicSliderUI(volumeSlider) {
            private static final Color TRACK_COLOR = new Color(255, 255, 255);
            private static final Color FILLED_TRACK_COLOR = new Color(6, 0, 173);
            private static final Color THUMB_COLOR = new Color(0x020047);

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(THUMB_COLOR);
                g2.fillOval(thumbRect.x, thumbRect.y + thumbRect.height/2 - 8, 16, 16);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Track vazia
                g2.setColor(TRACK_COLOR);
                g2.fillRoundRect(trackRect.x, trackRect.y + trackRect.height/2 - 3,
                        trackRect.width, 6, 6, 6);

                // Track preenchida
                g2.setColor(FILLED_TRACK_COLOR);
                g2.fillRoundRect(trackRect.x, trackRect.y + trackRect.height/2 - 3,
                        thumbRect.x - trackRect.x + 8, 6, 6, 6);
            }

            @Override
            protected Dimension getThumbSize() {
                return new Dimension(16, 16);
            }

            @Override
            public void paintFocus(Graphics g) {
                // Remove a borda de foco
            }
        });
        
        volumeSlider.setFocusable(false);

        volumePanel.add(volumeSlider);
        mainPanel.add(volumePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Botão Voltar
        JButton voltarBtn = new JButton("VOLTAR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0x020047).darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0x020047).brighter());
                } else {
                    g2.setColor(new Color(0x020047));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                super.paintComponent(g);
            }
        };
        
        voltarBtn.setForeground(Color.WHITE);
        voltarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        voltarBtn.setFocusPainted(false);
        voltarBtn.setPreferredSize(new Dimension(150, 45));
        voltarBtn.setMaximumSize(new Dimension(150, 45));
        voltarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        voltarBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        voltarBtn.setContentAreaFilled(false);
        voltarBtn.setOpaque(false);
        
        voltarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        mainPanel.add(voltarBtn);
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TelaSom().setVisible(true);
        });
    }
}