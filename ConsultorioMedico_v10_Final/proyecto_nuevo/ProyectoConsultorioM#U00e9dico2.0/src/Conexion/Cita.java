package Conexion;

/** Modelo de una cita médica */
public class Cita {
    private int    idCita;
    private String documentoPaciente;
    private String nombrePaciente;
    private String fecha;
    private String especialidad;
    private int    idEspecialidad;
    private double valorCopago;

    public Cita() {}

    public int    getIdCita()              { return idCita; }
    public void   setIdCita(int v)         { idCita = v; }
    public String getDocumentoPaciente()   { return documentoPaciente; }
    public void   setDocumentoPaciente(String v) { documentoPaciente = v; }
    public String getNombrePaciente()      { return nombrePaciente; }
    public void   setNombrePaciente(String v)    { nombrePaciente = v; }
    public String getFecha()               { return fecha; }
    public void   setFecha(String v)       { fecha = v; }
    public String getEspecialidad()        { return especialidad; }
    public void   setEspecialidad(String v){ especialidad = v; }
    public int    getIdEspecialidad()      { return idEspecialidad; }
    public void   setIdEspecialidad(int v) { idEspecialidad = v; }
    public double getValorCopago()         { return valorCopago; }
    public void   setValorCopago(double v) { valorCopago = v; }
}
