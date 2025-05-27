package com.example.java;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// Campo de texto arredondado
class CampoArredondado extends JTextField {
    public CampoArredondado(int tamanho) {
        super(tamanho);
        setOpaque(false);
        setFont(new Font("Arial", Font.PLAIN, 16));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setForeground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {}
}

// ComboBox arredondado
class ComboBoxArredondado<E> extends JComboBox<E> {
    public ComboBoxArredondado(E[] itens) {
        super(itens);
        setUI(new BasicComboBoxUI());
        setFont(new Font("Arial", Font.PLAIN, 16));
        setForeground(Color.BLACK);
        setBackground(Color.WHITE);
        setOpaque(false);
        setFocusable(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {}
}

public class TelaCadastrarPergunta extends JFrame {
    private List<CampoArredondado> alternativasErradas;
    private ConexaoBD conexaoBD;
    private int usuarioId;
    
    public TelaCadastrarPergunta(int usuarioId) {
        this.usuarioId = usuarioId;
        this.conexaoBD = new ConexaoBD();
        this.alternativasErradas = new ArrayList<>();
        
        setTitle("Cadastrar Pergunta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 14, 129));

        // TOPO com ícones
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(18, 14, 129)); // ✅ Agora igual ao fundo principal

        ImageIcon iconePerfil = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        ImageIcon iconeConfig = new ImageIcon(getClass().getClassLoader().getResource("configuracoes.png"));

        Image perfilImg = iconePerfil.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Image configImg = iconeConfig.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JButton btnPerfil = new JButton(new ImageIcon(perfilImg));
        JButton btnConfig = new JButton(new ImageIcon(configImg));

        configurarBotaoIcone(btnPerfil);
        configurarBotaoIcone(btnConfig);
        aplicarHoverIconeSimples(btnPerfil);
        aplicarHoverIconeSimples(btnConfig);

        btnConfig.addActionListener(e -> new TelaSom().setVisible(true));

        topPanel.add(btnConfig, BorderLayout.WEST);
        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // CENTRO
        JPanel centroWrapper = new JPanel(new GridBagLayout());
        centroWrapper.setBackground(new Color(18, 14, 129));

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(new Color(18, 14, 129));

        // Matéria
        JLabel lblMateria = criarLabel("Selecione a matéria:");
        conteudo.add(lblMateria);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        // Carregar matérias do banco de dados
        Vector<String> materias = new Vector<>();
        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement("SELECT nome FROM materias ORDER BY nome");
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                materias.add(rs.getString("nome"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar matérias: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        if (materias.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Não há matérias cadastradas no sistema!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        ComboBoxArredondado<String> comboMateria = new ComboBoxArredondado<>(materias.toArray(new String[0]));
        comboMateria.setMaximumSize(new Dimension(400, 45));
        comboMateria.setPreferredSize(new Dimension(400, 45));
        comboMateria.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(comboMateria);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));

        // Nível de Dificuldade
        JLabel lblDificuldade = criarLabel("Nível de Dificuldade:");
        conteudo.add(lblDificuldade);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        ComboBoxArredondado<String> comboDificuldade = new ComboBoxArredondado<>(
            new String[]{"Fácil", "Médio", "Difícil"});
        comboDificuldade.setMaximumSize(new Dimension(400, 45));
        comboDificuldade.setPreferredSize(new Dimension(400, 45));
        comboDificuldade.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(comboDificuldade);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));

        // Pergunta
        JLabel lblPergunta = criarLabel("Insira a pergunta:");
        conteudo.add(lblPergunta);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        CampoArredondado campoPergunta = new CampoArredondado(30);
        campoPergunta.setMaximumSize(new Dimension(500, 50));
        campoPergunta.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(campoPergunta);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));

        // Ajuda
        JLabel lblAjuda = criarLabel("Texto de Ajuda:");
        conteudo.add(lblAjuda);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        CampoArredondado campoAjuda = new CampoArredondado(30);
        campoAjuda.setMaximumSize(new Dimension(500, 50));
        campoAjuda.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(campoAjuda);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));

        // Resposta Correta
        JLabel lblRespostaCorreta = criarLabel("Resposta Correta:");
        conteudo.add(lblRespostaCorreta);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        CampoArredondado campoRespostaCorreta = new CampoArredondado(30);
        campoRespostaCorreta.setMaximumSize(new Dimension(500, 50));
        campoRespostaCorreta.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(campoRespostaCorreta);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));

        // Alternativas Erradas
        JLabel lblAlternativasErradas = criarLabel("Alternativas Erradas:");
        conteudo.add(lblAlternativasErradas);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        // Container para alternativas erradas
        JPanel alternativasPanel = new JPanel();
        alternativasPanel.setLayout(new BoxLayout(alternativasPanel, BoxLayout.Y_AXIS));
        alternativasPanel.setOpaque(false);

        // Adicionar 3 campos para alternativas erradas
        for (int i = 0; i < 3; i++) {
            CampoArredondado campoAlternativa = new CampoArredondado(30);
            campoAlternativa.setMaximumSize(new Dimension(500, 50));
            campoAlternativa.setAlignmentX(Component.CENTER_ALIGNMENT);
            alternativasErradas.add(campoAlternativa);
            alternativasPanel.add(campoAlternativa);
            alternativasPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        conteudo.add(alternativasPanel);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton btnAdicionar = createMenuButton("CADASTRAR");
        btnAdicionar.setPreferredSize(new Dimension(300, 70));
        btnAdicionar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdicionar.addActionListener(e -> cadastrarPergunta(
            comboMateria.getSelectedItem().toString(),
            comboDificuldade.getSelectedItem().toString(),
            campoPergunta.getText(),
            campoAjuda.getText(),
            campoRespostaCorreta.getText()
        ));
        conteudo.add(btnAdicionar);

        JScrollPane scrollPane = new JScrollPane(conteudo);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        centroWrapper.add(scrollPane);
        mainPanel.add(centroWrapper, BorderLayout.CENTER);

        // Rodapé
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setBackground(new Color(18, 14, 129));

        JButton btnVoltar = createMenuButton("VOLTAR");
        btnVoltar.setPreferredSize(new Dimension(130, 45));
        btnVoltar.addActionListener(e -> {
            new TelaEditar(usuarioId).setVisible(true);
            dispose();
        });
        rodape.add(btnVoltar);

        mainPanel.add(rodape, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    private void cadastrarPergunta(String materia, String dificuldade, String pergunta, String ajuda, String respostaCorreta) {
        // Validar campos obrigatórios
        if (pergunta.trim().isEmpty() || ajuda.trim().isEmpty() || respostaCorreta.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar alternativas erradas
        List<String> alternativas = new ArrayList<>();
        for (CampoArredondado campo : alternativasErradas) {
            String alternativa = campo.getText().trim();
            if (alternativa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todas as alternativas erradas!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Verificar se a alternativa é igual à resposta correta
            if (alternativa.equalsIgnoreCase(respostaCorreta.trim())) {
                JOptionPane.showMessageDialog(this, "Uma alternativa errada não pode ser igual à resposta correta!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Verificar se há alternativas repetidas
            if (alternativas.contains(alternativa)) {
                JOptionPane.showMessageDialog(this, "Não são permitidas alternativas repetidas!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            alternativas.add(alternativa);
        }

        try (Connection conn = conexaoBD.obterConexao()) {
            conn.setAutoCommit(false); // Inicia transação

            try {
                // 1. Obter ID da matéria
                int materiaId;
                String sqlMateria = "SELECT id FROM materias WHERE nome = ?";
                try (PreparedStatement stmtMateria = conn.prepareStatement(sqlMateria)) {
                    stmtMateria.setString(1, materia);
                    ResultSet rs = stmtMateria.executeQuery();
                    if (!rs.next()) {
                        throw new Exception("Matéria não encontrada!");
                    }
                    materiaId = rs.getInt("id");
                }

                // 2. Inserir a pergunta
                String sqlPergunta = "INSERT INTO perguntas (enunciado, materia_id, ajuda, nivel_dificuldade, cadastrado_por) " +
                                   "VALUES (?, ?, ?, ?, ?)";
                int perguntaId;
                try (PreparedStatement stmtPergunta = conn.prepareStatement(sqlPergunta, Statement.RETURN_GENERATED_KEYS)) {
                    stmtPergunta.setString(1, pergunta.trim());
                    stmtPergunta.setInt(2, materiaId);
                    stmtPergunta.setString(3, ajuda.trim());
                    // Normalizar dificuldade para o formato do banco
                    String nivelDificuldade = dificuldade.toLowerCase()
                        .replace("fácil", "facil")
                        .replace("médio", "medio")
                        .replace("difícil", "dificil");
                    stmtPergunta.setString(4, nivelDificuldade);
                    stmtPergunta.setInt(5, usuarioId);
                    stmtPergunta.executeUpdate();

                    ResultSet rs = stmtPergunta.getGeneratedKeys();
                    if (!rs.next()) {
                        throw new Exception("Falha ao obter ID da pergunta inserida!");
                    }
                    perguntaId = rs.getInt(1);
                }

                // 3. Inserir as respostas
                String sqlResposta = "INSERT INTO respostas (pergunta_id, texto, correta) VALUES (?, ?, ?)";
                try (PreparedStatement stmtResposta = conn.prepareStatement(sqlResposta)) {
                    // Inserir resposta correta
                    stmtResposta.setInt(1, perguntaId);
                    stmtResposta.setString(2, respostaCorreta.trim());
                    stmtResposta.setBoolean(3, true);
                    stmtResposta.executeUpdate();

                    // Inserir alternativas erradas
                    stmtResposta.setBoolean(3, false);
                    for (String alternativa : alternativas) {
                        stmtResposta.setString(2, alternativa.trim());
                        stmtResposta.executeUpdate();
                    }
                }

                conn.commit(); // Confirma a transação
                JOptionPane.showMessageDialog(this, "Pergunta cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();

            } catch (Exception e) {
                conn.rollback(); // Desfaz a transação em caso de erro
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao cadastrar pergunta: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        // Limpar todos os campos após cadastro bem-sucedido
        for (CampoArredondado campo : alternativasErradas) {
            campo.setText("");
        }
        // Limpar outros campos
        Component[] components = getContentPane().getComponents();
        limparCamposRecursivamente(components);
    }

    private void limparCamposRecursivamente(Component[] components) {
        for (Component component : components) {
            if (component instanceof CampoArredondado) {
                ((CampoArredondado) component).setText("");
            } else if (component instanceof Container) {
                limparCamposRecursivamente(((Container) component).getComponents());
            }
        }
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastrarPergunta(1));
    }
}