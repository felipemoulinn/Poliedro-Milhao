package com.example.java;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;
import java.sql.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class TelaEditar extends JFrame {
    private int usuarioId;
    private BufferedImage buttonImage;

    public TelaEditar(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Editar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        // Carrega a imagem do botão
        try {
            buttonImage = ImageIO.read(getClass().getResource("/botao1.png"));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar imagem do botão", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            private Image backgroundImage;
            {
                // Carrega a imagem no bloco de inicialização
                try {
                    InputStream imgStream = getClass().getResourceAsStream("/bg2.png");
                    if (imgStream != null) {
                        backgroundImage = ImageIO.read(imgStream);
                    } else {
                        JOptionPane.showMessageDialog(this, "Não foi possível carregar a imagem", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao ler a imagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Desenha a imagem dimensionada para o tamanho do painel
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        // Configura todos os painéis internos como transparentes
        mainPanel.setOpaque(false);

        // No seu rodapé, altere para:
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setOpaque(false);  // Isso é crucial para ver o fundo

        // ÍCONES TOPO
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));
        configurarBotaoIcone(btnPerfil);
        aplicarHoverIconeSimples(btnPerfil);

        // Adicionar ação ao botão de perfil
        btnPerfil.addActionListener(e -> {
            TelaUsuario telaUsuario = new TelaUsuario(this, usuarioId);
            telaUsuario.setVisible(true);
        });

        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // PAINEL CENTRAL COM TÍTULO + BOTÕES
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
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
        centerWrapper.setOpaque(false);
        centerWrapper.add(centerPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // BOTÃO VOLTAR
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

    // BOTÃO ESTILIZADO COM A IMAGEM
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
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

        button.setFont(new Font("Arial", Font.BOLD, 25));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(255, 200, 70));
            }

            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
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