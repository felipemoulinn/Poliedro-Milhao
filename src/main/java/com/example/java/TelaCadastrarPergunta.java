package com.example.java;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;


// Campo de texto arredondado
class CampoArredondado extends JTextField {
    public CampoArredondado(int tamanho) {
        super(tamanho);
        setOpaque(false);
        setFont(new Font("Arial", Font.PLAIN, 16));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setForeground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Sem borda
    }
}

// Botão arredondado
class BotaoArredondado extends JButton {
    public BotaoArredondado(String texto) {
        super(texto);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(219, 156, 38)); // #DB9C26
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Sem borda
    }
}

// ComboBox arredondado
class ComboBoxArredondado<E> extends JComboBox<E> {
    public ComboBoxArredondado(E[] itens) {
        super(itens);
        setUI(new BasicComboBoxUI());
        setFont(new Font("Arial", Font.PLAIN, 16));
        setForeground(Color.BLACK);
        setBackground(Color.WHITE);
        setOpaque(false);
        setFocusable(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g2);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // sem borda
    }
}

// Tela principal
public class TelaCadastrarPergunta extends JFrame {

    public TelaCadastrarPergunta() {
        setTitle("Editar Pergunta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setBackground(new Color(13, 11, 80)); // Azul escuro
        painelPrincipal.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label da matéria
        JLabel lblMateria = new JLabel("Selecione a matéria:");
        lblMateria.setForeground(Color.WHITE);
        lblMateria.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painelPrincipal.add(lblMateria, gbc);

        // ComboBox arredondado
        String[] materias = {"Biologia", "Matemática", "Português", "História", "Geografia"};
        ComboBoxArredondado<String> comboMateria = new ComboBoxArredondado<>(materias);
        comboMateria.setPreferredSize(new Dimension(300, 45));
        gbc.gridy = 1;
        painelPrincipal.add(comboMateria, gbc);

        // Label da pergunta
        JLabel lblPergunta = new JLabel("Insira a pergunta aqui:");
        lblPergunta.setForeground(Color.WHITE);
        lblPergunta.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 2;
        painelPrincipal.add(lblPergunta, gbc);

        // Campo de pergunta
        CampoArredondado campoPergunta = new CampoArredondado(30);
        campoPergunta.setPreferredSize(new Dimension(500, 50));
        gbc.gridy = 3;
        painelPrincipal.add(campoPergunta, gbc);

        // Label da resposta
        JLabel lblResposta = new JLabel("Insira a resposta aqui:");
        lblResposta.setForeground(Color.WHITE);
        lblResposta.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 4;
        painelPrincipal.add(lblResposta, gbc);

        // Campo de resposta
        CampoArredondado campoResposta = new CampoArredondado(30);
        campoResposta.setPreferredSize(new Dimension(500, 50));
        gbc.gridy = 5;
        painelPrincipal.add(campoResposta, gbc);

        // Botão ADICIONAR
        BotaoArredondado btnAdicionar = new BotaoArredondado("ADICIONAR");
        btnAdicionar.setPreferredSize(new Dimension(160, 45));
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        painelPrincipal.add(btnAdicionar, gbc);

        // Botão VOLTAR
        BotaoArredondado btnVoltar = new BotaoArredondado("VOLTAR");
        btnVoltar.setPreferredSize(new Dimension(160, 45));
        gbc.gridx = 1;
        painelPrincipal.add(btnVoltar, gbc);

        // Adiciona o painel
        add(painelPrincipal);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaCadastrarPergunta::new);
    }
}

