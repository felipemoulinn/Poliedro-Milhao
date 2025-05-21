package src.main;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class TelaExcluir extends JFrame {
    private JComboBox<String> materiaDropDown;
    private JComboBox<String> perguntasDropDown;

    public TelaExcluir() {
        setTitle("Excluir");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(10, 10, 80));
        setLayout(new GridBagLayout());

        JPanel painelCentral = new JPanel();
        painelCentral.setBackground(new Color(10, 10, 80));
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setPreferredSize(new Dimension(700, 550));

        JLabel titulo = new JLabel("EXCLUIR"); // achei melhor com o texto 'EXCLUR' do q sem
        titulo.setFont(new Font("SansSerif", Font.BOLD, 42));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(titulo);
        painelCentral.add(Box.createRigidArea(new Dimension(10, 40)));

        JLabel materiaLabel = new JLabel("Selecione a Matéria:");
        materiaLabel.setForeground(Color.WHITE);
        materiaLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        materiaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(materiaLabel);

        String[] materias = {"Matemática", "Português", "História", "Geografia"};
        materiaDropDown = new JComboBox<>(materias);
        estilizarDropdownArredondado(materiaDropDown);
        painelCentral.add(materiaDropDown);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel perguntaLabel = new JLabel("Selecione a Pergunta:");
        perguntaLabel.setForeground(Color.WHITE);
        perguntaLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        perguntaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(perguntaLabel);

        String[] perguntas = {"Teste Pergunta 1", "Teste Pergunta 2", "Teste Pergunta 3", "Teste Pergunta 4", "Teste Pergunta 5"}; // n sabia como puxar do banco por materia
        perguntasDropDown = new JComboBox<>(perguntas);
        estilizarDropdownArredondado(perguntasDropDown);
        painelCentral.add(perguntasDropDown);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 10)));


        JButton excluirBtn = criarBotaoArredondado("Excluir", new Color(255, 153, 0), Color.WHITE);
        excluirBtn.setMaximumSize(new Dimension(200, 100));
        excluirBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(excluirBtn);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(new Color(10, 10, 80));
        JButton voltarBtn = criarBotaoArredondado("VOLTAR", new Color(255, 153, 0), Color.WHITE);
        voltarBtn.setPreferredSize(new Dimension(130, 45));
        rodape.add(voltarBtn);
        painelCentral.add(rodape);

        add(painelCentral);
        setVisible(true);
    }

    private void estilizarDropdownArredondado(JComboBox<String> dropdown) {
        dropdown.setMaximumSize(new Dimension(480, 45));
        dropdown.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dropdown.setBackground(Color.WHITE);
        dropdown.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        dropdown.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setFont(new Font("Arial", Font.PLAIN, 12));
                button.setContentAreaFilled(false);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setFocusPainted(false);
                button.setMargin(new Insets(0, 0, 0, 0));
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

    private JButton criarBotaoArredondado(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaExcluir());
    }
}