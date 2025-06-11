package com.example.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TelaAluno extends JFrame {
    private int usuarioId;
    private BufferedImage backgroundImage;
    private BufferedImage buttonImage;
    
    public TelaAluno(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Poliedro Milhão - Aluno");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/bg3.png"));
            buttonImage = ImageIO.read(getClass().getResource("/botao1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setOpaque(false);

        // TOPO COM ÍCONES
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon iconePerfil = new ImageIcon(getClass().getClassLoader().getResource("perfil.png"));
        Image imgPerfil = iconePerfil.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        JButton btnPerfil = new JButton(new ImageIcon(imgPerfil));

        configurarBotaoIcone(btnPerfil);
        aplicarHoverIconeSimples(btnPerfil);

        btnPerfil.addActionListener(e -> {
            TelaUsuario telaUsuario = new TelaUsuario(this, usuarioId);
            telaUsuario.setVisible(true);
        });

        topPanel.add(btnPerfil, BorderLayout.EAST);

        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.weightx = 1;
        gbcTop.fill = GridBagConstraints.HORIZONTAL;
        gbcTop.anchor = GridBagConstraints.NORTH;
        mainPanel.add(topPanel, gbcTop);

        // CENTRO COM LOGO E BOTÃO
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Logo
        ImageIcon logoIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        Image imgLogo = logoIcon.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(imgLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        // Botão JOGAR com imagem personalizada
        JButton btnJogar = createMenuButton("JOGAR", buttonImage);
        btnJogar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnJogar.setPreferredSize(new Dimension(280, 65));
        btnJogar.setMaximumSize(new Dimension(280, 65));
        btnJogar.setMinimumSize(new Dimension(280, 65));

        btnJogar.addActionListener(e -> {
            new TelaDificuldade("aluno", usuarioId).setVisible(true);
            dispose();
        });

        // Montar layout
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(100));
        centerPanel.add(btnJogar);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0;
        gbcCenter.gridy = 1;
        gbcCenter.weightx = 1;
        gbcCenter.weighty = 1;
        gbcCenter.anchor = GridBagConstraints.CENTER;
        gbcCenter.fill = GridBagConstraints.NONE;

        mainPanel.add(centerPanel, gbcCenter);
        add(mainPanel);
        setVisible(true);
    }

    private JButton createMenuButton(String text, BufferedImage buttonImage) {
        JButton button = new JButton(text) {
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

        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito hover simples (escurece a imagem)
        button.addMouseListener(new MouseAdapter() {
            private ColorFilter filter = new ColorFilter(0.7f);

            public void mouseEntered(MouseEvent evt) {
                button.setIcon(new ImageIcon(applyFilter(buttonImage, filter)));
            }

            public void mouseExited(MouseEvent evt) {
                button.setIcon(new ImageIcon(buttonImage));
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
        botao.setBackground(new Color(255, 255, 255, 30));
        botao.setContentAreaFilled(false);
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