package src.main;

import java.awt.*;
import javax.swing.*;

public class TelaEditar extends JFrame {
    public TelaEditar() {
        setTitle("Editar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(13, 11, 80));

        String textoParabens = "<html><div style='text-align: center;'> <br><br>EDITAR PERGUNTAS </div></html>";
        JLabel parabensLabel = new JLabel(textoParabens);
        parabensLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        parabensLabel.setForeground(Color.WHITE);
        parabensLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(parabensLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(13, 11, 80));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        String[] botoes = {"Adicionar perguntas", "Excluir perguntas"};
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(btn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(13, 11, 80));
        centerPanel.add(buttonPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(new Color(10, 10, 80));
        JButton voltarBtn = createMenuButton("VOLTAR");
        voltarBtn.setPreferredSize(new Dimension(130, 45));
        rodape.add(voltarBtn);
        mainPanel.add(rodape, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
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
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i != 0 || j != 0) {
                            g2.drawString(getText(), x + i, y + j);
                        }
                    }
                }

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
        button.setPreferredSize(new Dimension(300, 70));
        button.setMinimumSize(new Dimension(250, 60));
        button.setMaximumSize(new Dimension(350, 80));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(corHover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(corNormal);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEditar());
    }
}