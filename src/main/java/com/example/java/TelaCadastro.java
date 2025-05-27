package com.example.java;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class TelaCadastro extends JFrame {

    private JTextField emailField;
    private JPasswordField senhaField;
    private JTextField nomeField;
    private JComboBox<String> tipoCombo;

    public TelaCadastro() {
        setTitle("Cadastro");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(18, 14, 129));
        setLayout(new BorderLayout());

        // Painel wrapper centralizado
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(18, 14, 129));

        // Painel central com campos
        JPanel painelCentral = new JPanel();
        painelCentral.setBackground(new Color(18, 14, 129));
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("CADASTRO");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 42));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(titulo);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 40)));

        // Email
        JLabel emailLabel = new JLabel("Login (e-mail) ✉");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(emailLabel);

        emailField = new JTextField();
        estilizarCampoArredondado(emailField);
        painelCentral.add(emailField);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 10)));

        // Senha
        JLabel senhaLabel = new JLabel("Senha 🔒");
        senhaLabel.setForeground(Color.WHITE);
        senhaLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        senhaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(senhaLabel);

        senhaField = new JPasswordField();
        estilizarCampoArredondado(senhaField);
        painelCentral.add(senhaField);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 10)));

        // Nome
        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setForeground(Color.WHITE);
        nomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(nomeLabel);

        nomeField = new JTextField();
        estilizarCampoArredondado(nomeField);
        painelCentral.add(nomeField);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tipo
        JLabel tipoLabel = new JLabel("Tipo de Usuário:");
        tipoLabel.setForeground(Color.WHITE);
        tipoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        tipoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(tipoLabel);

        tipoCombo = new JComboBox<>(new String[]{"admin", "professor", "aluno"});
        tipoCombo.setMaximumSize(new Dimension(600, 40));
        painelCentral.add(tipoCombo);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        // Botão cadastrar
        JButton cadastrarBtn = criarBotaoArredondado("Cadastrar", new Color(220, 220, 220), Color.BLACK);
        cadastrarBtn.setMaximumSize(new Dimension(230, 50));
        cadastrarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(cadastrarBtn);

        wrapper.add(painelCentral);
        add(wrapper, BorderLayout.CENTER);

        // Rodapé
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setBackground(new Color(18, 14, 129));

        JButton voltarBtn = criarBotaoArredondado("VOLTAR", new Color(255, 153, 0), Color.WHITE);
        voltarBtn.setPreferredSize(new Dimension(130, 45));
        voltarBtn.addActionListener(e -> {
            new TelaLogin().setVisible(true);
            dispose();
        });

        rodape.add(voltarBtn);
        add(rodape, BorderLayout.SOUTH);

        cadastrarBtn.addActionListener(e -> cadastrarUsuario());

        setVisible(true);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaCadastro::new);
    }
}
