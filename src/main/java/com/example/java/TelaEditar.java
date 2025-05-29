package com.example.java;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class TelaEditar extends JFrame {
    private int usuarioId;

    public TelaEditar(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Editar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 14, 129));

        // 🔝 ÍCONES TOPO
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        ImageIcon iconeConfigOriginal = new ImageIcon(getClass().getClassLoader().getResource("configuracoes.png"));

        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Image imgConfig = iconeConfigOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

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

        // 🧱 PAINEL CENTRAL COM TÍTULO + BOTÕES
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(18, 14, 129));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel tituloLabel = new JLabel("EDITAR PERGUNTAS");
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 145, 0));
        centerPanel.add(tituloLabel);

        String[] botoes = { "Adicionar perguntas", "Excluir perguntas" };
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            btn.setPreferredSize(new Dimension(300, 70));
            btn.setMinimumSize(new Dimension(300, 70));
            btn.setMaximumSize(new Dimension(300, 70));

            if (texto.equals("Adicionar perguntas")) {
                btn.addActionListener(e -> {
                    new TelaCadastrarPergunta(usuarioId).setVisible(true);
                    dispose();
                });
            } else if (texto.equals("Excluir perguntas")) {
                btn.addActionListener(e -> {
                    new TelaExcluir(usuarioId).setVisible(true);
                    dispose();
                });
            }

            centerPanel.add(btn);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(18, 14, 129));
        centerWrapper.add(centerPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // ⬇ BOTÃO VOLTAR
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setBackground(new Color(18, 14, 129));

        JButton voltarBtn = createMenuButton("VOLTAR");
        voltarBtn.setPreferredSize(new Dimension(130, 45));

        voltarBtn.addActionListener(e -> {
            try (Connection conn = new ConexaoBD().obterConexao()) {
                String sql = "SELECT tipo FROM usuarios WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, usuarioId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String tipoUsuario = rs.getString("tipo");
                        switch (tipoUsuario) {
                            case "professor" -> {
                                new TelaLoginProf(usuarioId).setVisible(true);
                                dispose();
                            }
                            case "admin" -> {
                                new TelaAdm(usuarioId).setVisible(true);
                                dispose();
                            }
                            default -> {
                                JOptionPane.showMessageDialog(this,
                                    "Tipo de usuário não autorizado",
                                    "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erro ao verificar tipo de usuário: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        rodape.add(voltarBtn);
        mainPanel.add(rodape, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 🔘 BOTÃO ESTILIZADO
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

        button.setFont(new Font("Arial", Font.BOLD, 25));
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
        botao.setContentAreaFilled(false);
    }
}