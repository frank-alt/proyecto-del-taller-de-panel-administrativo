package Conexion;
import java.sql.*;

public class ConexionBase {
    Statement consulta;
    Connection con;

    // Ruta relativa: el archivo .db debe estar en la raiz del proyecto
    String ruta = "C:\\Users\\frank\\OneDrive\\Documentos\\SQLte e-193\\baseConsultorio.db";
    String url = "jdbc:sqlite:" + ruta;

    public Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(url);
            consulta = con.createStatement();
            System.out.println("Conexion exitosa");
        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
        return con;
    }
}
