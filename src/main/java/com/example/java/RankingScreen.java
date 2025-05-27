package com.example.java;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class RankingScreen extends JFrame {
   private static final String DB_URL = "jdbc:mysql://localhost:3306/poliedro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
   private static final String DB_USER = "usuario";
   private static final String DB_PASS = "senha123"; // NOVA SENHA

    public RankingScreen() {
        setTitle("Poliedro Milhão - Ranking");
        setSize(800, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(0xA4A4A4));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("RANKING TOP 10");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
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
            new TelaLoginProf().setVisible(true); // Ajuste conforme sua lógica
            dispose();
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(backButton);

        add(mainPanel);
    }

    private void loadRankingData(JPanel rankingPanel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT u.nome_usuario, p.pontos " +
                         "FROM pontuacoes p " +
                         "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                         "ORDER BY p.pontos DESC LIMIT 10";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();

                int posicao = 1;
                while (rs.next()) {
                    String nome = rs.getString("nome_usuario");
                    int pontos = rs.getInt("pontos");

                    rankingPanel.add(createRankingItem(posicao, nome, null, pontos));
                    rankingPanel.add(Box.createRigidArea(new Dimension(0, 20)));

                    posicao++;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar ranking: " + e.getMessage());

            for (int i = 1; i <= 10; i++) {
                rankingPanel.add(createRankingItem(i, "Jogador " + i, null, 2000000 / i));
                rankingPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        }
    }

    private JPanel createRankingItem(int posicao, String nome, String fotoPath, int pontuacao) {
        JPanel itemPanel = new JPanel(new BorderLayout(20, 0));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(new RoundBorder(50, Color.WHITE));
        itemPanel.setMaximumSize(new Dimension(650, 80));

        JLabel posLabel = new JLabel();
        switch (posicao) {
            case 1 -> posLabel.setIcon(new ImageIcon("gold.png"));
            case 2 -> posLabel.setIcon(new ImageIcon("silver.png"));
            case 3 -> posLabel.setIcon(new ImageIcon("bronze.png"));
            default -> posLabel.setText(posicao + "°");
        }

        posLabel.setFont(new Font("Arial", Font.BOLD, 28));
        posLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        posLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel fotoLabel;
        if (fotoPath != null && !fotoPath.isEmpty()) {
            fotoLabel = new JLabel(new ImageIcon(new ImageIcon(fotoPath).getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } else {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/user.png"));
            fotoLabel = new JLabel(new ImageIcon(icon.getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        }

        fotoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);

        JLabel nomeLabel = new JLabel(nome);
        nomeLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel pontosLabel = new JLabel("R$ " + formatarPontuacao(pontuacao));
        pontosLabel.setFont(new Font("Arial", Font.BOLD, 20));
        pontosLabel.setForeground(new Color(0, 150, 0));

        infoPanel.add(nomeLabel, BorderLayout.NORTH);
        infoPanel.add(pontosLabel, BorderLayout.SOUTH);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(posLabel);
        leftPanel.add(fotoLabel);

        itemPanel.add(leftPanel, BorderLayout.WEST);
        itemPanel.add(infoPanel, BorderLayout.CENTER);

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
            g2.setColor(new Color(100, 100, 100));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                new RankingScreen().setVisible(true);
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Driver JDBC não encontrado!");
            }
        });
    }
}
