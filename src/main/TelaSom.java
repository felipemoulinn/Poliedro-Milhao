import java.awt.*;
import javax.swing.*;

public class TelaSom extends JFrame {
    
    public TelaSom() {
        setTitle("Configuração de Volume");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Painel principal com cor de fundo DB9C26
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(0xDB, 0x9C, 0x26)); // Cor DB9C26 (dourado)
        
        // Título
        JLabel titulo = new JLabel("CONFIGURAÇÃO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Color.BLACK); // Texto preto para melhor contraste
        mainPanel.add(titulo, BorderLayout.NORTH);
        
        // Painel do controle de volume
        JPanel volumePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        volumePanel.setBackground(new Color(0xDB, 0x9C, 0x26)); // Fundo DB9C26
        
        // Slider de volume
        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setBackground(new Color(0xDB, 0x9C, 0x26)); // Fundo DB9C26
        
        // Customização do slider
        volumeSlider.setForeground(new Color(0x64, 0x61, 0x61)); // Cor 646161 (cinza) para ticks e labels
        volumeSlider.setUI(new javax.swing.plaf.basic.BasicSliderUI(volumeSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0x64, 0x61, 0x61)); // Cor do indicador 646161
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
            
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0x64, 0x61, 0x61, 150)); // Cor da trilha com transparência
                g2d.fillRect(trackRect.x, trackRect.y + trackRect.height/2 - 2, 
                            trackRect.width, 4);
            }
        });
        
        // Label do volume
        JLabel volumeLabel = new JLabel("Volume: 50%", SwingConstants.CENTER);
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        volumeLabel.setForeground(Color.BLACK);
        
        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            volumeLabel.setText("Volume: " + value + "%");
            // Aqui você implementaria a mudança real do volume
            // Ex: AudioManager.setVolume(value);
        });
        
        volumePanel.add(volumeSlider);
        volumePanel.add(volumeLabel);
        mainPanel.add(volumePanel, BorderLayout.CENTER);
        
        // Botão voltar com cor 020047 (azul escuro)
        JButton voltarButton = new JButton("VOLTAR");
        voltarButton.setFont(new Font("Arial", Font.BOLD, 14));
        voltarButton.setBackground(new Color(0x02, 0x00, 0x47)); // Cor 020047
        voltarButton.setForeground(Color.WHITE); // Texto branco
        voltarButton.setFocusPainted(false);
        voltarButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        
        // Efeito hover no botão
        voltarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                voltarButton.setBackground(new Color(0x03, 0x02, 0x60)); // Cor mais clara
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                voltarButton.setBackground(new Color(0x02, 0x00, 0x47)); // Volta ao normal
            }
        });
        
        voltarButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(0xDB, 0x9C, 0x26)); // Fundo DB9C26
        buttonPanel.add(voltarButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaSom config = new TelaSom();
            config.setVisible(true);
        });
    }
}