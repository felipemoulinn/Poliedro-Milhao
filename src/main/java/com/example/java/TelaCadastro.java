package com.example.java;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class TelaCadastro extends JFrame {
    private JTextField emailField;
    private JPasswordField senhaField;
    private JTextField nomeField;
    private JComboBox<String> tipoCombo;
    private int usuarioId;
    private BufferedImage backgroundImage;

    public TelaCadastro(int usuarioId) {
        this.usuarioId = usuarioId;
        
        // Carrega a imagem de fundo
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/bg3.png"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        setTitle("Cadastro");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Painel principal com imagem de fundo
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback se a imagem não carregar
                    g.setColor(new Color(18, 14, 129));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel central com conteúdo
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        JLabel titulo = new JLabel("CADASTRO");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 42));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        centerPanel.add(titulo);

        // Campos do formulário
        adicionarCampo(centerPanel, "Login (e-mail) ✉", emailField = new JTextField());
        adicionarCampo(centerPanel, "Senha 🔒", senhaField = new JPasswordField());
        adicionarCampo(centerPanel, "Nome:", nomeField = new JTextField());

        // Combobox de tipo de usuário
        JLabel tipoLabel = new JLabel("Tipo de Usuário:");
        tipoLabel.setForeground(Color.WHITE);
        tipoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        tipoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(tipoLabel);

        tipoCombo = new JComboBox<>(new String[]{"admin", "professor", "aluno"});
        estilizarComboBox(tipoCombo);
        tipoCombo.setMaximumSize(new Dimension(600, 40));
        tipoCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(tipoCombo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Botão Cadastrar
        JButton cadastrarBtn = criarBotaoArredondado("Cadastrar", new Color(220, 220, 220), Color.BLACK);
        cadastrarBtn.setMaximumSize(new Dimension(230, 50));
        cadastrarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cadastrarBtn.addActionListener(e -> cadastrarUsuario());
        centerPanel.add(cadastrarBtn);

        // Centraliza o painel de conteúdo
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(centerPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // Rodapé com botão Voltar
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setOpaque(false);
        
        JButton voltarBtn = criarBotaoArredondado("VOLTAR", new Color(255, 153, 0), Color.WHITE);
        voltarBtn.setPreferredSize(new Dimension(130, 45));
        voltarBtn.addActionListener(e -> {
            new TelaAdm(usuarioId).setVisible(true);
            dispose();
        });
        
        rodape.add(voltarBtn);
        mainPanel.add(rodape, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void adicionarCampo(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        estilizarCampoArredondado(field);
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void estilizarCampoArredondado(JTextField campo) {
        Dimension campoSize = new Dimension(600, 55);

        campo.setMaximumSize(campoSize);
        campo.setPreferredSize(campoSize);
        campo.setMinimumSize(campoSize);

        campo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        campo.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintSafely(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, campo.getWidth(), campo.getHeight(), 40, 40);
                super.paintSafely(g);
            }
        });
    }

    private void estilizarComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setContentAreaFilled(false);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });
    }

    private JButton criarBotaoArredondado(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        return btn;
    }

    private void cadastrarUsuario() {
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());
        String nome = nomeField.getText();
        String tipo = (String) tipoCombo.getSelectedItem();

        if (email.isEmpty() || senha.isEmpty() || nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try {
            ConexaoBD conexaoBD = new ConexaoBD();
            Connection conn = conexaoBD.obterConexao();

            String sql = "INSERT INTO usuarios (email, senha, nome, tipo) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, senha);
            stmt.setString(3, nome);
            stmt.setString(4, tipo);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            emailField.setText("");
            senhaField.setText("");
            nomeField.setText("");
            tipoCombo.setSelectedIndex(0);

            stmt.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + ex.getMessage());
        }
    }
}