package com.example.java;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TelaDificuldade extends JFrame {
    private String tipoUsuario;
    private int usuarioId;
    private BufferedImage backgroundImage;
    private BufferedImage buttonImage;

    public TelaDificuldade(String tipoUsuario, int usuarioId) {
        this.tipoUsuario = tipoUsuario;
        this.usuarioId = usuarioId;
        
        setTitle("Selecionar Dificuldade!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/bg3.png"));
            buttonImage = ImageIO.read(getClass().getResource("/botao1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage finalBackgroundImage = backgroundImage;
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBackgroundImage != null) {
                    g.drawImage(finalBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setOpaque(false);

        // TOPO
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon iconePerfilOriginal = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        Image imgPerfil = iconePerfilOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));
        configurarBotaoIcone(btnPerfil);

        btnPerfil.addActionListener(e -> {
            TelaUsuario telaUsuario = new TelaUsuario(this, usuarioId);
            telaUsuario.setVisible(true);
        });

        topPanel.add(btnPerfil, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // CENTRO
        JPanel centroPanel = new JPanel();
        centroPanel.setOpaque(false);
        centroPanel.setLayout(new BoxLayout(centroPanel, BoxLayout.Y_AXIS));

        JLabel tituloLabel = new JLabel("DIFICULDADE");
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 140, 0));

        centroPanel.add(tituloLabel);

        String[] botoes = { "FÁCIL", "MÉDIO", "DIFÍCIL" };
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto, buttonImage);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setPreferredSize(new Dimension(300, 65));
            btn.setMinimumSize(new Dimension(300, 65));
            btn.setMaximumSize(new Dimension(300, 65));
            btn.addActionListener(e -> {
                String dificuldade = texto.toLowerCase().replace('á', 'a').replace('é', 'e').replace('í', 'i');
        
            // Verifica se existem questões para esta dificuldade
            ConexaoBD questaoDAO = new ConexaoBD(); // Supondo que você tenha esta classe
            boolean existemQuestoes = questaoDAO.verificarQuestoesDisponiveis(dificuldade);
            if (existemQuestoes) {
                new TelaQuiz(dificuldade, usuarioId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                    TelaDificuldade.this,
                    "Não há questões disponíveis para a dificuldade " + texto + " no momento.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
                // Não faz dispose() - mantém na tela atual
            }
        });
        centroPanel.add(btn);
        centroPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }
        mainPanel.add(centroPanel, BorderLayout.CENTER);

        // RODAPÉ COM BOTÃO VOLTAR
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setOpaque(false);

        JButton btnVoltar = createMenuButton("VOLTAR", buttonImage);
        btnVoltar.setPreferredSize(new Dimension(150, 50));
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 20));

        btnVoltar.addActionListener(e -> {
            switch (tipoUsuario.toLowerCase()) {
                case "aluno":
                    new TelaAluno(usuarioId).setVisible(true);
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

    private JButton createMenuButton(String texto, BufferedImage buttonImage) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Desenha a imagem do botão
                if (buttonImage != null) {
                    g2.drawImage(buttonImage, 0, 0, getWidth(), getHeight(), this);
                }
                
                // Desenha o texto
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                g2.setColor(Color.BLACK);
                g2.drawString(getText(), x + 1, y + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Não desenha borda para manter a aparência da imagem
            }
        };

        botao.setFont(new Font("Arial", Font.BOLD, 26));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito hover simples (escurece a imagem)
        botao.addMouseListener(new MouseAdapter() {
            private ColorFilter filter = new ColorFilter(0.7f);

            public void mouseEntered(MouseEvent evt) {
                botao.setIcon(new ImageIcon(applyFilter(buttonImage, filter)));
            }

            public void mouseExited(MouseEvent evt) {
                botao.setIcon(new ImageIcon(buttonImage));
            }
        });

        return botao;
    }

    // Classe auxiliar para aplicar efeito hover
    private static class ColorFilter {
        private float factor;

        public ColorFilter(float factor) {
            this.factor = factor;
        }

        public int filterRGB(int x, int y, int rgb) {
            int a = (rgb >> 24) & 0xFF;
            int r = (int) (((rgb >> 16) & 0xFF) * factor);
            int g = (int) (((rgb >> 8) & 0xFF) * factor);
            int b = (int) ((rgb & 0xFF) * factor);
            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    // Método para aplicar filtro à imagem
    private BufferedImage applyFilter(BufferedImage src, ColorFilter filter) {
        if (src == null) return null;
        
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                dst.setRGB(x, y, filter.filterRGB(x, y, src.getRGB(x, y)));
            }
        }
        return dst;
    }
}