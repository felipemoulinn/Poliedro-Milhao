package com.example.java;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class TelaExcluir extends JFrame {
    private int usuarioId;
    private JComboBox<String> comboMateria;
    private JComboBox<String> comboPergunta;
    private Map<String, Integer> materiasMap = new HashMap<>();
    private Map<String, Integer> perguntasMap = new HashMap<>();
    private ConexaoBD conexaoBD = new ConexaoBD(); // 🔥 NOVO

    public TelaExcluir(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Excluir Pergunta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 14, 129));

        // TOPO
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

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(18, 14, 129));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel tituloLabel = new JLabel("EXCLUIR PERGUNTAS");
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 60, 0));
        centerPanel.add(tituloLabel);

        JLabel lblMateria = new JLabel("Selecione a matéria:");
        lblMateria.setForeground(Color.WHITE);
        lblMateria.setFont(new Font("Arial", Font.BOLD, 18));
        lblMateria.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblMateria);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        comboMateria = new JComboBox<>();
        estilizarDropdownArredondado(comboMateria);
        comboMateria.setMaximumSize(new Dimension(350, 45));
        comboMateria.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(comboMateria);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel lblPergunta = new JLabel("Selecione a pergunta:");
        lblPergunta.setForeground(Color.WHITE);
        lblPergunta.setFont(new Font("Arial", Font.BOLD, 18));
        lblPergunta.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblPergunta);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        comboPergunta = new JComboBox<>();
        estilizarDropdownArredondado(comboPergunta);
        comboPergunta.setMaximumSize(new Dimension(500, 45));
        comboPergunta.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(comboPergunta);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnExcluir = createMenuButton("EXCLUIR");
        btnExcluir.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(btnExcluir);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(18, 14, 129));
        centerWrapper.add(centerPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

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

        // 🔥 Lógica nova: carregar matérias e eventos
        carregarMaterias();
        comboMateria.addActionListener(e -> carregarPerguntas());
        btnExcluir.addActionListener(e -> excluirPerguntaSelecionada());
    }

    private void carregarMaterias() {
        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nome FROM materias");
             ResultSet rs = stmt.executeQuery()) {

            comboMateria.removeAllItems();
            materiasMap.clear();

            while (rs.next()) {
                String nome = rs.getString("nome");
                int id = rs.getInt("id");
                materiasMap.put(nome, id);
                comboMateria.addItem(nome);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar matérias.");
            e.printStackTrace();
        }
    }

    private void carregarPerguntas() {
        String materiaSelecionada = (String) comboMateria.getSelectedItem();
        if (materiaSelecionada == null) return;

        int materiaId = materiasMap.get(materiaSelecionada);

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, enunciado FROM perguntas WHERE materia_id = ? AND cadastrado_por = ?")) {
            stmt.setInt(1, materiaId);
            stmt.setInt(2, usuarioId);

            ResultSet rs = stmt.executeQuery();
            comboPergunta.removeAllItems();
            perguntasMap.clear();

            while (rs.next()) {
                String enunciado = rs.getString("enunciado");
                int id = rs.getInt("id");
                perguntasMap.put(enunciado, id);
                comboPergunta.addItem(enunciado);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar perguntas.");
            e.printStackTrace();
        }
    }

    private void excluirPerguntaSelecionada() {
        String perguntaSelecionada = (String) comboPergunta.getSelectedItem();
        if (perguntaSelecionada == null) return;

        int perguntaId = perguntasMap.get(perguntaSelecionada);

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir esta pergunta?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM perguntas WHERE id = ?")) {

            stmt.setInt(1, perguntaId);
            int afetadas = stmt.executeUpdate();

            if (afetadas > 0) {
                JOptionPane.showMessageDialog(this, "Pergunta excluída com sucesso.");
                carregarPerguntas(); // Atualiza lista
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir a pergunta.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir pergunta.");
            e.printStackTrace();
        }
    }

    // ⬇️ Métodos utilitários permanecem os mesmos
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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(corNormal); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                g2.dispose();
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 25));
        button.setBackground(corNormal);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 70));
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

    private void estilizarDropdownArredondado(JComboBox<String> dropdown) {
        dropdown.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dropdown.setBackground(Color.WHITE);
        dropdown.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        dropdown.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setFont(new Font("Arial", Font.PLAIN, 12));
                button.setContentAreaFilled(false);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setFocusPainted(false);
                return button;
            }
        });
        dropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
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