package com.example.java;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TelaAdm extends JFrame {

    public TelaAdm() {
        setTitle("Poliedro Milhão - Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 14, 129));

        // TOPO COM ÍCONES
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        ImageIcon iconeConfigOriginal = new ImageIcon(getClass().getClassLoader().getResource("configuracoes.png"));

        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Image imgConfig = iconeConfigOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JButton btnConfig = new JButton(new ImageIcon(imgConfig));
        configurarBotaoIcone(btnConfig);
        aplicarHoverIconeSimples(btnConfig);
        btnConfig.addActionListener(e -> new TelaSom().setVisible(true));

        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));
        configurarBotaoIcone(btnPerfil);
        aplicarHoverIconeSimples(btnPerfil);

        topPanel.add(btnConfig, BorderLayout.WEST);
        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // LOGO
        ImageIcon logoOriginal = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        Image imgLogo = logoOriginal.getImage().getScaledInstance(260, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(imgLogo));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(logoLabel, BorderLayout.CENTER);

        // BOTÕES
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new BoxLayout(botoesPanel, BoxLayout.Y_AXIS));
        botoesPanel.setBackground(new Color(18, 14, 129));

        String[] botoes = { "JOGAR", "RANKING", "EDITAR", "CADASTRAR" };
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);

            switch (texto) {
                case "JOGAR" -> btn.addActionListener(e -> {
                    new TelaQuiz().setVisible(true);
                    dispose();
                });
                case "RANKING" -> btn.addActionListener(e -> {
                    new RankingScreen().setVisible(true);
                    dispose();
                });
                case "EDITAR" -> btn.addActionListener(e -> {
                    new TelaEditar().setVisible(true);
                });
                case "CADASTRAR" -> btn.addActionListener(e -> {
                    new TelaCadastro().setVisible(true);
                });
            }

            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            botoesPanel.add(btn);
            botoesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(18, 14, 129));
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));
        wrapper.add(botoesPanel);

        mainPanel.add(wrapper, BorderLayout.SOUTH);

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
                g2.drawString(getText(), x + 1, y + 1);
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

        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(corNormal);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(280, 65));
        button.setMinimumSize(new Dimension(280, 65));
        button.setMaximumSize(new Dimension(280, 65));
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
        SwingUtilities.invokeLater(TelaAdm::new);
    }
}
