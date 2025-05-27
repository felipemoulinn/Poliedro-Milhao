package com.example.java;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TelaDificuldade extends JFrame {
    private String tipoUsuario;
    private int usuarioId;

    public TelaDificuldade(String tipoUsuario, int usuarioId) {
        this.tipoUsuario = tipoUsuario;
        this.usuarioId = usuarioId;

        setTitle("Selecionar Dificuldade!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 14, 129));

        // TOPO
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        ImageIcon iconeConfigOriginal = new ImageIcon(getClass().getClassLoader().getResource("configuracoes.png"));

        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Image imgConfig = iconeConfigOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JButton btnConfig = new JButton(new ImageIcon(imgConfig));
        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));

        configurarBotaoIcone(btnConfig);
        configurarBotaoIcone(btnPerfil);

        btnConfig.addActionListener(e -> new TelaSom().setVisible(true));

        topPanel.add(btnConfig, BorderLayout.WEST);
        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // CENTRO
        JPanel centroPanel = new JPanel();
        centroPanel.setBackground(new Color(18, 14, 129));
        centroPanel.setLayout(new BoxLayout(centroPanel, BoxLayout.Y_AXIS));

        JLabel tituloLabel = new JLabel("DIFICULDADE");
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 140, 0));

        centroPanel.add(tituloLabel);

        String[] botoes = { "FÁCIL", "MÉDIO", "DIFÍCIL" };
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setPreferredSize(new Dimension(300, 65));
            btn.setMinimumSize(new Dimension(300, 65));
            btn.setMaximumSize(new Dimension(300, 65));
            btn.addActionListener(e -> {
                String dificuldade = texto.toLowerCase().replace('á', 'a').replace('é', 'e').replace('í', 'i');
                new TelaQuiz(dificuldade, usuarioId).setVisible(true);
                dispose();
            });
            centroPanel.add(btn);
            centroPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JPanel centroWrapper = new JPanel(new GridBagLayout());
        centroWrapper.setBackground(new Color(18, 14, 129));
        centroWrapper.add(centroPanel);
        mainPanel.add(centroWrapper, BorderLayout.CENTER);

        // RODAPÉ COM BOTÃO VOLTAR
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(new Color(18, 14, 129));

        JButton btnVoltar = createMenuButton("VOLTAR");
        btnVoltar.setPreferredSize(new Dimension(150, 50));
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 20));

        btnVoltar.addActionListener(e -> {
            switch (tipoUsuario.toLowerCase()) {
                case "aluno":
                    new TelaAluno(usuarioId).setVisible(true);
                    break;
                case "professor":
                    new TelaLoginProf(usuarioId).setVisible(true);
                    break;
                case "admin":
                    new TelaAdm(usuarioId).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Tipo de usuário desconhecido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            dispose();
        });

        rodape.add(btnVoltar);
        mainPanel.add(rodape, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void configurarBotaoIcone(JButton botao) {
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JButton createMenuButton(String texto) {
        Color corNormal = new Color(195, 141, 41);
        Color corHover = new Color(255, 200, 70);

        JButton botao = new JButton(texto) {
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
                g2.setColor(new Color(195, 141, 41));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                g2.dispose();
            }
        };

        botao.setFont(new Font("Arial", Font.BOLD, 26));
        botao.setBackground(corNormal);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(corHover);
            }

            public void mouseExited(MouseEvent evt) {
                botao.setBackground(corNormal);
            }
        });

        return botao;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaDificuldade("aluno", 1));
    }
}