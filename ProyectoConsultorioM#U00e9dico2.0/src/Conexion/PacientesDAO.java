/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frank
 */
public class PacientesDAO {
    PreparedStatement ps;
    ResultSet rs;
    Connection con;
    ConexionBase conectar = new ConexionBase();
    Pacientes p = new Pacientes();
    
    public List<Pacientes> listar() {
    List<Pacientes> datos = new ArrayList<>();
    try {
        con = conectar.getConnection();
        String sql = "SELECT p.documento_paciente, p.nombre, p.tipo_afiliacion, " +
                     "p.edad, e.nombre_especialidad, e.costo_base, " +
                     "c.fecha " +
                     "FROM pacientes p " +
                     "JOIN citas c ON p.documento_paciente = c.documento_paciente " +
                     "JOIN especialidad e ON c.id_especialidad = e.id_especialidad";
    
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();
        
        while (rs.next()) {
            Pacientes pac = new Pacientes();
            pac.setDocumentoPaciente(rs.getString("documento_paciente"));
            pac.setNombre(rs.getString("nombre"));
            pac.setTipoAfiliacion(rs.getString("tipo_afiliacion"));
            pac.setEdad(rs.getInt("edad"));
            pac.setEspecialidad(rs.getString("nombre_especialidad"));
            pac.setFecha(rs.getString("fecha"));
        
            double costo = rs.getDouble("costo_base");
            String afiliacion = rs.getString("tipo_afiliacion");
            double totalPagar;
            if (afiliacion.equals("Subsidiado")) {
                totalPagar = costo * 0.50;
            } 
            else {
                totalPagar = costo;   
            }

            pac.setTotalPagar(totalPagar);
            datos.add(pac);
        }
    }
    catch (Exception e) {
        e.printStackTrace();
    }
        return datos;
    }
    
    public int insertar(Pacientes pac, int idEspecialidad) {
    int r = 0;
    try {
        con = conectar.getConnection();

        String sqlPaciente = "INSERT INTO pacientes(documento_paciente, nombre, tipo_afiliacion, edad) " +
                             "VALUES(?,?,?,?)";
        ps = con.prepareStatement(sqlPaciente);
        ps.setString(1, pac.getDocumentoPaciente());
        ps.setString(2, pac.getNombre());
        ps.setString(3, pac.getTipoAfiliacion());
        ps.setInt(4, pac.getEdad());
        r = ps.executeUpdate();

        
        if (r == 1) {
            String sqlCita = "INSERT INTO citas(fecha, valor_copago, documento_paciente, id_especialidad) " +
                             "VALUES(date('now'), 0, ?, ?)";
            ps = con.prepareStatement(sqlCita);
            ps.setString(1, pac.getDocumentoPaciente());
            ps.setInt(2, idEspecialidad);
            ps.executeUpdate();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return r;
    }

    public int eliminar(String documento) {
        int r = 0;
        try {
            con = conectar.getConnection();

           
            String sqlCita = "DELETE FROM citas WHERE documento_paciente = ?";
            ps = con.prepareStatement(sqlCita);
            ps.setString(1, documento);
            ps.executeUpdate();

            String sqlPaciente = "DELETE FROM pacientes WHERE documento_paciente = ?";
            ps = con.prepareStatement(sqlPaciente);
            ps.setString(1, documento);
            r = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }
    
    public List<String> listarEspecialidades() {
    List<String> datos = new ArrayList<>();
    try {
        con = conectar.getConnection();
        ps = con.prepareStatement("SELECT nombre_especialidad FROM especialidad");
        rs = ps.executeQuery();
        while (rs.next()) {
            datos.add(rs.getString("nombre_especialidad"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return datos;
    }
    
   public List<Pacientes> listarPorFecha(String desde, String hasta) {
    List<Pacientes> datos = new ArrayList<>();
    try {
        con = conectar.getConnection();
        
        // ✅ verifica la conexión
        System.out.println("Conexión: " + (con != null ? "OK" : "NULL"));
        
        String sql = "SELECT p.documento_paciente, p.nombre, p.tipo_afiliacion, " +
                     "p.edad, e.nombre_especialidad, e.costo_base, c.fecha " +
                     "FROM pacientes p " +
                     "JOIN citas c ON p.documento_paciente = c.documento_paciente " +
                     "JOIN especialidad e ON c.id_especialidad = e.id_especialidad " +
                     "WHERE c.fecha BETWEEN ? AND ? " +
                     "ORDER BY c.fecha ASC";
                     
        System.out.println("SQL: " + sql);
        System.out.println("Desde: " + desde + " Hasta: " + hasta);
        
        ps = con.prepareStatement(sql);
        ps.setString(1, desde);
        ps.setString(2, hasta);
        rs = ps.executeQuery();
        
        int contador = 0;
        while (rs.next()) {
            contador++;
            Pacientes pac = new Pacientes();
            pac.setDocumentoPaciente(rs.getString("documento_paciente"));
            pac.setNombre(rs.getString("nombre"));
            pac.setTipoAfiliacion(rs.getString("tipo_afiliacion"));
            pac.setEdad(rs.getInt("edad"));
            pac.setEspecialidad(rs.getString("nombre_especialidad"));
            pac.setFecha(rs.getString("fecha"));
            double costo = rs.getDouble("costo_base");
            pac.setTotalPagar(
                rs.getString("tipo_afiliacion").equals("Subsidiado")
                ? costo * 0.50 : costo
            );
            datos.add(pac);
            System.out.println("Fila " + contador + ": " + pac.getNombre() + " - " + pac.getFecha());
        }
        System.out.println("Total encontrado: " + contador);
        
    } catch (Exception e) {
        e.printStackTrace(); // ✅ muestra el error completo
    }
    return datos;
}
    
}
