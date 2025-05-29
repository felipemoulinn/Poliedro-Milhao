package com.example.java;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class TelaExclusaoUsuario extends JFrame {

    private JTextField emailField;
    private int usuarioId;

    public TelaExclusaoUsuario(int usuarioId) {
        this.usuarioId = usuarioId;

        setTitle("Exclusão de Usuário");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(18, 14, 129));
        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(18, 14, 129));

        JPanel painelCentral = new JPanel();
        painelCentral.setBackground(new Color(18, 14, 129));
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("EXCLUIR USUÁRIO");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 42));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(titulo);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 40)));

        JLabel emailLabel = new JLabel("Login (e-mail) ✉");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(emailLabel);

        emailField = new JTextField();
        estilizarCampoArredondado(emailField);
        painelCentral.add(emailField);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton excluirBtn = criarBotaoArredondado("Excluir", new Color(255, 50, 50), Color.WHITE);
        excluirBtn.setMaximumSize(new Dimension(230, 50));
        excluirBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(excluirBtn);

        wrapper.add(painelCentral);
        add(wrapper, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setBackground(new Color(18, 14, 129));

        JButton voltarBtn = criarBotaoArredondado("VOLTAR", new Color(255, 153, 0), Color.WHITE);
        voltarBtn.setPreferredSize(new Dimension(130, 45));
        voltarBtn.addActionListener(e -> {
            new TelaAdm(usuarioId).setVisible(true);
            dispose();
        });

        rodape.add(voltarBtn);
        add(rodape, BorderLayout.SOUTH);

        excluirBtn.addActionListener(e -> excluirUsuario());

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

    private void excluirUsuario() {
        String email = emailField.getText();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o e-mail do usuário a ser excluído.");
            return;
        }

        try {
            ConexaoBD conexaoBD = new ConexaoBD();
            Connection conn = conexaoBD.obterConexao();

            // Buscar o ID do usuário com base no e-mail
            String selectSql = "SELECT id FROM usuarios WHERE email = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setString(1, email);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int usuarioId = rs.getInt("id");

                // Deleta da tabela pontuacao primeiro
                PreparedStatement deletePontuacao = conn.prepareStatement("DELETE FROM pontuacao WHERE aluno_id = ?");
                deletePontuacao.setInt(1, usuarioId);
                deletePontuacao.executeUpdate();
                deletePontuacao.close();

                // Agora deleta o usuário
                PreparedStatement deleteUsuario = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?");
                deleteUsuario.setInt(1, usuarioId);
                int linhasAfetadas = deleteUsuario.executeUpdate();
                deleteUsuario.close();

                if (linhasAfetadas > 0) {
                    JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
                    emailField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir usuário.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
            }

            rs.close();
            selectStmt.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
        }
    }
}
