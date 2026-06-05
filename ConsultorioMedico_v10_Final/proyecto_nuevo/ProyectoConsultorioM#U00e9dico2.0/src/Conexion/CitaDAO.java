package Conexion;
import java.sql.*;
import java.util.*;

public class CitaDAO {
    ConexionBase conectar = new ConexionBase();

    /** Busca nombre del paciente por documento */
    public String buscarNombrePorDocumento(String documento) {
        try {
            Connection con = conectar.getConnection();
            if (con == null) return null;
            PreparedStatement ps = con.prepareStatement(
                "SELECT nombre FROM pacientes WHERE documento_paciente = ?");
            ps.setString(1, documento.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nombre");
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Lista todas las citas con JOIN a pacientes y especialidad */
    public List<Cita> listarTodas() {
        List<Cita> lista = new ArrayList<>();
        try {
            Connection con = conectar.getConnection();
            if (con == null) return lista;
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT c.id_cita, c.documento_paciente, p.nombre, " +
                "c.fecha, e.nombre_especialidad, c.id_especialidad, c.valor_copago " +
                "FROM citas c " +
                "JOIN pacientes   p ON c.documento_paciente = p.documento_paciente " +
                "JOIN especialidad e ON c.id_especialidad   = e.id_especialidad " +
                "ORDER BY c.fecha DESC");
            while (rs.next()) {
                Cita ct = new Cita();
                ct.setIdCita(rs.getInt("id_cita"));
                ct.setDocumentoPaciente(rs.getString("documento_paciente"));
                ct.setNombrePaciente(rs.getString("nombre"));
                ct.setFecha(rs.getString("fecha"));
                ct.setEspecialidad(rs.getString("nombre_especialidad"));
                ct.setIdEspecialidad(rs.getInt("id_especialidad"));
                ct.setValorCopago(rs.getDouble("valor_copago"));
                lista.add(ct);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    /** Agenda cita — consultas separadas para evitar errores de JOIN en SQLite */
    public int agendar(String documento, String fecha, int idEspecialidad) {
        try {
            Connection con = conectar.getConnection();
            if (con == null) { System.out.println("Sin conexión BD"); return 0; }

            // 1. Obtener costo_base de la especialidad
            PreparedStatement ps1 = con.prepareStatement(
                "SELECT costo_base FROM especialidad WHERE id_especialidad = ?");
            ps1.setInt(1, idEspecialidad);
            ResultSet rs1 = ps1.executeQuery();
            double costo = 0;
            if (rs1.next()) {
                costo = rs1.getDouble("costo_base");
                System.out.println("Costo base especialidad " + idEspecialidad + ": " + costo);
            } else {
                System.out.println("No se encontró especialidad con id=" + idEspecialidad);
                return 0;
            }

            // 2. Obtener tipo_afiliacion del paciente
            PreparedStatement ps2 = con.prepareStatement(
                "SELECT tipo_afiliacion FROM pacientes WHERE documento_paciente = ?");
            ps2.setString(1, documento.trim());
            ResultSet rs2 = ps2.executeQuery();
            String afil = "Contributivo";
            if (rs2.next()) {
                afil = rs2.getString("tipo_afiliacion");
                System.out.println("Afiliación paciente " + documento + ": " + afil);
            } else {
                System.out.println("No se encontró paciente con doc=" + documento);
                return 0;
            }

            // 3. Calcular copago
            double copago = afil.equalsIgnoreCase("Subsidiado") ? costo * 0.5 : costo;
            System.out.println("Copago a insertar: " + copago);

            // 4. Insertar la cita
            PreparedStatement ps3 = con.prepareStatement(
                "INSERT INTO citas(fecha, valor_copago, documento_paciente, id_especialidad) " +
                "VALUES(?, ?, ?, ?)");
            ps3.setString(1, fecha);
            ps3.setDouble(2, copago);
            ps3.setString(3, documento.trim());
            ps3.setInt(4, idEspecialidad);
            int r = ps3.executeUpdate();
            System.out.println("Cita insertada: " + r);
            return r;

        } catch (Exception e) { e.printStackTrace(); return 0; }
    }

    public int eliminar(int idCita) {
        try {
            Connection con = conectar.getConnection();
            if (con == null) return 0;
            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM citas WHERE id_cita = ?");
            ps.setInt(1, idCita);
            return ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); return 0; }
    }

    /** Devuelve [id, nombre] de cada especialidad para el combo */
    public List<String[]> listarEspecialidadesConId() {
        List<String[]> lista = new ArrayList<>();
        try {
            Connection con = conectar.getConnection();
            if (con == null) return lista;
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT id_especialidad, nombre_especialidad FROM especialidad ORDER BY id_especialidad");
            while (rs.next())
                lista.add(new String[]{rs.getString(1), rs.getString(2)});
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}
