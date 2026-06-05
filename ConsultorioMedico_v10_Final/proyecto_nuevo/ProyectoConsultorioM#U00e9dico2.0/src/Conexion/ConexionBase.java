package Conexion;
import java.sql.*;

public class ConexionBase {

    // Una sola conexión para todo el programa (Singleton)
    private static Connection con = null;
    private static final String URL =
        "jdbc:sqlite:C:\\Users\\frank\\OneDrive\\Documentos\\SQLte e-193\\baseConsultorio.db";

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection(URL);
                // Evitar bloqueos: modo WAL permite lecturas y escrituras simultáneas
                con.createStatement().execute("PRAGMA journal_mode=WAL;");
                con.createStatement().execute("PRAGMA busy_timeout=5000;");
                con.setAutoCommit(true);
                System.out.println("Conexion establecida correctamente");
            }
        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
        return con;
    }
}
