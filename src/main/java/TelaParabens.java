package src.main;

import java.awt.*;
import javax.swing.*;

public class TelaParabens extends JFrame {
    public TelaParabens() {
        setTitle("Parabéns!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Tamanho padrão caso não maximize
        setLocationRelativeTo(null);
        
        // Configura para abrir maximizada
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Permite redimensionamento e minimização
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(13, 11, 80));

        // Texto com HTML para melhor quebra de linha
        String textoParabens = "<html><div style='text-align: center;'><div> - <div><div>Parabéns!<div> Você acertou 10 de 12 perguntas e faturou a bagatela de R$ 1.000 policoins!</div></html>";
        JLabel parabensLabel = new JLabel(textoParabens);
        parabensLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        parabensLabel.setForeground(Color.WHITE);
        parabensLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 20, 40, 20); // Margens laterais
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(parabensLabel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(13, 11, 80));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        String[] botoes = {"Menu", "Ranking"};
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(btn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Espaço maior entre botões
        }

        gbc.gridy = 1;
        gbc.weighty = 1.5;
        gbc.insets = new Insets(0, 20, 20, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        Color corNormal = new Color(195, 141, 41);
        Color corHover = new Color(255, 200, 70);

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2.setColor(Color.BLACK);
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i != 0 || j != 0) {
                            g2.drawString(getText(), x + i, y + j);
                        }
                    }
                }

                g2.setColor(getForeground());
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(corNormal);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                g2.dispose();
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 25));
        button.setBackground(corNormal);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        
        // Tamanhos ajustáveis
        button.setPreferredSize(new Dimension(300, 70));
        button.setMinimumSize(new Dimension(250, 60));
        button.setMaximumSize(new Dimension(350, 80));
        
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(corHover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(corNormal);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaParabens());
    }
}