/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

/**
 *
 * @author frank
 */
public class Pacientes {
    private String documentoPaciente;
    private String nombre;
    private String tipoAfiliacion;
    private int edad;
    private String especialidad;  
    private String fecha;
    private double totalPagar;
    
    public Pacientes(){
        
    }
    
    //Constructor con todos los parametros
    public Pacientes(String documentoPaciente, String nombre, String tipoAfiliacion, int edad) {
        this.documentoPaciente = documentoPaciente;
        this.nombre = nombre;
        this.tipoAfiliacion = tipoAfiliacion;
        this.edad = edad;
        
    }
    
    //Getter and setters

    public String getDocumentoPaciente() {
        return documentoPaciente;
    }

    public void setDocumentoPaciente(String documentoPaciente) {
        this.documentoPaciente = documentoPaciente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoAfiliacion() {
        return tipoAfiliacion;
    }

    public void setTipoAfiliacion(String tipoAfiliacion) {
        this.tipoAfiliacion = tipoAfiliacion;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
    
    public String getEspecialidad() { 
        return especialidad; 
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public double getTotalPagar() { 
        return totalPagar; 
    }
    public void setTotalPagar(double totalPagar) { 
        this.totalPagar = totalPagar; 
    }
    
}
