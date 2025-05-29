package com.example.java;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import java.io.*;
import java.util.stream.Collectors;

final public class TelaQuiz extends JFrame {
    JLabel lblPergunta;
    JLabel lblPontuacao;
    List<JButton> botoesResposta;
    private List<Integer> perguntasRespondidasIds = new ArrayList<>();
    private List<Questao> bancoQuestoes;
    private int questaoAtual = 0;
    private int pontos = 0;
    private int perguntasRespondidas = 0;
    private static final int TOTAL_PERGUNTAS = 5;
    private String dificuldade;
    private ConexaoBD conexaoBD;
    private int usuarioId;
    private boolean dicaUsada = false;  // Nova variável para controlar uso da dica

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
            this.bancoQuestoes = new ArrayList<>(); // Initialize bancoQuestoes

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

            // Botões auxiliares
            JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
            painelBotoes.setBackground(COR_FUNDO);

            JButton btnPular = criarBotaoAuxiliar("Pular");
            JButton btnDica = criarBotaoAuxiliar("Dica");
            JButton btnSair = criarBotaoAuxiliar("Sair");
            JButton btnSalvarSair = criarBotaoAuxiliar("Salvar e Sair");

            painelBotoes.add(btnPular);
            painelBotoes.add(btnDica);
            painelBotoes.add(btnSair);
            painelBotoes.add(btnSalvarSair);

            mainPanel.add(painelBotoes, BorderLayout.SOUTH);

            add(mainPanel);

            // Configura ações dos botões
            btnPular.addActionListener(e -> pularPergunta());
            btnDica.addActionListener(e -> mostrarDica());
            btnSair.addActionListener(e -> {
                int opcao = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente sair do quiz?\nSeu progresso será perdido.",
                    "Confirmar Saída",
                    JOptionPane.YES_NO_OPTION);
                
                if (opcao == JOptionPane.YES_OPTION) {
                    try (Connection conn = conexaoBD.obterConexao()) {
                        // Verifica o tipo do usuário
                        String sql = "SELECT tipo FROM usuarios WHERE id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setInt(1, usuarioId);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                String tipoUsuario = rs.getString("tipo");
                                dispose();
                                new TelaDificuldade(tipoUsuario, usuarioId).setVisible(true);
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                            "Erro ao retornar à tela de dificuldade: " + e1.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            btnSalvarSair.addActionListener(e -> salvarESair());

            // Carrega o progresso primeiro para saber quais perguntas já foram respondidas
            carregarProgresso();
            // Depois carrega as perguntas, considerando o progresso
            carregarPerguntasDoBanco();
            
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
        System.out.println("Iniciando carregamento de perguntas para dificuldade: " + dificuldade);

        try (Connection conn = conexaoBD.obterConexao()) {
            // Primeiro, vamos contar quantas perguntas existem no total para esta dificuldade
            String sqlCount = "SELECT COUNT(*) as total FROM perguntas WHERE nivel_dificuldade = ?";
            int totalPerguntasDisponiveis = 0;
            
            try (PreparedStatement stmtCount = conn.prepareStatement(sqlCount)) {
                stmtCount.setString(1, dificuldade.toLowerCase());
                ResultSet rsCount = stmtCount.executeQuery();
                if (rsCount.next()) {
                    totalPerguntasDisponiveis = rsCount.getInt("total");
                }
            }
            
            System.out.println("Total de perguntas disponíveis no banco: " + totalPerguntasDisponiveis);

            // Primeiro, carregamos todas as perguntas já respondidas
            if (!perguntasRespondidasIds.isEmpty()) {
                String sqlRespondidas = """
                    SELECT p.id, p.enunciado, p.materia_id, p.ajuda, p.nivel_dificuldade
                    FROM perguntas p
                    WHERE p.id IN (%s)
                """;
                String placeholders = perguntasRespondidasIds.stream()
                    .map(id -> "?")
                    .collect(Collectors.joining(","));
                sqlRespondidas = String.format(sqlRespondidas, placeholders);

                try (PreparedStatement stmtRespondidas = conn.prepareStatement(sqlRespondidas)) {
                    for (int i = 0; i < perguntasRespondidasIds.size(); i++) {
                        stmtRespondidas.setInt(i + 1, perguntasRespondidasIds.get(i));
                    }
                    ResultSet rsRespondidas = stmtRespondidas.executeQuery();

                    while (rsRespondidas.next()) {
                        carregarQuestaoDoResultSet(rsRespondidas, true);
                    }
                }
            }

            // Depois, carregamos as perguntas não respondidas
            String sql = """
                SELECT p.id, p.enunciado, p.materia_id, p.ajuda, p.nivel_dificuldade
                FROM perguntas p
                WHERE p.nivel_dificuldade = ? AND p.id NOT IN (%s)
                ORDER BY RAND()
            """;

            String placeholders = perguntasRespondidasIds.isEmpty() ? "0" : 
                perguntasRespondidasIds.stream()
                    .map(id -> "?")
                    .collect(Collectors.joining(","));

            sql = String.format(sql, placeholders);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, dificuldade.toLowerCase());
                for (int i = 0; i < perguntasRespondidasIds.size(); i++) {
                    stmt.setInt(i + 2, perguntasRespondidasIds.get(i));
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    carregarQuestaoDoResultSet(rs, false);
                }
            }

            System.out.println("Total de questões carregadas: " + bancoQuestoes.size());
            System.out.println("Total de questões já respondidas: " + perguntasRespondidas);
            System.out.println("Total de questões disponíveis: " + (totalPerguntasDisponiveis - perguntasRespondidas));

            if (bancoQuestoes.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Nenhuma pergunta encontrada para o nível " + dificuldade, 
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
                criarBancoQuestoesPadrao();
            } else {
                // Procura a primeira questão não respondida
                for (int i = 0; i < bancoQuestoes.size(); i++) {
                    if (!bancoQuestoes.get(i).isRespondida()) {
                        questaoAtual = i;
                        break;
                    }
                }
                carregarQuestao(questaoAtual);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar perguntas: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            criarBancoQuestoesPadrao();
        }
    }

    private void carregarQuestaoDoResultSet(ResultSet rs, boolean respondida) throws Exception {
        int perguntaId = rs.getInt("id");
        String enunciado = rs.getString("enunciado");
        System.out.println("Carregando pergunta ID: " + perguntaId + ", Enunciado: " + enunciado);

        List<String> respostas = new ArrayList<>();
        int respostaCorreta = -1;

        try {
            String sqlRespostas = "SELECT texto, correta FROM respostas WHERE pergunta_id = ?";
            try (Connection conn = conexaoBD.obterConexao();
                 PreparedStatement stmtRespostas = conn.prepareStatement(sqlRespostas)) {
                stmtRespostas.setInt(1, perguntaId);
                ResultSet rsRespostas = stmtRespostas.executeQuery();

                int index = 0;
                while (rsRespostas.next()) {
                    String texto = rsRespostas.getString("texto");
                    boolean correta = rsRespostas.getBoolean("correta");
                    respostas.add(texto);
                    if (correta) {
                        respostaCorreta = index;
                    }
                    index++;
                }
            }

            if (respostaCorreta != -1 && !respostas.isEmpty()) {
                Questao questao = new Questao(
                    enunciado,
                    respostas,
                    respostaCorreta,
                    perguntaId,
                    rs.getInt("materia_id")
                );
                questao.setAjuda(rs.getString("ajuda"));
                questao.setRespondida(respondida);
                bancoQuestoes.add(questao);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar respostas para pergunta " + perguntaId + ": " + e.getMessage());
            throw e;
        }
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
            1,
            1
        ));

        // Adicione mais perguntas padrão aqui...
    }

    private void carregarQuestao(int indice) {
        System.out.println("Tentando carregar questão índice: " + indice);
        if (indice >= 0 && indice < bancoQuestoes.size()) {
            Questao q = bancoQuestoes.get(indice);
            
            // Se a questão já foi respondida, procura a próxima não respondida
            if (q.isRespondida()) {
                int proximaQuestao = indice + 1;
                while (proximaQuestao < bancoQuestoes.size()) {
                    if (!bancoQuestoes.get(proximaQuestao).isRespondida()) {
                        questaoAtual = proximaQuestao;
                        carregarQuestao(proximaQuestao);
                        return;
                    }
                    proximaQuestao++;
                }
                // Se não encontrou nenhuma questão não respondida, finaliza o jogo
                finalizarJogo(true);
                return;
            }
            
            System.out.println("Carregando questão: " + q.getPergunta());
            
            // Configura o enunciado
            lblPergunta.setText("<html><div style='text-align: center;'>" + q.getPergunta() + "</div></html>");
            
            // Embaralha as respostas
            List<String> respostasEmbaralhadas = new ArrayList<>(q.getRespostas());
            Collections.shuffle(respostasEmbaralhadas);
            System.out.println("Respostas embaralhadas: " + respostasEmbaralhadas);
            
            // Configura os botões
            for (int i = 0; i < botoesResposta.size(); i++) {
                if (i < respostasEmbaralhadas.size()) {
                    JButton btn = botoesResposta.get(i);
                    btn.setText(respostasEmbaralhadas.get(i));
                    btn.setEnabled(true);
                    btn.setBackground(COR_BOTAO_NORMAL);
                    System.out.println("Botão " + i + " configurado com: " + respostasEmbaralhadas.get(i));
                }
            }
        } else {
            System.out.println("ERRO: Índice inválido para carregar questão: " + indice);
            // Se o índice é inválido, tenta encontrar uma questão não respondida
            for (int i = 0; i < bancoQuestoes.size(); i++) {
                if (!bancoQuestoes.get(i).isRespondida()) {
                    questaoAtual = i;
                    carregarQuestao(i);
                    return;
                }
            }
            // Se não encontrou nenhuma questão não respondida, finaliza o jogo
            finalizarJogo(true);
        }
    }

    private void verificarResposta(JButton btnSelecionado) {
        if (questaoAtual >= bancoQuestoes.size()) return;
        
        Questao q = bancoQuestoes.get(questaoAtual);
        if (q.isRespondida()) {
            JOptionPane.showMessageDialog(this, 
                "Esta questão já foi respondida!", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Desativa todos os botões temporariamente
        botoesResposta.forEach(btn -> btn.setEnabled(false));

        String respostaCorreta = q.getRespostas().get(q.getRespostaCorreta());
        boolean acertou = btnSelecionado.getText().equals(respostaCorreta);

        // Marca visualmente a resposta certa e errada
        for (JButton btn : botoesResposta) {
            if (btn.getText().equals(respostaCorreta)) {
                btn.setBackground(COR_BOTAO_CERTO);
            } else if (btn == btnSelecionado && !acertou) {
                btn.setBackground(COR_BOTAO_ERRADO);
            }
        }

        // Marca a questão como respondida
        q.setRespondida(true);
        perguntasRespondidas++;
        perguntasRespondidasIds.add(q.getId()); // Adiciona o ID da pergunta respondida

        if (acertou) {
            // Atualiza pontuação (1000 pontos por acerto, multiplicado pelo fator de dificuldade)
            int pontosGanhos = 1000;
            switch (dificuldade.toLowerCase()) {
                case "facil" -> pontosGanhos *= 1;
                case "medio" -> pontosGanhos *= 2;
                case "dificil" -> pontosGanhos *= 3;
            }
            
            pontos += pontosGanhos;
            lblPontuacao.setText("Pontos: R$ " + formatarPontuacao(pontos));

            // Salva a pontuação no banco imediatamente após cada acerto
            salvarPontuacaoBanco();
            // Salva o progresso atual
            salvarProgresso();

            JOptionPane.showMessageDialog(this, 
                "Resposta correta! Continue assim!\nPontos ganhos: R$ " + formatarPontuacao(pontosGanhos), 
                "Acertou!", 
                JOptionPane.INFORMATION_MESSAGE);

            Timer timer = new Timer(1500, e -> {
                if (perguntasRespondidas >= TOTAL_PERGUNTAS) {
                    finalizarJogo(true);
                } else {
                    proximaPergunta();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // Mesmo perdendo, salvamos o progresso para marcar a questão como respondida
            salvarProgresso();
            
            JOptionPane.showMessageDialog(this, 
                "Que pena! A resposta correta era:\n" + respostaCorreta, 
                "Errou!", 
                JOptionPane.ERROR_MESSAGE);
            
            Timer timer = new Timer(1500, e -> finalizarJogo(false));
            timer.setRepeats(false);
            timer.start();
        }
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
            // Se a próxima questão já foi respondida, procura a próxima não respondida
            if (bancoQuestoes.get(questaoAtual).isRespondida()) {
                int proximaQuestao = questaoAtual;
                while (proximaQuestao < bancoQuestoes.size()) {
                    if (!bancoQuestoes.get(proximaQuestao).isRespondida()) {
                        questaoAtual = proximaQuestao;
                        break;
                    }
                    proximaQuestao++;
                }
                // Se não encontrou nenhuma questão não respondida após a atual, finaliza o jogo
                if (proximaQuestao >= bancoQuestoes.size()) {
                    finalizarJogo(true);
                    return;
                }
            }
            carregarQuestao(questaoAtual);
            // Reativa os botões para a próxima pergunta
            botoesResposta.forEach(btn -> {
                btn.setEnabled(true);
                btn.setBackground(COR_BOTAO_NORMAL);
            });
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

        try (Connection conn = conexaoBD.obterConexao()) {
            // Busca o tipo do usuário
            String sql = "SELECT tipo FROM usuarios WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String tipoUsuario = rs.getString("tipo");
                    dispose();
                    new TelaDificuldade(tipoUsuario, usuarioId).setVisible(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erro ao retornar à tela de dificuldade: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calcularPontos(int nivel) {
        // Pontuação fixa de 1000 por acerto, multiplicada pelo fator de dificuldade
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
                String sql = pontuacaoExiste ?
                    "UPDATE pontuacao SET pontos = ? WHERE aluno_id = ?" :
                    "INSERT INTO pontuacao (pontos, aluno_id) VALUES (?, ?)";

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

    private void mostrarDica() {
        if (dicaUsada) {
            JOptionPane.showMessageDialog(this,
                "Você já usou sua dica nesta partida!",
                "Dica Indisponível",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (questaoAtual >= 0 && questaoAtual < bancoQuestoes.size()) {
            Questao questaoAtual = bancoQuestoes.get(this.questaoAtual);
            String ajuda = questaoAtual.getAjuda();
            if (ajuda != null && !ajuda.trim().isEmpty()) {
                dicaUsada = true;  // Marca a dica como usada
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

    private void salvarProgresso() {
        try {
            File progressoFile = new File("progresso_" + usuarioId + "_" + dificuldade + ".txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(progressoFile))) {
                // Linha 1: Estado atual do jogo
                writer.println(questaoAtual + "," + pontos + "," + perguntasRespondidas + "," + dicaUsada);
                
                // Linha 2: Estado de cada questão (respondida ou não)
                StringBuilder questoesRespondidas = new StringBuilder();
                for (Questao q : bancoQuestoes) {
                    questoesRespondidas.append(q.isRespondida() ? "1" : "0").append(",");
                }
                writer.println(questoesRespondidas.toString());

                // Linha 3: IDs das perguntas
                StringBuilder questoesIds = new StringBuilder();
                for (Questao q : bancoQuestoes) {
                    questoesIds.append(q.getId()).append(",");
                }
                writer.println(questoesIds.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar progresso: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
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
                    if (dados.length >= 4) {
                        this.questaoAtual = Integer.parseInt(dados[0]);
                        this.pontos = Integer.parseInt(dados[1]);
                        this.perguntasRespondidas = Integer.parseInt(dados[2]);
                        this.dicaUsada = Boolean.parseBoolean(dados[3]);
                    }
                }
                
                // Carrega os IDs das questões respondidas
                linha = reader.readLine();
                String[] questoesRespondidas = null;
                if (linha != null) {
                    questoesRespondidas = linha.split(",");
                }

                linha = reader.readLine();
                if (linha != null) {
                    String[] questoesIds = linha.split(",");
                    perguntasRespondidasIds.clear();
                    for (int i = 0; i < questoesIds.length; i++) {
                        if (i < questoesRespondidas.length && questoesRespondidas[i].equals("1")) {
                            perguntasRespondidasIds.add(Integer.parseInt(questoesIds[i]));
                        }
                    }
                }

                // Atualiza a pontuação exibida
                lblPontuacao.setText("Pontos: R$ " + formatarPontuacao(pontos));
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erro ao carregar progresso: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvarESair() {
        int opcao = JOptionPane.showConfirmDialog(this,
            "Deseja salvar seu progresso e sair?",
            "Salvar e Sair",
            JOptionPane.YES_NO_OPTION);
        
        if (opcao == JOptionPane.YES_OPTION) {
            try (Connection conn = conexaoBD.obterConexao()) {
                // Verifica o tipo do usuário
                String sql = "SELECT tipo FROM usuarios WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, usuarioId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String tipoUsuario = rs.getString("tipo");
                        salvarProgresso();
                        salvarPontuacaoBanco();
                        dispose();
                        new TelaDificuldade(tipoUsuario, usuarioId).setVisible(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erro ao retornar à tela de dificuldade: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class Questao {
        private final String pergunta;
        private final List<String> respostas;
        private final int respostaCorreta;
        private final int id;
        private final int materiaId;
        private String ajuda;
        private boolean respondida;

        public Questao(String pergunta, List<String> respostas, int respostaCorreta, int id, int materiaId) {
            this.pergunta = pergunta;
            this.respostas = respostas;
            this.respostaCorreta = respostaCorreta;
            this.id = id;
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
            return respostaCorreta;
        }

        public int getId() {
            return id;
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

    
}