package com.example.java;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;

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

    public TelaCadastrarPergunta() {
        setTitle("Editar Pergunta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(13, 11, 80));

        // TOPO com ícones
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
        JPanel centroWrapper = new JPanel(new GridBagLayout());
        centroWrapper.setBackground(new Color(18, 14, 129));

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(new Color(18, 14, 129));

        JLabel lblMateria = criarLabel("Selecione a matéria:");
        conteudo.add(lblMateria);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        ComboBoxArredondado<String> comboMateria = new ComboBoxArredondado<>(
            new String[]{"Biologia", "Matemática", "Português", "História", "Geografia"});
        comboMateria.setMaximumSize(new Dimension(400, 45));
        comboMateria.setPreferredSize(new Dimension(400, 45));
        comboMateria.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(comboMateria);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel lblPergunta = criarLabel("Insira a pergunta aqui:");
        conteudo.add(lblPergunta);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        CampoArredondado campoPergunta = new CampoArredondado(30);
        campoPergunta.setMaximumSize(new Dimension(500, 50));
        campoPergunta.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(campoPergunta);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel lblResposta = criarLabel("Insira a resposta aqui:");
        conteudo.add(lblResposta);
        conteudo.add(Box.createRigidArea(new Dimension(0, 10)));

        CampoArredondado campoResposta = new CampoArredondado(30);
        campoResposta.setMaximumSize(new Dimension(500, 50));
        campoResposta.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(campoResposta);
        conteudo.add(Box.createRigidArea(new Dimension(0, 35)));

        JButton btnAdicionar = createMenuButton("ADICIONAR");
        btnAdicionar.setPreferredSize(new Dimension(300, 70));
        btnAdicionar.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(btnAdicionar);

        centroWrapper.add(conteudo);
        mainPanel.add(centroWrapper, BorderLayout.CENTER);

        // Rodapé
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setBackground(new Color(18, 14, 129));

        JButton btnVoltar = createMenuButton("VOLTAR");
        btnVoltar.setPreferredSize(new Dimension(130, 45));
        btnVoltar.addActionListener(e -> {
            new TelaEditar().setVisible(true);
            dispose();
        });
        rodape.add(btnVoltar);

        mainPanel.add(rodape, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
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
        SwingUtilities.invokeLater(TelaCadastrarPergunta::new);
    }
}
