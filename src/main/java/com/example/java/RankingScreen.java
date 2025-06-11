package com.example.java;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class RankingScreen extends JFrame {
    private ConexaoBD conexaoBD;
    private int usuarioId;
    private BufferedImage backgroundImage;

    public RankingScreen(int usuarioId) {
        try {
            this.usuarioId = usuarioId;
            this.conexaoBD = new ConexaoBD();
            
            // Carrega a imagem de fundo
            try {
                backgroundImage = ImageIO.read(getClass().getResource("/bg5.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
                backgroundImage = null;
            }
            
            setTitle("Poliedro Milhão - Ranking");
            setSize(800, 900);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            // Painel principal com imagem de fundo
            JPanel mainPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (backgroundImage != null) {
                        // Redimensiona a imagem para cobrir todo o painel
                        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    }
                }
            };
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            JLabel titleLabel = new JLabel("RANKING TOP 10");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
            mainPanel.add(titleLabel);

            JPanel rankingPanel = new JPanel();
            rankingPanel.setLayout(new BoxLayout(rankingPanel, BoxLayout.Y_AXIS));
            rankingPanel.setOpaque(false);
            rankingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JScrollPane scrollPane = new JScrollPane(rankingPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setPreferredSize(new Dimension(700, 650));

            loadRankingData(rankingPanel);

            mainPanel.add(scrollPane);
            mainPanel.add(Box.createVerticalGlue());

            JButton backButton = createRoundedButton("Voltar", new Color(31, 176, 195));
            backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            backButton.addActionListener(e -> {
                // Verificar o tipo de usuário no banco de dados
                try (Connection conn = conexaoBD.obterConexao()) {
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
                                    new TelaDificuldade(tipoUsuario, usuarioId).setVisible(true);
                                    dispose();
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Erro: Usuário não encontrado", 
                                "Erro", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Erro ao verificar tipo de usuário: " + ex.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(backButton);

            add(mainPanel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao inicializar tela de ranking: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void loadRankingData(JPanel rankingPanel) throws Exception {
        try (Connection conn = conexaoBD.obterConexao()) {
            String sql = """
                SELECT u.nome, p.pontos 
                FROM usuarios u 
                INNER JOIN pontuacao p ON u.id = p.aluno_id 
                WHERE u.tipo = 'aluno' 
                ORDER BY p.pontos DESC 
                LIMIT 10
                """;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                int posicao = 1;
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    int pontos = rs.getInt("pontos");

                    rankingPanel.add(createRankingItem(posicao, nome, pontos));
                    rankingPanel.add(Box.createRigidArea(new Dimension(0, 30)));  // Aumentei o espaço entre itens
                    posicao++;
                }

                // Se não houver registros, mostrar mensagem
                if (posicao == 1) {
                    JLabel noDataLabel = new JLabel("Ainda não há pontuações registradas");
                    noDataLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    noDataLabel.setForeground(Color.WHITE);
                    noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    rankingPanel.add(noDataLabel);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar ranking: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

        private JPanel createRankingItem(int posicao, String nome, int pontuacao) {
            // Painel principal transparente
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setOpaque(false);
            itemPanel.setMaximumSize(new Dimension(650, 100));

            // Painel interno com fundo branco e borda arredondada
            JPanel innerPanel = new JPanel(new BorderLayout(20, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 50, 50);
                }
            };
            innerPanel.setOpaque(false); // Importante para o paintComponent funcionar
            innerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

            // Posição com emoji
            JLabel posLabel = new JLabel();
            switch (posicao) {
                case 1 -> posLabel.setText("🥇 1º");
                case 2 -> posLabel.setText("🥈 2º");
                case 3 -> posLabel.setText("🥉 3º");
                default -> posLabel.setText(posicao + "º");
            }
            posLabel.setFont(new Font("Arial", Font.BOLD, 28));
            posLabel.setForeground(Color.BLACK); // Garantir que o texto seja visível

            // Painel central com nome e pontuação
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);

            JLabel nomeLabel = new JLabel(nome);
            nomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
            nomeLabel.setForeground(Color.BLACK);
            nomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

            JLabel pontosLabel = new JLabel("R$ " + formatarPontuacao(pontuacao));
            pontosLabel.setFont(new Font("Arial", Font.BOLD, 22));
            pontosLabel.setForeground(new Color(0, 150, 0));

            centerPanel.add(nomeLabel);
            centerPanel.add(pontosLabel);

            innerPanel.add(posLabel, BorderLayout.WEST);
            innerPanel.add(centerPanel, BorderLayout.CENTER);

            itemPanel.add(innerPanel);
            return itemPanel;
        }

    private String formatarPontuacao(int pontos) {
        if (pontos >= 1_000_000) {
            return String.format("%,.1f MI", pontos / 1_000_000.0);
        } else if (pontos >= 1_000) {
            return String.format("%,.1f MIL", pontos / 1_000.0);
        }
        return String.format("%,d", pontos);
    }

    private JButton createRoundedButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 22f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(250, 60));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

        class RoundBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.setColor(new Color(200, 200, 200));  // Cor da borda mais suave
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius, this.radius, this.radius, this.radius);
        }
    }
}