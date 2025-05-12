package src.main.jogo.tela;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaProf extends JFrame {
    public TelaProf() {
        setTitle("Poliedro Milhão - Professor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(13, 11, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);

        String[] botoes = {"JOGAR", "RANKING", "EDITAR"};
        for (String texto : botoes) {
            JButton btn = createMenuButton(texto);
            mainPanel.add(btn, gbc);
        }

        add(mainPanel);
        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        Color corNormal = new Color(195, 141, 41);           // amarelo base
        Color corHover = new Color(255, 200, 70);            // amarelo hover mais claro

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

        button.setFont(new Font("Arial", Font.BOLD, 32));
        button.setBackground(corNormal);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(400, 85));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(corHover);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(corNormal);
                button.repaint();
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaProf());
    }
}