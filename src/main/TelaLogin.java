package src.main;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaLogin extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtSenha;

    public TelaLogin() {
        // Configuração da janela
        setTitle("Show do Milhão - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(13, 11, 80));

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(13, 11, 80));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(13, 11, 80));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.setMaximumSize(new Dimension(500, 700));

        // Logo
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo.png"));
            Image logoImage = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(logoImage));
        } catch (Exception e) {
            logoLabel.setText("Show do Milhão");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
            logoLabel.setForeground(Color.WHITE);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 50, 0));

        // Título
        JLabel titulo = new JLabel("Login");
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Campo de e-mail
        JLabel lblEmail = new JLabel("E-mail");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 18));
        lblEmail.setForeground(Color.WHITE);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblEmail.setBorder(new EmptyBorder(0, 0, 10, 0));

        txtEmail = new RoundJTextField(40);
        txtEmail.setPreferredSize(new Dimension(400, 50));
        txtEmail.setMaximumSize(new Dimension(400, 50));
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        txtEmail.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        txtEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campo de senha
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSenha.setForeground(Color.WHITE);
        lblSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSenha.setBorder(new EmptyBorder(20, 0, 10, 0));

        txtSenha = new RoundJPasswordField(40);
        txtSenha.setPreferredSize(new Dimension(400, 50));
        txtSenha.setMaximumSize(new Dimension(400, 50));
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 16));
        txtSenha.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        txtSenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botão de entrar
        JButton btnEntrar = new RoundJButton("Entrar", 40);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnEntrar.setBackground(new Color(0, 120, 215));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setPreferredSize(new Dimension(400, 55));
        btnEntrar.setMaximumSize(new Dimension(400, 55));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        btnEntrar.setMargin(new Insets(10, 30, 10, 30));

        // Link para cadastro
        JLabel lblCadastro = new JLabel("Não tem conta? Cadastre-se");
        lblCadastro.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCadastro.setForeground(Color.WHITE);
        lblCadastro.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCadastro.setBorder(new EmptyBorder(20, 0, 0, 0));
        lblCadastro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblCadastro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new TelaCadastro().setVisible(true);
                dispose();
            }
        });

        // Adiciona componentes ao painel central
        centerPanel.add(logoLabel);
        centerPanel.add(titulo);
        centerPanel.add(lblEmail);
        centerPanel.add(txtEmail);
        centerPanel.add(lblSenha);
        centerPanel.add(txtSenha);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(btnEntrar);
        centerPanel.add(lblCadastro);

        // Adiciona ao painel principal
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(centerPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);

        // Configura ação do botão entrar
        configurarAcaoLogin();
    }

    private void configurarAcaoLogin() {
        btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText().trim();
                String senha = new String(txtSenha.getPassword()).trim();

                if (validarCampos(email, senha)) {
                    autenticarUsuario(email, senha);
                }
            }
        });

        // Enter pressionado no campo de senha
        txtSenha.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();
            
            if (validarCampos(email, senha)) {
                autenticarUsuario(email, senha);
            }
        });
    }

    private boolean validarCampos(String email, String senha) {
        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Preencha todos os campos", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!email.matches("^[\\w-.]+@(aluno|prof)\\.\\w+$")) {
            JOptionPane.showMessageDialog(this, 
                "O e-mail deve conter '@aluno' ou '@prof' e ser válido", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void autenticarUsuario(String email, String senha) {
        BANCO banco = new BANCO();
        if (banco.verificarLogin(email, senha)) {
            String tipoUsuario = email.contains("@aluno") ? "aluno" : "professor";
            
            JOptionPane.showMessageDialog(this, 
                "Bem-vindo, " + tipoUsuario + "!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Redireciona conforme o tipo de usuário
            if (tipoUsuario.equals("aluno")) {
                new TelaQuiz().setVisible(true);
            } else {
                new TelaProfessor().setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "E-mail ou senha incorretos", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Classes internas para componentes customizados
    class RoundJTextField extends JTextField {
        private int radius;

        public RoundJTextField(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            super.paintComponent(g2);
            g2.dispose();
        }

        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();
        }
    }

    class RoundJPasswordField extends JPasswordField {
        private int radius;

        public RoundJPasswordField(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            super.paintComponent(g2);
            g2.dispose();
        }

        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();
        }
    }

    class RoundJButton extends JButton {
        private int radius;

        public RoundJButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            super.paintComponent(g2);
            g2.dispose();
        }

        protected void paintBorder(Graphics g) {
            // Sem borda
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaLogin tela = new TelaLogin();
            tela.setVisible(true);
        });
    }
}