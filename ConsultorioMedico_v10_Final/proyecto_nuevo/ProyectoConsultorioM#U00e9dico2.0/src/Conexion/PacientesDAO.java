package Conexion;
import java.sql.*;
import java.util.*;

public class PacientesDAO {
    ConexionBase conectar = new ConexionBase();

    /**
     * Lista TODOS los pacientes registrados.
     * Si tiene cita muestra especialidad, fecha y copago.
     * Si no tiene cita muestra "Sin cita" en esos campos.
     */
    public List<Pacientes> listar() {
        List<Pacientes> datos = new ArrayList<>();
        try {
            Connection con = conectar.getConnection();
            if (con == null) return datos;

            // LEFT JOIN para que aparezcan TODOS aunque no tengan cita
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT p.documento_paciente, p.nombre, p.tipo_afiliacion, p.edad, " +
                "COALESCE(e.nombre_especialidad, 'Sin cita') AS especialidad, " +
                "COALESCE(e.costo_base, 0) AS costo_base, " +
                "COALESCE(c.fecha, '—') AS fecha " +
                "FROM pacientes p " +
                "LEFT JOIN citas c ON p.documento_paciente = c.documento_paciente " +
                "LEFT JOIN especialidad e ON c.id_especialidad = e.id_especialidad " +
                "ORDER BY p.nombre ASC");

            while (rs.next()) {
                Pacientes pac = new Pacientes();
                pac.setDocumentoPaciente(rs.getString("documento_paciente"));
                pac.setNombre(rs.getString("nombre"));
                pac.setTipoAfiliacion(rs.getString("tipo_afiliacion"));
                pac.setEdad(rs.getInt("edad"));
                pac.setEspecialidad(rs.getString("especialidad"));
                pac.setFecha(rs.getString("fecha"));
                double costo = rs.getDouble("costo_base");
                double total = costo == 0 ? 0 :
                    pac.getTipoAfiliacion().equalsIgnoreCase("Subsidiado") ? costo * 0.5 : costo;
                pac.setTotalPagar(total);
                datos.add(pac);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return datos;
    }

    /** Inserta SOLO el paciente — sin cita automática */
    public int insertar(Pacientes pac) {
        try {
            Connection con = conectar.getConnection();
            if (con == null) return 0;

            // Verificar si ya existe
            PreparedStatement check = con.prepareStatement(
                "SELECT COUNT(*) FROM pacientes WHERE documento_paciente = ?");
            check.setString(1, pac.getDocumentoPaciente());
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return -1; // ya existe

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO pacientes(documento_paciente, nombre, tipo_afiliacion, edad) " +
                "VALUES(?, ?, ?, ?)");
            ps.setString(1, pac.getDocumentoPaciente());
            ps.setString(2, pac.getNombre());
            ps.setString(3, pac.getTipoAfiliacion());
            ps.setInt(4, pac.getEdad());
            return ps.executeUpdate();

        } catch (Exception e) { e.printStackTrace(); return 0; }
    }

    public int eliminar(String documento) {
        try {
            Connection con = conectar.getConnection();
            if (con == null) return 0;
            con.createStatement().executeUpdate(
                "DELETE FROM citas WHERE documento_paciente='" + documento + "'");
            return con.createStatement().executeUpdate(
                "DELETE FROM pacientes WHERE documento_paciente='" + documento + "'");
        } catch (Exception e) { e.printStackTrace(); return 0; }
    }

    public List<String> listarEspecialidades() {
        List<String> datos = new ArrayList<>();
        try {
            Connection con = conectar.getConnection();
            if (con == null) return datos;
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT nombre_especialidad FROM especialidad ORDER BY id_especialidad");
            while (rs.next()) datos.add(rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
        return datos;
    }

    public List<Pacientes> listarPorFecha(String desde, String hasta) {
        List<Pacientes> datos = new ArrayList<>();
        try {
            Connection con = conectar.getConnection();
            if (con == null) return datos;
            PreparedStatement ps = con.prepareStatement(
                "SELECT p.documento_paciente, p.nombre, p.tipo_afiliacion, p.edad, " +
                "e.nombre_especialidad, e.costo_base, c.fecha " +
                "FROM pacientes p " +
                "JOIN citas c ON p.documento_paciente = c.documento_paciente " +
                "JOIN especialidad e ON c.id_especialidad = e.id_especialidad " +
                "WHERE c.fecha BETWEEN ? AND ? ORDER BY c.fecha ASC");
            ps.setString(1, desde); ps.setString(2, hasta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pacientes pac = new Pacientes();
                pac.setDocumentoPaciente(rs.getString("documento_paciente"));
                pac.setNombre(rs.getString("nombre"));
                pac.setTipoAfiliacion(rs.getString("tipo_afiliacion"));
                pac.setEdad(rs.getInt("edad"));
                pac.setEspecialidad(rs.getString("nombre_especialidad"));
                pac.setFecha(rs.getString("fecha"));
                double costo = rs.getDouble("costo_base");
                pac.setTotalPagar(pac.getTipoAfiliacion().equalsIgnoreCase("Subsidiado")
                    ? costo * 0.5 : costo);
                datos.add(pac);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return datos;
    }
}
