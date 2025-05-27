package com.example.java;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TelaParabens extends JFrame {

    public TelaParabens() {
        setTitle("Parabéns!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(13, 11, 80));

        // 🔝 TOPO COM ÍCONES
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        ImageIcon iconeConfigOriginal = new ImageIcon(getClass().getClassLoader().getResource("configuracoes.png"));

        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Image imgConfig = iconeConfigOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JButton btnConfig = new JButton(new ImageIcon(imgConfig));
        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));

        configurarBotaoIcone(btnConfig);
        configurarBotaoIcone(btnPerfil);
        aplicarHoverIconeSimples(btnConfig);
        aplicarHoverIconeSimples(btnPerfil);

        // ✅ Conectar botão de configuração à TelaSom
        btnConfig.addActionListener(e -> new TelaSom().setVisible(true));

        topPanel.add(btnConfig, BorderLayout.WEST);
        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 🎯 CENTRO
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(13, 11, 80));

        ImageIcon logoOriginal = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        Image imgLogo = logoOriginal.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(imgLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel textoLabel = new JLabel(
            "<html><div style='text-align: center;'>"
            + "<span style='font-size:24px; color:white;'>Parabéns! você acertou 10 de 12 perguntas<br>"
            + "e faturou a bagatela de R$ 1.000 policoins!</span>"
            + "</div></html>"
        );
        textoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(13, 11, 80));

        String[] botoes = { "RANKING", "MENU" };
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(btn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(logoLabel);
        centerPanel.add(textoLabel);
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    // 🔘 BOTÃO ESTILIZADO COM HOVER
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
                g2.drawString(getText(), x + 1, y + 1);
                g2.setColor(getForeground());
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(195, 141, 41));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                g2.dispose();
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(new Color(195, 141, 41));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        Dimension tamanho = new Dimension(280, 65);
        button.setPreferredSize(tamanho);
        button.setMinimumSize(tamanho);
        button.setMaximumSize(tamanho);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(corHover);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(corNormal);
            }
        });

        return button;
    }

    // 🧩 CONFIG DOS ÍCONES
    private void configurarBotaoIcone(JButton botao) {
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void aplicarHoverIconeSimples(JButton botao) {
        botao.setRolloverEnabled(true);
        botao.setBackground(new Color(255, 255, 255, 30));
        botao.setContentAreaFilled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaParabens::new);
    }
}
