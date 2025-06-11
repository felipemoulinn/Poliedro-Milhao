package com.example.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConexaoBD {
    private String host = "localhost";
    private String port = "3306";
    private String db = "quiz_educacional_db";
    private String user = "root";
    private String password = "banana";

    public Connection obterConexao() throws Exception {
        String url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                host, port, db);
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Verifica o login do usuário e retorna o tipo de usuário se válido.
     * 
     * @return "admin", "professor", "aluno" ou null
     */
    public String verificarLogin(String email, String senha) {
        String sql = "SELECT tipo FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection conexao = obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha); // ⚠️ Em produção, utilize hashing de senha (ex: BCrypt)

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipo");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar login:");
            e.printStackTrace();
        }

        return null; // Login inválido
    }

    public static void main(String[] args) {
        try {
            ConexaoBD fabricaDeConexoes = new ConexaoBD();
            Connection conexao = fabricaDeConexoes.obterConexao();

            if (conexao != null && !conexao.isClosed()) {
                System.out.println("✅ Conexão com o banco de dados estabelecida com sucesso!");
                conexao.close();
            } else {
                System.out.println("❌ Falha ao conectar ao banco de dados.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao banco de dados:");
            e.printStackTrace();
        }
    }

    public int obterIdUsuario(String email) {
        String sql = "SELECT id FROM usuarios WHERE email = ?";

        try (Connection conexao = obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter ID do usuário:");
            e.printStackTrace();
        }

        return -1; // Retorna -1 se não encontrado ou erro
    }
    public boolean verificarQuestoesDisponiveis(String dificuldade) {
        String sql = "SELECT COUNT(*) FROM perguntas WHERE nivel_dificuldade = ?";

        try (Connection conn = obterConexao();  // ← Usando seu método existente
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dificuldade);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar questões disponíveis:");
            e.printStackTrace();
        }

        return false;
    }
}
