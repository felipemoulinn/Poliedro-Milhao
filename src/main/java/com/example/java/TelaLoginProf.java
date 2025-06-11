package com.example.java;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TelaLoginProf extends JFrame {
    private int usuarioId;
    private BufferedImage backgroundImage;
    private BufferedImage buttonImage;

    public TelaLoginProf(int usuarioId) {
        this.usuarioId = usuarioId;
        
        setTitle("Poliedro Milhão - Professor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/bg3.png"));
            buttonImage = ImageIO.read(getClass().getResource("/botao1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage finalBackgroundImage = backgroundImage;
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBackgroundImage != null) {
                    g.drawImage(finalBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setOpaque(false);

        // TOPO COM ÍCONES
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));
        configurarBotaoIcone(btnPerfil);

        btnPerfil.addActionListener(e -> {
            TelaUsuario telaUsuario = new TelaUsuario(this, usuarioId);
            telaUsuario.setVisible(true);
        });

        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // LOGO CENTRALIZADO
        ImageIcon logoOriginal = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        Image imgLogo = logoOriginal.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(imgLogo));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        logoLabel.setOpaque(false);
        
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(logoLabel);
        mainPanel.add(logoPanel, BorderLayout.CENTER);

        // BOTÕES CENTRALIZADOS
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new BoxLayout(botoesPanel, BoxLayout.Y_AXIS));
        botoesPanel.setOpaque(false);

        String[] botoes = { "RANKING", "EDITAR" };
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            switch (texto) {
                case "RANKING" -> btn.addActionListener(e -> {
                    new RankingScreen(usuarioId).setVisible(true);
                    dispose();
                });
                case "EDITAR" -> btn.addActionListener(e -> {
                    new TelaEditar(usuarioId).setVisible(true);
                    dispose();
                });
            }

            botoesPanel.add(btn);
            botoesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JPanel botoesWrapper = new JPanel(new GridBagLayout());
        botoesWrapper.setOpaque(false);
        botoesWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));
        botoesWrapper.add(botoesPanel);

        mainPanel.add(botoesWrapper, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void configurarBotaoIcone(JButton botao) {
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JButton createMenuButton(String texto) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                if (buttonImage != null) {
                    // Desenha a imagem do botão redimensionada
                    g.drawImage(buttonImage, 0, 0, getWidth(), getHeight(), this);
                }
                
                // Desenha o texto do botão
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2.setColor(Color.BLACK);
                g2.drawString(getText(), x + 1, y + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        botao.setFont(new Font("Arial", Font.BOLD, 24));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setPreferredSize(new Dimension(280, 65));
        botao.setMinimumSize(new Dimension(280, 65));
        botao.setMaximumSize(new Dimension(280, 65));
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botao.setForeground(new Color(255, 200, 70));
            }

            public void mouseExited(MouseEvent evt) {
                botao.setForeground(Color.WHITE);
            }
        });

        return botao;
    }
}