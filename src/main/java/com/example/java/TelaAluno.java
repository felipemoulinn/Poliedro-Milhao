package com.example.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaAluno extends JFrame {
    private int usuarioId;

    public TelaAluno(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Poliedro Milhão - Aluno");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 14, 129));

        // TOPO COM ÍCONES
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon iconePerfil = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        ImageIcon iconeConfig = new ImageIcon(getClass().getClassLoader().getResource("configuracoes.png"));

        Image imgPerfil = iconePerfil.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Image imgConfig = iconeConfig.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));
        JButton btnConfig = new JButton(new ImageIcon(imgConfig));

        configurarBotaoIcone(btnPerfil);
        configurarBotaoIcone(btnConfig);
        aplicarHoverIconeSimples(btnPerfil);
        aplicarHoverIconeSimples(btnConfig);

        btnConfig.addActionListener(e -> new TelaSom().setVisible(true));

        // Adicionar ação ao botão de perfil
        btnPerfil.addActionListener(e -> {
            TelaUsuario telaUsuario = new TelaUsuario(this, usuarioId);
            telaUsuario.setVisible(true);
        });

        topPanel.add(btnConfig, BorderLayout.WEST);
        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // CENTRO COM LOGO E BOTÃO
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(18, 14, 129));

        // Logo
        ImageIcon logoIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        Image imgLogo = logoIcon.getImage().getScaledInstance(260, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(imgLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        // Botão JOGAR
        JButton btnJogar = createMenuButton("JOGAR");
        btnJogar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnJogar.setPreferredSize(new Dimension(280, 65));
        btnJogar.setMaximumSize(new Dimension(280, 65));
        btnJogar.setMinimumSize(new Dimension(280, 65));

        btnJogar.addActionListener(e -> {
            new TelaDificuldade("aluno", usuarioId).setVisible(true);
            dispose();
        });

        // Montar layout
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(100)); // espaçamento extra entre logo e botão
        centerPanel.add(btnJogar);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

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
}