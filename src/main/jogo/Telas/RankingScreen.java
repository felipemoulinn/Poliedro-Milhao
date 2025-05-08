import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.border.*;

public class RankingScreen extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/showdomilhao";
    private static final String DB_USER = "usuario";
    private static final String DB_PASS = "senha";

    public RankingScreen() {
        // Configuração da janela
        setTitle("Show do Milhão - Ranking");
        setSize(800, 900); // Aumentei o tamanho para caber 10 jogadores
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(0xA4A4A4));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Título
        JLabel titleLabel = new JLabel("RANKING TOP 10");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        mainPanel.add(titleLabel);

        // Painel de rankings com scroll
        JPanel rankingPanel = new JPanel();
        rankingPanel.setLayout(new BoxLayout(rankingPanel, BoxLayout.Y_AXIS));
        rankingPanel.setOpaque(false);
        rankingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(rankingPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(700, 650));

        // Carregar dados do ranking
        loadRankingData(rankingPanel);

        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalGlue());

        // Botão Voltar
        JButton backButton = createRoundedButton("Voltar", new Color(31, 176, 195));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> dispose());

        mainPanel.add(backButton);
        add(mainPanel);
    }

    private void loadRankingData(JPanel rankingPanel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT u.nome, p.pontos " +
                         "FROM ponttuacao p " +  // Corrigido para o nome correto da tabela
                         "JOIN usuarios u ON p.aluno_id = u.id " +
                         "ORDER BY p.pontos DESC LIMIT 10";  // Busca os 10 melhores

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                
                int posicao = 1;
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    int pontos = rs.getInt("pontos");
                    
                    rankingPanel.add(createRankingItem(posicao, nome, null, pontos));
                    rankingPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                    
                    posicao++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar ranking: " + e.getMessage());
            
            // Dados de exemplo com 10 jogadores
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
        
        // Posição com medalha para os 3 primeiros
        JLabel posLabel = new JLabel();
        if (posicao == 1) {
            posLabel.setIcon(new ImageIcon("gold.png")); // Substitua pelo caminho da imagem
            posLabel.setText("");
        } else if (posicao == 2) {
            posLabel.setIcon(new ImageIcon("silver.png")); // Substitua pelo caminho da imagem
            posLabel.setText("");
        } else if (posicao == 3) {
            posLabel.setIcon(new ImageIcon("bronze.png")); // Substitua pelo caminho da imagem
            posLabel.setText("");
        } else {
            posLabel.setText(posicao + "°");
        }
        posLabel.setFont(new Font("Arial", Font.BOLD, 28));
        posLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        posLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Foto do usuário
        JLabel fotoLabel;
        if (fotoPath != null && !fotoPath.isEmpty()) {
            fotoLabel = new JLabel(new ImageIcon(new ImageIcon(fotoPath).getImage()
                .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } else {
            // Placeholder padrão
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/user.png"));
            if (icon != null) {
                fotoLabel = new JLabel(new ImageIcon(icon.getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
            } else {
                fotoLabel = new JLabel("Foto");
                fotoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            }
        }
        fotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Nome e pontuação
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
        
        // Adiciona componentes ao item
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(posLabel);
        leftPanel.add(fotoLabel);
        
        itemPanel.add(leftPanel, BorderLayout.WEST);
        itemPanel.add(infoPanel, BorderLayout.CENTER);
        
        return itemPanel;
    }

    private String formatarPontuacao(int pontos) {
        if (pontos >= 1000000) {
            return String.format("%,.1f MI", pontos / 1000000.0);
        } else if (pontos >= 1000) {
            return String.format("%,.1f MIL", pontos / 1000.0);
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

    // Borda arredondada personalizada
    class RoundBorder extends AbstractBorder {
        private int radius;
        private Color color;
        
        public RoundBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, width-1, height-1, radius, radius);
            g2.setColor(new Color(100, 100, 100));
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                RankingScreen ranking = new RankingScreen();
                ranking.setVisible(true);
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, 
                    "Driver JDBC não encontrado!\nAdicione o conector MySQL ao seu projeto.");
            }
        });
    }
}