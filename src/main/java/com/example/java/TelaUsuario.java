package com.example.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class TelaUsuario extends JDialog {

    public TelaUsuario(JFrame parent) {
        super(parent, "Perfil", true);
        setSize(300, 350);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(255, 255, 255));
        setLayout(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        // Avatar com verificação de imagem
        URL perfilIconURL = getClass().getClassLoader().getResource("perfil2.png");
        System.out.println("URL do perfil2.png = " + perfilIconURL);

        ImageIcon iconePerfil = perfilIconURL != null ? new ImageIcon(perfilIconURL) : null;
        JLabel avatar = new JLabel(iconePerfil);
        avatar.setBounds(100, 20, 100, 100);
        add(avatar);

        // Nome
        JLabel nome = new JLabel("Gustavo Silva Bezerra");
        nome.setForeground(Color.WHITE);
        nome.setFont(new Font("Arial", Font.BOLD, 14));
        nome.setBounds(0, 130, 300, 20);
        nome.setHorizontalAlignment(SwingConstants.CENTER);
        add(nome);

        // Email
        JLabel email = new JLabel("GustavoSilva@aluno.com");
        email.setForeground(new Color(180, 180, 180));
        email.setFont(new Font("Arial", Font.PLAIN, 12));
        email.setBounds(0, 150, 300, 20);
        email.setHorizontalAlignment(SwingConstants.CENTER);
        add(email);

        // Botão SAIR com texto centralizado e ícone à direita
        RoundedButton btnSair = new RoundedButton("", 20);
        btnSair.setBounds(55, 200, 190, 40);
        btnSair.setBackground(new Color(220, 220, 220));
        btnSair.setLayout(new BorderLayout());

        JLabel textoSair = new JLabel("Sair");
        textoSair.setHorizontalAlignment(SwingConstants.CENTER);
        textoSair.setFont(new Font("Arial", Font.BOLD, 14));
        textoSair.setForeground(Color.BLACK);

        URL sairIconURL = getClass().getClassLoader().getResource("sair.png");
        System.out.println("URL do sair.png = " + sairIconURL);

        JLabel iconeSair = sairIconURL != null ? new JLabel(new ImageIcon(sairIconURL)) : new JLabel();
        iconeSair.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        btnSair.add(textoSair, BorderLayout.CENTER);
        btnSair.add(iconeSair, BorderLayout.EAST);
        btnSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSair.setFocusPainted(false);

        // ✅ Ao clicar em SAIR, fecha tudo e vai para TelaLogin
        btnSair.addActionListener(e -> {
            dispose(); // Fecha este dialog
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow != null) {
                parentWindow.dispose(); // Fecha a tela que chamou
            }
            new TelaLogin().setVisible(true); // Abre a tela de login
        });

        add(btnSair);

        // Botão VOLTAR no canto inferior direito
        RoundedButton btnVoltar = new RoundedButton("VOLTAR", 10);
        btnVoltar.setBounds(200, 310, 80, 25);
        btnVoltar.setBackground(Color.WHITE);
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 10));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> dispose());
        add(btnVoltar);
    }

    // Botão com visual arredondado real
    static class RoundedButton extends JButton {
        private final int radius;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        public void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public boolean isContentAreaFilled() {
            return false;
        }
    }

    // Main para testes
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaUsuario usuario = new TelaUsuario(null);
            usuario.setVisible(true);
        });
    }
}
