import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.sql.*;
import java.util.*;
import java.util.List;

public class TelaQuiz extends JFrame {
    private JLabel lblPergunta;
    private JLabel lblPontuacao;
    private JLabel lblImagem;
    private List<JButton> botoesResposta;
    private List<Questao> bancoQuestoes;
    private int questaoAtual = 0;
    private int pontos = 0;
    private int perguntasRespondidas = 0;
    private final int TOTAL_PERGUNTAS = 12;

    // Cores
    private final Color COR_FUNDO = new Color(191, 148, 69);
    private final Color COR_TEXTO_PERGUNTA = new Color(50, 50, 50);
    private final Color COR_BOTAO_NORMAL = new Color(21, 42, 110);
    private final Color COR_BOTAO_HOVER = new Color(50, 80, 179);
    private final Color COR_BOTAO_CERTO = new Color(0, 150, 0);
    private final Color COR_BOTAO_ERRADO = new Color(150, 0, 0);
    private final Color COR_BORDA_BOTAO = new Color(0, 0, 0);
    private final Color COR_TEXTO_BOTAO = new Color(255, 255, 255);

    // Configurações do banco
    private static final String DB_URL = "jdbc:mysql://localhost:3306/showdomilhao";
    private static final String DB_USER = "usuario";
    private static final String DB_PASS = "senha";

    public TelaQuiz() {
        // Configurações da janela
        setTitle("Show do Milhão");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBackground(COR_FUNDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel superior com pontuação
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COR_FUNDO);

        lblPontuacao = new JLabel("Pontos: R$ 0", SwingConstants.RIGHT);
        lblPontuacao.setFont(new Font("Arial", Font.BOLD, 24));
        lblPontuacao.setForeground(Color.WHITE);
        topPanel.add(lblPontuacao, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Painel da pergunta
        JPanel painelPergunta = new JPanel(new BorderLayout());
        painelPergunta.setBackground(COR_FUNDO);

        lblPergunta = new JLabel("", SwingConstants.CENTER);
        lblPergunta.setFont(new Font("Arial", Font.BOLD, 32));
        lblPergunta.setForeground(COR_TEXTO_PERGUNTA);
        lblPergunta.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        lblImagem = new JLabel();
        lblImagem.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagem.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel perguntaContainer = new JPanel(new BorderLayout());
        perguntaContainer.setBackground(COR_FUNDO);
        perguntaContainer.add(lblPergunta, BorderLayout.CENTER);
        perguntaContainer.add(lblImagem, BorderLayout.SOUTH);

        painelPergunta.add(perguntaContainer, BorderLayout.CENTER);
        mainPanel.add(painelPergunta, BorderLayout.NORTH);

        // Painel das respostas
        JPanel painelRespostas = new JPanel(new GridLayout(2, 2, 20, 20));
        painelRespostas.setBackground(COR_FUNDO);
        painelRespostas.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));

        botoesResposta = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            JButton btn = criarBotaoResposta();
            botoesResposta.add(btn);
            painelRespostas.add(btn);
        }

        mainPanel.add(painelRespostas, BorderLayout.CENTER);

        // Botões auxiliares
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        painelBotoes.setBackground(COR_FUNDO);

        JButton btnPular = criarBotaoAuxiliar("Pular");
        JButton btnSair = criarBotaoAuxiliar("Sair");

        painelBotoes.add(btnPular);
        painelBotoes.add(btnSair);

        mainPanel.add(painelBotoes, BorderLayout.SOUTH);

        add(mainPanel);

        // Configura ações dos botões
        btnPular.addActionListener(e -> pularPergunta());
        btnSair.addActionListener(e -> finalizarJogo());

        // Carrega as perguntas
        carregarPerguntasDoBanco();
    }

    private JButton criarBotaoResposta() {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(COR_BOTAO_HOVER);
                } else {
                    g2.setColor(COR_BOTAO_NORMAL);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.setColor(COR_BORDA_BOTAO);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2.setColor(COR_TEXTO_BOTAO);
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();

                // Quebra o texto em linhas
                String[] lines = text.split("\n");
                int y = ((getHeight() - (fm.getHeight() * lines.length)) / 2 + fm.getAscent());

                for (String line : lines) {
                    int x = (getWidth() - fm.stringWidth(line)) / 2;
                    g2.drawString(line, x, y);
                    y += fm.getHeight();
                }

                g2.dispose();
            }
        };

        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setForeground(COR_TEXTO_BOTAO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(400, 120));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.repaint();
            }

            public void mouseExited(MouseEvent evt) {
                btn.repaint();
            }
        });

        btn.addActionListener(e -> verificarResposta((JButton) e.getSource()));
        return btn;
    }

    private JButton criarBotaoAuxiliar(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(100, 100, 100));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(150, 150, 150));
                } else {
                    g2.setColor(new Color(120, 120, 120));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);

                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(150, 50));

        return btn;
    }

    private void carregarPerguntasDoBanco() {
        bancoQuestoes = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Busca perguntas aleatórias com suas respostas
            String sql = "SELECT p.id, p.enunciado, p.materia_id, " +
                    "GROUP_CONCAT(r.texto ORDER BY RAND() SEPARATOR '||') as respostas, " +
                    "GROUP_CONCAT(r.correta ORDER BY r.correta DESC SEPARATOR '||') as corretas " +
                    "FROM perguntas p " +
                    "JOIN respostas r ON p.id = r.pergunta_id " +
                    "GROUP BY p.id " +
                    "ORDER BY RAND() LIMIT ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, TOTAL_PERGUNTAS);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String enunciado = rs.getString("enunciado");
                    String[] respostas = rs.getString("respostas").split("\\|\\|");
                    String[] corretas = rs.getString("corretas").split("\\|\\|");

                    int respostaCorreta = -1;
                    for (int i = 0; i < corretas.length; i++) {
                        if (corretas[i].equals("1")) {
                            respostaCorreta = i;
                            break;
                        }
                    }

                    if (respostaCorreta != -1) {
                        bancoQuestoes.add(new Questao(enunciado, Arrays.asList(respostas), respostaCorreta));
                    }
                }
            }

            if (bancoQuestoes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma pergunta encontrada no banco de dados!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                criarBancoQuestoesPadrao();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar perguntas: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
            criarBancoQuestoesPadrao();
        }

        if (bancoQuestoes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Não foi possível carregar nenhuma pergunta. O jogo será encerrado.", 
                "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        carregarQuestao(0);
        carregarImagemAleatoria();
    }

    private void criarBancoQuestoesPadrao() {
        List<String> respostas1 = Arrays.asList(
                "A - Raiz de 16 é 3",
                "B - A guerra fria foi uma guerra não armada\nonde a luta era pelo conhecimento\ne conquista do espaço",
                "C - A variação linguística é uma expressão\nque se refere às diversas maneiras\npelas quais a língua se manifesta",
                "D - 23");

        bancoQuestoes.add(new Questao(
                "Qual questão está relacionada com a matéria de história?",
                respostas1,
                1));

        // Adicione mais perguntas padrão aqui...
    }

    private void carregarQuestao(int indice) {
        if (indice >= 0 && indice < bancoQuestoes.size()) {
            Questao q = bancoQuestoes.get(indice);
            lblPergunta.setText("<html><div style='text-align: center;'>" + q.getPergunta() + "</div></html>");

            for (int i = 0; i < botoesResposta.size(); i++) {
                botoesResposta.get(i).setText(q.getRespostas().get(i));
                botoesResposta.get(i).setEnabled(true);
                botoesResposta.get(i).setBackground(COR_BOTAO_NORMAL);
            }
        }
    }

    private void carregarImagemAleatoria() {
        try {
            // Busca imagem aleatória do banco
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "SELECT caminho_imagem FROM imagens WHERE materia_id = " +
                        "(SELECT materia_id FROM perguntas WHERE id = " +
                        "(SELECT pergunta_id FROM respostas WHERE pergunta_id = perguntas.id LIMIT 1)) " +
                        "ORDER BY RAND() LIMIT 1";

                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {

                    if (rs.next()) {
                        String caminho = rs.getString("caminho_imagem");
                        ImageIcon icon = new ImageIcon(caminho);
                        Image img = icon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
                        lblImagem.setIcon(new ImageIcon(img));
                    } else {
                        lblImagem.setIcon(null);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + e.getMessage());
            lblImagem.setIcon(null);
        }
    }

    private void verificarResposta(JButton btnSelecionado) {
        Questao q = bancoQuestoes.get(questaoAtual);
        String respostaSelecionada = btnSelecionado.getText();
        String respostaCorreta = q.getRespostas().get(q.getRespostaCorreta());

        // Desativa todos os botões
        for (JButton btn : botoesResposta) {
            btn.setEnabled(false);
            if (btn.getText().equals(respostaCorreta)) {
                btn.setBackground(COR_BOTAO_CERTO);
            } else if (btn == btnSelecionado && !respostaSelecionada.equals(respostaCorreta)) {
                btn.setBackground(COR_BOTAO_ERRADO);
            }
        }

        Timer timer;
        if (respostaSelecionada.equals(respostaCorreta)) {
            pontos += calcularPontos(perguntasRespondidas);
            perguntasRespondidas++;
            lblPontuacao.setText("Pontos: R$ " + formatarPontuacao(pontos));

            timer = new Timer(2000, e -> {
                if (perguntasRespondidas >= TOTAL_PERGUNTAS) {
                    finalizarJogo(true);
                } else {
                    proximaPergunta();
                }
            });
        } else {
            timer = new Timer(2000, e -> finalizarJogo(false));
        }
        
        timer.setRepeats(false);
        timer.start();
    }

    private void pularPergunta() {
        if (questaoAtual < bancoQuestoes.size() - 1) {
            proximaPergunta();
        } else {
            JOptionPane.showMessageDialog(this, "Não há mais perguntas para pular!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void proximaPergunta() {
        questaoAtual++;
        if (questaoAtual < bancoQuestoes.size()) {
            carregarQuestao(questaoAtual);
            carregarImagemAleatoria();
        } else {
            finalizarJogo(true);
        }
    }

    private void finalizarJogo() {
        finalizarJogo(false);
    }

    private void finalizarJogo(boolean completouTodas) {
        salvarPontuacao();

        if (completouTodas) {
            JOptionPane.showMessageDialog(this,
                    "Parabéns! Você completou todas as perguntas!\nPontuação final: R$ " + formatarPontuacao(pontos),
                    "Fim do Jogo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Fim do jogo! Sua pontuação foi: R$ " + formatarPontuacao(pontos),
                    "Fim do Jogo", JOptionPane.INFORMATION_MESSAGE);
        }

        dispose();
        new RankingScreen().setVisible(true);
    }

    private int calcularPontos(int nivel) {
        // Pontuação crescente: 1000 para a primeira, 2000 para a segunda, etc.
        return (nivel + 1) * 1000;
    }

    private String formatarPontuacao(int pontos) {
        if (pontos >= 1000000) {
            return String.format("%,.1f MI", pontos / 1000000.0);
        } else if (pontos >= 1000) {
            return String.format("%,.1f MIL", pontos / 1000.0);
        }
        return String.format("%,d", pontos);
    }

    private void salvarPontuacao() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Obtém o ID do usuário logado (você precisará implementar isso)
            int alunoId = obterIdUsuarioLogado();

            if (alunoId > 0) {
                String sql = "INSERT INTO ponttuacao (aluno_id, pontos, ultima_atualizacao) " +
                        "VALUES (?, ?, NOW()) " +
                        "ON DUPLICATE KEY UPDATE pontos = GREATEST(pontos, VALUES(pontos)), " +
                        "ultima_atualizacao = NOW()";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, alunoId);
                    stmt.setInt(2, pontos);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar pontuação: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int obterIdUsuarioLogado() {
        // Implemente a lógica para obter o ID do usuário logado
        // Por exemplo, você pode armazenar isso quando o usuário faz login
        return 1; // Temporário - substitua pela implementação real
    }

    class Questao {
        private String pergunta;
        private List<String> respostas;
        private int respostaCorreta;

        public Questao(String pergunta, List<String> respostas, int respostaCorreta) {
            this.pergunta = pergunta;
            this.respostas = respostas;
            this.respostaCorreta = respostaCorreta;
        }

        public String getPergunta() {
            return pergunta;
        }

        public List<String> getRespostas() {
            return respostas;
        }

        public int getRespostaCorreta() {
            return respostaCorreta;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                new TelaQuiz().setVisible(true);
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, 
                    "Driver JDBC não encontrado!\nAdicione o conector MySQL ao seu projeto.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}