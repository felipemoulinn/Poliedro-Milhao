package com.example.java;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class TelaExcluir extends JFrame {
    private int usuarioId;
    private JComboBox<String> comboMateria;
    private JComboBox<String> comboPergunta;
    private Map<String, Integer> materiasMap = new HashMap<>();
    private Map<String, Integer> perguntasMap = new HashMap<>();
    private ConexaoBD conexaoBD = new ConexaoBD();
    private BufferedImage backgroundImage;

    public TelaExcluir(int usuarioId) {
        this.usuarioId = usuarioId;
        
        // Carrega a imagem de fundo
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        setTitle("Excluir Pergunta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Painel principal com imagem de fundo
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback se a imagem não carregar
                    g.setColor(new Color(18, 14, 129));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TOPO - Botão de perfil
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getResource("/perfil.png"));
        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        
        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));
        configurarBotaoIcone(btnPerfil);
        aplicarHoverIconeSimples(btnPerfil);
        btnPerfil.addActionListener(e -> {
            new TelaUsuario(this, usuarioId).setVisible(true);
        });
        
        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // CENTRO - Conteúdo principal
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        JLabel tituloLabel = new JLabel("EXCLUIR PERGUNTAS");
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0));
        centerPanel.add(tituloLabel);

        // Combobox de matérias
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

        // Combobox de perguntas
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

        // Botão Excluir
        JButton btnExcluir = createMenuButton("EXCLUIR");
        btnExcluir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExcluir.addActionListener(e -> excluirPerguntaSelecionada());
        centerPanel.add(btnExcluir);

        // Centraliza o painel de conteúdo
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(centerPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // RODAPÉ - Botão Voltar
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setOpaque(false);
        
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

        // Carrega as matérias e configura listeners
        carregarMaterias();
        comboMateria.addActionListener(e -> carregarPerguntas());
    }

    // Métodos de carregamento de dados (mantidos iguais)
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

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir esta pergunta?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM perguntas WHERE id = ?")) {

            stmt.setInt(1, perguntaId);
            int afetadas = stmt.executeUpdate();

            if (afetadas > 0) {
                JOptionPane.showMessageDialog(this, "Pergunta excluída com sucesso.");
                carregarPerguntas();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir a pergunta.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir pergunta.");
            e.printStackTrace();
        }
    }

    // Métodos de estilo (mantidos iguais)
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