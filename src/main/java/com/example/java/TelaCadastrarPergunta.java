package com.example.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

public class TelaCadastrarPergunta extends JFrame {

    private List<CampoArredondado> alternativasErradas = new ArrayList<>();
    private ConexaoBD conexaoBD = new ConexaoBD();
    private int usuarioId;

    public TelaCadastrarPergunta(int usuarioId) {
        this.usuarioId = usuarioId;

        setTitle("Cadastrar Pergunta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 14, 129));

        // TOPO
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(18, 14, 129));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("CADASTRAR PERGUNTA");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 48));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(40, 0, 60, 0));
        centerPanel.add(titulo);

        centerPanel.add(criarLabel("Selecione a matéria:"));

        Vector<String> materias = new Vector<>();
        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement("SELECT nome FROM materias ORDER BY nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) materias.add(rs.getString("nome"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar matérias: " + e.getMessage());
            dispose();
            return;
        }

        if (materias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há matérias cadastradas.");
            dispose();
            return;
        }

        JComboBox<String> comboMateria = new JComboBox<>(materias);
        estilizarDropdownArredondado(comboMateria);
        comboMateria.setMaximumSize(new Dimension(350, 45));
        comboMateria.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(comboMateria);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        centerPanel.add(criarLabel("Nível de Dificuldade:"));
        JComboBox<String> comboDificuldade = new JComboBox<>(new String[]{"Fácil", "Médio", "Difícil"});
        estilizarDropdownArredondado(comboDificuldade);
        comboDificuldade.setMaximumSize(new Dimension(350, 45));
        comboDificuldade.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(comboDificuldade);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        centerPanel.add(criarLabel("Pergunta:"));
        CampoArredondado campoPergunta = new CampoArredondado(30);
        campoPergunta.setMaximumSize(new Dimension(500, 45));
        campoPergunta.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(campoPergunta);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        centerPanel.add(criarLabel("Ajuda:"));
        CampoArredondado campoAjuda = new CampoArredondado(30);
        campoAjuda.setMaximumSize(new Dimension(500, 45));
        campoAjuda.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(campoAjuda);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        centerPanel.add(criarLabel("Resposta Correta:"));
        CampoArredondado campoRespostaCorreta = new CampoArredondado(30);
        campoRespostaCorreta.setMaximumSize(new Dimension(500, 45));
        campoRespostaCorreta.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(campoRespostaCorreta);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        centerPanel.add(criarLabel("Alternativas Erradas:"));
        for (int i = 0; i < 3; i++) {
            CampoArredondado campoAlt = new CampoArredondado(30);
            campoAlt.setMaximumSize(new Dimension(500, 45));
            campoAlt.setAlignmentX(Component.CENTER_ALIGNMENT);
            alternativasErradas.add(campoAlt);
            centerPanel.add(campoAlt);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JButton btnCadastrar = createMenuButton("CADASTRAR");
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.addActionListener(e -> cadastrarPergunta(
                comboMateria.getSelectedItem().toString(),
                comboDificuldade.getSelectedItem().toString(),
                campoPergunta.getText(),
                campoAjuda.getText(),
                campoRespostaCorreta.getText()
        ));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        centerPanel.add(btnCadastrar);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(18, 14, 129));
        centerWrapper.add(centerPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // RODAPÉ
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setBackground(new Color(18, 14, 129));
        JButton voltarBtn = createMenuButton("VOLTAR");
        voltarBtn.setPreferredSize(new Dimension(130, 45));
        voltarBtn.addActionListener(e -> {
            new TelaEditar(usuarioId).setVisible(true);
            dispose();
        });
        rodape.add(voltarBtn);
        mainPanel.add(rodape, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void cadastrarPergunta(String materia, String dificuldade, String pergunta, String ajuda, String respostaCorreta) {
        if (pergunta.isBlank() || ajuda.isBlank() || respostaCorreta.isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.");
            return;
        }

        List<String> alternativas = new ArrayList<>();
        for (CampoArredondado campo : alternativasErradas) {
            String texto = campo.getText().trim();
            if (texto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todas as alternativas erradas.");
                return;
            }
            if (texto.equalsIgnoreCase(respostaCorreta.trim())) {
                JOptionPane.showMessageDialog(this, "Alternativa errada não pode ser igual à correta.");
                return;
            }
            if (alternativas.contains(texto)) {
                JOptionPane.showMessageDialog(this, "Alternativas repetidas não são permitidas.");
                return;
            }
            alternativas.add(texto);
        }

        try (Connection conn = conexaoBD.obterConexao()) {
            conn.setAutoCommit(false);
            int materiaId;

            try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM materias WHERE nome = ?")) {
                stmt.setString(1, materia);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) throw new Exception("Matéria não encontrada.");
                materiaId = rs.getInt("id");
            }

            int perguntaId;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO perguntas (enunciado, materia_id, ajuda, nivel_dificuldade, cadastrado_por) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, pergunta.trim());
                stmt.setInt(2, materiaId);
                stmt.setString(3, ajuda.trim());
                stmt.setString(4, dificuldade.toLowerCase().replace("fácil", "facil").replace("médio", "medio").replace("difícil", "dificil"));
                stmt.setInt(5, usuarioId);
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (!rs.next()) throw new Exception("Erro ao obter ID da pergunta.");
                perguntaId = rs.getInt(1);
            }

            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO respostas (pergunta_id, texto, correta) VALUES (?, ?, ?)")) {
                stmt.setInt(1, perguntaId);
                stmt.setString(2, respostaCorreta.trim());
                stmt.setBoolean(3, true);
                stmt.executeUpdate();

                stmt.setBoolean(3, false);
                for (String alt : alternativas) {
                    stmt.setString(2, alt);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Pergunta cadastrada com sucesso.");
            limparCampos();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar pergunta: " + e.getMessage());
        }
    }

    private void limparCampos() {
        for (CampoArredondado campo : alternativasErradas) campo.setText("");
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
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.setColor(Color.BLACK); g2.drawString(getText(), x + 1, y + 1);
                g2.setColor(getForeground()); g2.drawString(getText(), x, y);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
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
            public void mouseEntered(MouseEvent e) { button.setBackground(corHover); }
            public void mouseExited(MouseEvent e) { button.setBackground(corNormal); }
        });

        return button;
    }

    private void estilizarDropdownArredondado(JComboBox<String> dropdown) {
        dropdown.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dropdown.setBackground(Color.WHITE);
        dropdown.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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

    // ✅ Classe utilitária interna para evitar erro de tipo
    class CampoArredondado extends JTextField {
        public CampoArredondado(int tamanho) {
            super(tamanho);
            setOpaque(false);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            setForeground(Color.BLACK);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override protected void paintBorder(Graphics g) {
            // Sem borda
        }
    }
}
