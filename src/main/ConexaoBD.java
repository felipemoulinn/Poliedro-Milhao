package src.main;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexaoBD {
    private String host = "localhost";
    private String port = "3306";
    private String db = "quiz_educacional_db"; // Nome modificado para refletir o propósito
    private String user = "root";
    private String password = "imtdb"; // imtdb nas máquinas da Mauá
    
    public Connection obterConexao() throws Exception {
        // Registrar o driver explicitamente
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        String url = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
            host, port, db
        );
        
        return DriverManager.getConnection(url, user, password);
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