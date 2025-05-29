package com.example.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaPerfil extends JDialog {

    public TelaPerfil(JFrame parent) {
        super(parent, "Perfil", true);
        setSize(300, 350);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(18, 14, 129)); // fundo azul
        setLayout(null);
        setUndecorated(true); // sem bordas

        // Avatar
        JLabel avatar = new JLabel(new ImageIcon("assets/perfil_icon.png"));
        avatar.setBounds(100, 20, 100, 100);
        add(avatar);

        // Nome
        JLabel nome = new JLabel("Gustavo Silva Bezerra", SwingConstants.CENTER);
        nome.setForeground(Color.WHITE);
        nome.setFont(new Font("Arial", Font.BOLD, 14));
        nome.setBounds(25, 130, 250, 20);
        add(nome);

        // Email
        JLabel email = new JLabel("GustavoSilva@aluno.com", SwingConstants.CENTER);
        email.setForeground(new Color(180, 180, 180));
        email.setFont(new Font("Arial", Font.PLAIN, 12));
        email.setBounds(25, 150, 250, 20);
        add(email);

        // Botão SAIR
        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Arial", Font.BOLD, 14));
        btnSair.setBackground(new Color(220, 220, 220));
        btnSair.setBounds(55, 200, 190, 40);
        btnSair.setFocusPainted(false);
        btnSair.setBorderPainted(false);
        btnSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSair.setIcon(new ImageIcon("assets/logout_icon.png"));
        btnSair.setHorizontalTextPosition(SwingConstants.LEFT);
        btnSair.setIconTextGap(60);
        btnSair.addActionListener(e -> System.exit(0));
        add(btnSair);

        // Botão VOLTAR
        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setBounds(110, 270, 80, 25);
        btnVoltar.setBackground(Color.WHITE);
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 10));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> dispose());
        add(btnVoltar);
    }

    // MAIN para teste isolado
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPerfil perfil = new TelaPerfil(null); // sem JFrame de fundo
            perfil.setVisible(true);
            System.exit(0); // fecha app após fechar a janela
        });
    }
}
