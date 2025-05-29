package com.example.java;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import java.io.*;

final public class TelaQuiz extends JFrame {
    JLabel lblPergunta;
    JLabel lblPontuacao;
    List<JButton> botoesResposta;
    private List<Questao> bancoQuestoes;
    private int questaoAtual = 0;
    private int pontos = 0;
    private int perguntasRespondidas = 0;
    private final int TOTAL_PERGUNTAS = 12;
    private String dificuldade;
    private ConexaoBD conexaoBD;
    private int usuarioId;

    // Cores
    private final Color COR_FUNDO = new Color(191, 148, 69);
    private final Color COR_TEXTO_PERGUNTA = new Color(50, 50, 50);
    private final Color COR_BOTAO_NORMAL = new Color(21, 42, 110);
    private final Color COR_BOTAO_HOVER = new Color(50, 80, 179);
    private final Color COR_BOTAO_CERTO = new Color(0, 150, 0);
    private final Color COR_BOTAO_ERRADO = new Color(150, 0, 0);
    private final Color COR_BORDA_BOTAO = new Color(0, 0, 0);
    private final Color COR_TEXTO_BOTAO = new Color(255, 255, 255);

    public TelaQuiz(String dificuldade, int usuarioId) {
        try {
            this.dificuldade = dificuldade;
            this.usuarioId = usuarioId;
            this.conexaoBD = new ConexaoBD();

            // Tentar carregar progresso salvo
            carregarProgresso();
            
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

            JPanel perguntaContainer = new JPanel(new BorderLayout());
            perguntaContainer.setBackground(COR_FUNDO);
            perguntaContainer.add(lblPergunta, BorderLayout.CENTER);

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

            // Botões auxiliares (modificado conforme solicitado)
            JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
            painelBotoes.setBackground(COR_FUNDO);

            JButton btnPular = criarBotaoAuxiliar("Pular");
            JButton btnDica = criarBotaoAuxiliar("Dica");  // Alterado de "Ajuda" para "Dica"
            JButton btnSair = criarBotaoAuxiliar("Sair");

            painelBotoes.add(btnPular);
            painelBotoes.add(btnDica);
            painelBotoes.add(btnSair);

            mainPanel.add(painelBotoes, BorderLayout.SOUTH);

            add(mainPanel);

            // Configura ações dos botões (modificado conforme solicitado)
            btnPular.addActionListener(e -> pularPergunta());
            btnDica.addActionListener(e -> mostrarDica());  // Método renomeado para mostrarDica()
            
            // Nova ação para o botão Sair
            btnSair.addActionListener(e -> {
                int opcao = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente sair do quiz?",
                    "Confirmar Saída",
                    JOptionPane.YES_NO_OPTION);
                
                if (opcao == JOptionPane.YES_OPTION) {
                    dispose();

                }
            });

            // Carrega as perguntas
            carregarPerguntasDoBanco();
            
            // Atualiza a exibição da pontuação inicial
            if (pontos > 0) {
                lblPontuacao.setText("Pontos: R$ " + formatarPontuacao(pontos));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Erro ao inicializar o quiz: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            dispose();
        }
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

        try (Connection conn = conexaoBD.obterConexao()) {
            // Busca perguntas aleatórias com suas respostas
            String sql = "SELECT p.id, p.enunciado, p.materia_id, p.ajuda, " +
                    "GROUP_CONCAT(r.texto ORDER BY RAND() SEPARATOR '||') as respostas, " +
                    "GROUP_CONCAT(r.correta ORDER BY RAND() SEPARATOR '||') as corretas " +
                    "FROM perguntas p " +
                    "JOIN respostas r ON p.id = r.pergunta_id " +
                    "WHERE p.nivel_dificuldade = ? " +
                    "GROUP BY p.id " +
                    "ORDER BY RAND()";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, dificuldade);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String enunciado = rs.getString("enunciado");
                    int materiaId = rs.getInt("materia_id");
                    String ajuda = rs.getString("ajuda");
                    String[] respostas = rs.getString("respostas").split("\\|\\|");
                    String[] corretas = rs.getString("corretas").split("\\|\\|");

                    // Encontra o índice da resposta correta
                    int respostaCorreta = -1;
                    for (int i = 0; i < corretas.length; i++) {
                        if (corretas[i].equals("1")) {
                            respostaCorreta = i;
                            break;
                        }
                    }

                    if (respostaCorreta != -1) {
                        Questao questao = new Questao(enunciado, Arrays.asList(respostas), respostaCorreta, materiaId);
                        questao.setAjuda(ajuda);
                        questao.setRespondida(false);
                        bancoQuestoes.add(questao);
                    }
                }
            }

            if (bancoQuestoes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma pergunta encontrada no banco de dados!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                criarBancoQuestoesPadrao();
            }
        } catch (Exception e) {
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

        System.out.println("Total de perguntas carregadas: " + bancoQuestoes.size());
        carregarQuestao(questaoAtual);
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
                1,
                1));

        // Adicione mais perguntas padrão aqui...
    }

    private void carregarQuestao(int indice) {
        if (indice >= 0 && indice < bancoQuestoes.size()) {
            Questao q = bancoQuestoes.get(indice);
            lblPergunta.setText("<html><div style='text-align: center;'>" + q.getPergunta() + "</div></html>");

            // Configura cada botão com sua respectiva resposta
            for (int i = 0; i < botoesResposta.size(); i++) {
                if (i < q.getRespostas().size()) {
                    botoesResposta.get(i).setText(q.getRespostas().get(i));
                    botoesResposta.get(i).setEnabled(true);
                    botoesResposta.get(i).setBackground(COR_BOTAO_NORMAL);
                }
            }
        }
    }

    private void verificarResposta(JButton btnSelecionado) {
        Questao q = bancoQuestoes.get(questaoAtual);
        
        // Se a questão já foi respondida, não permite responder novamente
        if (q.isRespondida()) {
            JOptionPane.showMessageDialog(this, 
                "Esta questão já foi respondida!", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int indiceRespostaCorreta = q.getRespostaCorreta();
        String respostaCorreta = q.getRespostas().get(indiceRespostaCorreta);

        // Desativa todos os botões
        for (JButton btn : botoesResposta) {
            btn.setEnabled(false);
            if (btn.getText().equals(respostaCorreta)) {
                btn.setBackground(COR_BOTAO_CERTO);
            } 
            if (btn == btnSelecionado && !btn.getText().equals(respostaCorreta)) {
                btn.setBackground(COR_BOTAO_ERRADO);
            }
        }

        Timer timer;
        if (btnSelecionado.getText().equals(respostaCorreta)) {
            // Marca a questão como respondida
            q.setRespondida(true);
            
            JOptionPane.showMessageDialog(this, 
                "Resposta correta! Continue assim!", 
                "Acertou!", 
                JOptionPane.INFORMATION_MESSAGE);

            int pontosGanhos = calcularPontos(perguntasRespondidas);
            pontos += pontosGanhos;
            lblPontuacao.setText("Pontos: R$ " + formatarPontuacao(pontos));
            perguntasRespondidas++;

            timer = new Timer(1000, e -> {
                if (perguntasRespondidas >= TOTAL_PERGUNTAS || questaoAtual >= bancoQuestoes.size() - 1) {
                    finalizarJogo(true);
                } else {
                    proximaPergunta();
                }
            });
        } else {
            q.setRespondida(true);
            JOptionPane.showMessageDialog(this, 
                "Que pena! A resposta correta era:\n" + respostaCorreta, 
                "Errou!", 
                JOptionPane.ERROR_MESSAGE);

            timer = new Timer(1000, e -> finalizarJogo(false));
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
        } else {
            finalizarJogo(true);
        }
    }

    private void finalizarJogo(boolean completouTodas) {
        String mensagem;
        if (completouTodas) {
            mensagem = String.format("PARABÉNS! Você completou o Show do Milhão!\n" +
                                   "Você respondeu todas as %d perguntas corretamente!\n" +
                                   "Pontuação final: R$ %s", 
                perguntasRespondidas, formatarPontuacao(pontos));
        } else {
            // Se errou, perguntasRespondidas já contém o número correto de acertos
            mensagem = String.format("Fim de jogo!\n" +
                                   "Você respondeu %d perguntas corretamente.\n" +
                                   "Pontuação final: R$ %s", 
                perguntasRespondidas, formatarPontuacao(pontos));
        }

        // Mostra a mensagem com a pontuação atual
        JOptionPane.showMessageDialog(this, mensagem, "Fim do Jogo", 
            completouTodas ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        // Apaga o arquivo de progresso ao finalizar
        File progressoFile = new File("progresso_" + usuarioId + "_" + dificuldade + ".txt");
        if (progressoFile.exists()) {
            progressoFile.delete();
        }

        // Salva a pontuação no banco
        salvarPontuacaoBanco();

        dispose();
        new RankingScreen(usuarioId).setVisible(true);
    }

    private int calcularPontos(int nivel) {
        // Pontuação base por nível de dificuldade
        switch (dificuldade.toLowerCase()) {
            case "facil" -> {
                return 1000;
            }
            case "medio" -> {
                return 2000;
            }
            case "dificil" -> {
                return 3000;
            }
            default -> {
                return 1000;
            }
        }
    }

    private String formatarPontuacao(int pontos) {
        if (pontos >= 1000000) {
            return String.format("%,.1f MI", pontos / 1000000.0);
        } else if (pontos >= 1000) {
            return String.format("%,.1f MIL", pontos / 1000.0);
        }
        return String.format("%,d", pontos);
    }

    private void salvarPontuacaoBanco() {
        try (Connection conn = conexaoBD.obterConexao()) {
            // Verificar se o usuário é um aluno
            String sqlVerificarTipo = "SELECT tipo FROM usuarios WHERE id = ?";
            try (PreparedStatement stmtTipo = conn.prepareStatement(sqlVerificarTipo)) {
                stmtTipo.setInt(1, usuarioId);
                ResultSet rs = stmtTipo.executeQuery();
                
                if (!rs.next() || !rs.getString("tipo").equals("aluno")) {
                    System.out.println("Pontuação não salva: usuário não é um aluno");
                    return;
                }
            }

            // Verificar pontuação atual do aluno
            String sqlSelect = "SELECT pontos FROM pontuacao WHERE aluno_id = ?";
            int pontuacaoAtual = 0;
            boolean pontuacaoExiste = false;
            
            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
                stmtSelect.setInt(1, usuarioId);
                ResultSet rs = stmtSelect.executeQuery();
                if (rs.next()) {
                    pontuacaoAtual = rs.getInt("pontos");
                    pontuacaoExiste = true;
                }
            }

            // Se a pontuação nova for maior, atualiza
            if (pontos > pontuacaoAtual) {
                String sql;
                if (pontuacaoExiste) {
                    sql = "UPDATE pontuacao SET pontos = ? WHERE aluno_id = ?";
                } else {
                    sql = "INSERT INTO pontuacao (pontos, aluno_id) VALUES (?, ?)";
                }

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, pontos);
                    stmt.setInt(2, usuarioId);
                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao salvar pontuação: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método renomeado de mostrarAjuda() para mostrarDica()
    private void mostrarDica() {
        if (questaoAtual >= 0 && questaoAtual < bancoQuestoes.size()) {
            Questao questaoAtual = bancoQuestoes.get(this.questaoAtual);
            String ajuda = questaoAtual.getAjuda();
            if (ajuda != null && !ajuda.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    ajuda,
                    "Dica",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Não há dica disponível para esta pergunta.",
                    "Dica",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void salvarProgresso() throws IOException {
        File progressoFile = new File("progresso_" + usuarioId + "_" + dificuldade + ".txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(progressoFile))) {
            // Salva o estado atual do jogo
            writer.println(questaoAtual + "," + pontos + "," + perguntasRespondidas);
            
            // Salva quais questões já foram respondidas
            StringBuilder questoesRespondidas = new StringBuilder();
            for (Questao q : bancoQuestoes) {
                questoesRespondidas.append(q.isRespondida() ? "1" : "0").append(",");
            }
            writer.println(questoesRespondidas.toString());

            JOptionPane.showMessageDialog(this,
                "Progresso salvo com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void carregarProgresso() {
        File progressoFile = new File("progresso_" + usuarioId + "_" + dificuldade + ".txt");
        if (progressoFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(progressoFile))) {
                // Carrega o estado do jogo
                String linha = reader.readLine();
                if (linha != null) {
                    String[] dados = linha.split(",");
                    if (dados.length == 3) {
                        this.questaoAtual = Integer.parseInt(dados[0]);
                        this.pontos = Integer.parseInt(dados[1]);
                        this.perguntasRespondidas = Integer.parseInt(dados[2]);
                    }
                }
                
                // Carrega o estado das questões respondidas
                linha = reader.readLine();
                if (linha != null) {
                    String[] questoesRespondidas = linha.split(",");
                    for (int i = 0; i < questoesRespondidas.length && i < bancoQuestoes.size(); i++) {
                        bancoQuestoes.get(i).setRespondida(questoesRespondidas[i].equals("1"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erro ao carregar progresso: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class Questao {
        private final String pergunta;
        private final List<String> respostas;
        private final int respostaCorreta;
        private final int materiaId;
        private String ajuda;
        private boolean respondida;

        public Questao(String pergunta, List<String> respostas, int respostaCorreta, int materiaId) {
            this.pergunta = pergunta;
            this.respostas = respostas;
            this.respostaCorreta = respostaCorreta;
            this.materiaId = materiaId;
            this.ajuda = "";
            this.respondida = false;
        }

        public String getPergunta() {
            return pergunta;
        }

        public List<String> getRespostas() {
            return respostas;
        }

        public int getRespostaCorreta() {
            return respostaCorreta + 1; // Incrementa o índice para corrigir a verificação
        }

        public int getMateriaId() {
            return materiaId;
        }

        public String getAjuda() {
            return ajuda;
        }

        public void setAjuda(String ajuda) {
            this.ajuda = ajuda;
        }

        public boolean isRespondida() {
            return respondida;
        }

        public void setRespondida(boolean respondida) {
            this.respondida = respondida;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                new TelaQuiz("medio", 1).setVisible(true);
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,
                        "Driver JDBC não encontrado!\nAdicione o conector MySQL ao seu projeto.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}