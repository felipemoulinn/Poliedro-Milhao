import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BANCO {
    public boolean verificarLogin(String email, String senha) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement stmt = conn.prepareCall("{call verificar_login(?, ?)}")) {

            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("sucesso") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}