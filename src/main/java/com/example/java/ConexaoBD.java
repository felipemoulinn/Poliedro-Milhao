package com.example.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConexaoBD {
    private String host = "localhost";
    private String port = "3306";
    private String db = "quiz_educacional_db"; // Nome modificado para refletir o propósito
    private String user = "root";
    private String password = "imtdb"; // imtdb nas máquinas da Mauá
    
    public Connection obterConexao() throws Exception {
        // Registrar o driver explicitamente
        //Class.forName("com.mysql.cj.jdbc.Driver");
        
        String url = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
            host, port, db
        );
        
        return DriverManager.getConnection(url, user, password);
    }
    
    // Adicione este método à classe ConexaoBD
    public boolean verificarLogin(String email, String senha) {
        String sql = "SELECT id FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection conexao = obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha); // Na prática, você deve usar hash da senha

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrou um usuário com essas credenciais
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar login:");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            // Teste de conexão
            ConexaoBD fabricaDeConexoes = new ConexaoBD();
            Connection conexao = fabricaDeConexoes.obterConexao();
            
            if(conexao != null && !conexao.isClosed()){
                System.out.println("Conexão com o banco de dados estabelecida com sucesso!");
                conexao.close();
            } else {
                System.out.println("Falha ao conectar ao banco de dados.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao banco de dados:");
            e.printStackTrace();
        }
    }
}

