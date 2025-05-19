
package src.main;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TelaCadastro extends JFrame {
    private JTextField emailField;
    private JPasswordField senhaField;

    public TelaCadastro() {
        setTitle("Cadastro");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(10, 10, 80));
        setLayout(new GridBagLayout());

        JPanel painelCentral = new JPanel();
        painelCentral.setBackground(new Color(10, 10, 80));
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setPreferredSize(new Dimension(700, 550));

        JLabel titulo = new JLabel("CADASTRO");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 42));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(titulo);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 40)));

        JLabel emailLabel = new JLabel("Login (e-mail) ✉");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(emailLabel);

        emailField = new JTextField();
        estilizarCampoArredondado(emailField);
        painelCentral.add(emailField);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel senhaLabel = new JLabel("Senha 🔒");
        senhaLabel.setForeground(Color.WHITE);
        senhaLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        senhaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(senhaLabel);

        senhaField = new JPasswordField();
        estilizarCampoArredondado(senhaField);
        painelCentral.add(senhaField);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton cadastrarBtn = criarBotaoArredondado("Cadastrar", new Color(220, 220, 220), Color.BLACK);
        cadastrarBtn.setMaximumSize(new Dimension(230, 50));
        cadastrarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(cadastrarBtn);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(new Color(10, 10, 80));
        JButton voltarBtn = criarBotaoArredondado("VOLTAR", new Color(255, 153, 0), Color.WHITE);
        voltarBtn.setPreferredSize(new Dimension(130, 45));
        rodape.add(voltarBtn);
        painelCentral.add(rodape);

        cadastrarBtn.addActionListener(e -> cadastrarUsuario());

        add(painelCentral);
        setVisible(true);
    }

    private void estilizarCampoArredondado(JTextField campo) {
        campo.setMaximumSize(new Dimension(480, 45));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        campo.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintSafely(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, campo.getWidth(), campo.getHeight(), 40, 40);
                super.paintSafely(g);
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

    private void cadastrarUsuario() {
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/showdomilhao", "usuario", "senha"
            );

            String sql = "INSERT INTO usuarios (email, senha) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, senha);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            emailField.setText("");
            senhaField.setText("");

            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastro());
    }
}
