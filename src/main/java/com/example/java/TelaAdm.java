package com.example.java;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TelaAdm extends JFrame {
    private int usuarioId;

    public TelaAdm(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Poliedro Milhão - Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        // Painel principal com imagem de fundo
        JPanel mainPanel = new ImageBackgroundPanel("bg.png");
        mainPanel.setLayout(new BorderLayout());

        // TOPO COM ÍCONES
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));

        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);


        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));
        configurarBotaoIcone(btnPerfil);
        aplicarHoverIconeSimples(btnPerfil);
        btnPerfil.addActionListener(e -> {
            TelaUsuario telaUsuario = new TelaUsuario(this, usuarioId);
            telaUsuario.setVisible(true);
        });

        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // LOGO CENTRAL
        ImageIcon logoOriginal = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        Image imgLogo = logoOriginal.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(imgLogo));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        logoLabel.setOpaque(false);
        mainPanel.add(logoLabel, BorderLayout.CENTER);

        // BOTÕES DO MENU
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new BoxLayout(botoesPanel, BoxLayout.Y_AXIS));
        botoesPanel.setOpaque(false);

        String[] botoes = { "RANKING", "EDITAR", "CADASTRAR", "EXCLUIR" };
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);

            switch (texto) {
                case "RANKING" -> btn.addActionListener(e -> {
                    new RankingScreen(usuarioId).setVisible(true);
                    dispose();
                });
                case "EDITAR" -> btn.addActionListener(e -> {
                    new TelaEditar(usuarioId).setVisible(true);
                    dispose();
                });
                case "CADASTRAR" -> btn.addActionListener(e -> {
                    new TelaCadastro(usuarioId).setVisible(true);
                    dispose();
                });
                case "EXCLUIR" -> btn.addActionListener(e -> {
                    new TelaExclusaoUsuario(usuarioId).setVisible(true);
                    dispose();
                });
            }

            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            botoesPanel.add(btn);
            botoesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));
        wrapper.add(botoesPanel);

        mainPanel.add(wrapper, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // Botão com imagem de fundo
    private JButton createMenuButton(String text) {
        ImageIcon iconOriginal = new ImageIcon(getClass().getClassLoader().getResource("botao1.png"));
        Image image = iconOriginal.getImage().getScaledInstance(280, 65, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(image);

        JButton button = new JButton(text, icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 65));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(50, 50, 50));
            }

            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.BLACK);
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
        botao.setContentAreaFilled(false);
    }

    // Painel de fundo com imagem
    class ImageBackgroundPanel extends JPanel {
        private Image backgroundImage;

        public ImageBackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(getClass().getClassLoader().getResource(imagePath)).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
