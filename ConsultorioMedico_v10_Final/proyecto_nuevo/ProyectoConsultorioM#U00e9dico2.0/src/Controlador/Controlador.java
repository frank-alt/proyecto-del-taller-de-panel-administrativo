package Controlador;

import Conexion.*;
import Vista.InterfazGrafica;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Controlador {

    private final PacientesDAO daoPac  = new PacientesDAO();
    private final CitaDAO      daoCita = new CitaDAO();
    private final InterfazGrafica v;
    private String documentoBuscado = "";

    public Controlador(InterfazGrafica vista) {
        this.v = vista;
        cargarEspecialidades();
        cargarEspCita();

        // Menú 1: Registrar
        v.btnAgregar    .addActionListener(e -> registrarPaciente());
        v.btnLimpiarReg .addActionListener(e -> limpiarRegistro());

        // Menú 2: Listar
        v.btnListar     .addActionListener(e -> listarPacientes());
        v.btnEliminar   .addActionListener(e -> eliminarPaciente());

        // Menú 3: Agendar
        v.btnBuscarPaciente.addActionListener(e -> buscarPaciente());
        v.btnAgendarCita   .addActionListener(e -> agendarCita());
        v.btnEliminarCita  .addActionListener(e -> eliminarCita());
        v.btnListarCitas   .addActionListener(e -> listarCitas());

        // Menú 4: Buscar por fecha
        v.btnBuscarFecha.addActionListener(e -> buscarPorFecha());
        v.btnVerTodas   .addActionListener(e -> verTodasFechas());
    }

    // ── Especialidades ────────────────────────────────────────────────────────
    private void cargarEspecialidades() {
        v.jcbEspecialidad.removeAllItems();
        for (String e : daoPac.listarEspecialidades()) v.jcbEspecialidad.addItem(e);
    }

    private void cargarEspCita() {
        v.jcbEspCita.removeAllItems();
        for (String[] e : daoCita.listarEspecialidadesConId()) v.jcbEspCita.addItem(e[1]);
    }

    // =========================================================================
    //  MENÚ 1: REGISTRAR
    // =========================================================================
    private void registrarPaciente() {
        String nombre = v.txtNombre.getText().trim();
        String doc    = v.txtDocumento.getText().trim();
        String edStr  = v.txtEdad.getText().trim();

        if (nombre.isEmpty() || doc.isEmpty() || edStr.isEmpty()) {
            msg("⚠️  Todos los campos son obligatorios."); return; }

        String afil;
        if      (v.rbtContributivo.isSelected()) afil = "Contributivo";
        else if (v.rbtSubsidiado.isSelected())   afil = "Subsidiado";
        else { msg("⚠️  Selecciona el tipo de afiliación."); return; }

        try {
            int edad = Integer.parseInt(edStr);
            if (edad <= 0 || edad > 120) { msg("⚠️  Ingresa una edad válida."); return; }
            Pacientes pac = new Pacientes();
            pac.setDocumentoPaciente(doc);
            pac.setNombre(nombre);
            pac.setTipoAfiliacion(afil);
            pac.setEdad(edad);
            int r = daoPac.insertar(pac);
            if (r == 1) { msg("✅  Paciente registrado con éxito.\n\nAhora puedes ir a 'Agendar Cita' para asignarle una cita."); limpiarRegistro(); }
            else         msg("❌  No se pudo registrar. El documento ya puede existir.");
        } catch (NumberFormatException ex) { msg("⚠️  La edad debe ser un número."); }
    }

    private void limpiarRegistro() {
        v.txtNombre.setText(""); v.txtDocumento.setText(""); v.txtEdad.setText("");
        v.rbtContributivo.setSelected(false); v.rbtSubsidiado.setSelected(false);
        v.txtNombre.requestFocus();
    }

    // =========================================================================
    //  MENÚ 2: LISTAR PACIENTES
    // =========================================================================
    private void listarPacientes() {
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new String[]{"Documento","Nombre","Afiliación","Edad","Especialidad","Fecha","Total a Pagar"});
        for (Pacientes pac : daoPac.listar()) {
            String total = pac.getTotalPagar() == 0 ? "Sin cita"
                : String.format("$%.0f", pac.getTotalPagar());
            m.addRow(new Object[]{
                pac.getDocumentoPaciente(), pac.getNombre(), pac.getTipoAfiliacion(),
                pac.getEdad(), pac.getEspecialidad(), pac.getFecha(), total});
        }
        v.animarTabla(m);
        v.txtTotalPacientes.setText(String.valueOf(m.getRowCount()));
    }

    private void eliminarPaciente() {
        int fila = v.tabla.getSelectedRow();
        if (fila < 0) { msg("Selecciona un paciente de la tabla primero."); return; }
        String doc = v.tabla.getModel().getValueAt(fila,0).toString();
        String nom = v.tabla.getModel().getValueAt(fila,1).toString();
        int ok = javax.swing.JOptionPane.showConfirmDialog(v,
            "¿Eliminar al paciente:\n" + nom + " (" + doc + ")?",
            "Confirmar eliminación", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            if (daoPac.eliminar(doc) == 1) { msg("✅  Paciente eliminado."); listarPacientes(); }
            else msg("❌  No se pudo eliminar.");
        }
    }

    // =========================================================================
    //  MENÚ 3: AGENDAR CITA
    // =========================================================================
    private void buscarPaciente() {
        String doc = v.txtBuscarDoc.getText().trim();
        if (doc.isEmpty()) { msg("Escribe el número de documento."); return; }
        String nombre = daoCita.buscarNombrePorDocumento(doc);
        if (nombre != null) {
            documentoBuscado = doc;
            v.lblNombreEncontrado.setText("✅  " + nombre);
            v.lblNombreEncontrado.setForeground(new Color(0x00C9A7));
            v.lblNombreEncontrado.setFont(new Font("Segoe UI", Font.BOLD, 12));
            listarCitas();
            javax.swing.Timer t = new javax.swing.Timer(600, e -> v.resaltarDocumentoEnTabla(doc));
            t.setRepeats(false); t.start();
        } else {
            documentoBuscado = "";
            v.lblNombreEncontrado.setText("❌  Paciente no encontrado — regístralo primero en 'Registrar Paciente'");
            v.lblNombreEncontrado.setForeground(new Color(0xFF5A5F));
            v.lblNombreEncontrado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            v.resaltarDocumentoEnTabla("");
        }
    }

    private void agendarCita() {
        if (documentoBuscado.isEmpty()) {
            msg("⚠️  Primero busca un paciente válido por documento."); return; }

        // Construir fecha desde los tres combos
        String anio = (String) v.cboAnio.getSelectedItem();
        String mes  = (String) v.cboMes.getSelectedItem();
        String dia  = (String) v.cboDia.getSelectedItem();

        if (anio == null || anio.equals("Año")) { msg("⚠️  Selecciona el año de la cita.");  return; }
        if (mes  == null || mes.equals("Mes"))  { msg("⚠️  Selecciona el mes de la cita.");   return; }
        if (dia  == null || dia.equals("Día"))  { msg("⚠️  Selecciona el día de la cita.");   return; }

        // Extraer número de mes (ej: "06-Jun" -> "06")
        String numMes = mes.substring(0, 2);
        String fecha  = anio + "-" + numMes + "-" + dia;
        System.out.println("Fecha armada: " + fecha);

        if (v.jcbEspCita.getSelectedIndex() < 0) { msg("⚠️  Selecciona una especialidad."); return; }

        List<String[]> lista = daoCita.listarEspecialidadesConId();
        if (lista.isEmpty()) { msg("❌  No se pudieron cargar las especialidades."); return; }
        int idEsp = Integer.parseInt(lista.get(v.jcbEspCita.getSelectedIndex())[0]);
        System.out.println("ID Especialidad: " + idEsp + " | Doc: " + documentoBuscado);

        int r = daoCita.agendar(documentoBuscado, fecha, idEsp);
        if (r == 1) {
            msg("✅  Cita agendada exitosamente para el " + dia + "/" + numMes + "/" + anio + ".");
            listarCitas();
            v.txtBuscarDoc.setText("");
            v.cboAnio.setSelectedIndex(0); v.cboMes.setSelectedIndex(0); v.cboDia.setSelectedIndex(0);
            v.lblNombreEncontrado.setText("— Sin paciente seleccionado —");
            v.lblNombreEncontrado.setForeground(new java.awt.Color(0x7A9BB5));
            v.lblNombreEncontrado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            documentoBuscado = "";
        } else {
            msg("❌  No se pudo agendar la cita.\nRevisa la consola de NetBeans para ver el error exacto.");
        }
    }

    private void eliminarCita() {
        int fila = v.tablaCitas.getSelectedRow();
        if (fila < 0) { msg("Selecciona una cita de la tabla primero."); return; }
        int idCita = Integer.parseInt(v.tablaCitas.getModel().getValueAt(fila,0).toString());
        String pac = v.tablaCitas.getModel().getValueAt(fila,2).toString();
        int ok = javax.swing.JOptionPane.showConfirmDialog(v,
            "¿Eliminar la cita de " + pac + "?", "Confirmar",
            javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            if (daoCita.eliminar(idCita) == 1) { msg("✅  Cita eliminada."); listarCitas(); }
            else msg("❌  No se pudo eliminar.");
        }
    }

    public void listarCitas() {
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new String[]{"ID","Documento","Paciente","Fecha","Especialidad","Copago"});
        for (Cita c : daoCita.listarTodas())
            m.addRow(new Object[]{c.getIdCita(),c.getDocumentoPaciente(),c.getNombrePaciente(),
                c.getFecha(),c.getEspecialidad(),String.format("$%.0f",c.getValorCopago())});
        v.animarTablaCitas(m);
    }

    // =========================================================================
    //  MENÚ 4: BUSCAR POR FECHA
    // =========================================================================
    private void buscarPorFecha() {
        String desde = v.txtFechaDesde.getText().trim();
        String hasta = v.txtFechaHasta.getText().trim();
        if (desde.isEmpty() && hasta.isEmpty()) { verTodasFechas(); return; }
        if (!desde.isEmpty() && !desde.matches("\\d{4}-\\d{2}-\\d{2}")) {
            msg("⚠️  Formato incorrecto en Desde.\nUsa YYYY-MM-DD   ej: 2026-01-01"); return; }
        if (!hasta.isEmpty() && !hasta.matches("\\d{4}-\\d{2}-\\d{2}")) {
            msg("⚠️  Formato incorrecto en Hasta.\nUsa YYYY-MM-DD   ej: 2026-12-31"); return; }
        if (desde.isEmpty()) desde = "0001-01-01";
        if (hasta.isEmpty()) hasta = "9999-12-31";

        List<Pacientes> lista = daoPac.listarPorFecha(desde, hasta);
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new String[]{"Documento","Nombre","Afiliación","Edad","Especialidad","Fecha","Total a Pagar"});
        for (Pacientes pac : lista)
            m.addRow(new Object[]{pac.getDocumentoPaciente(),pac.getNombre(),pac.getTipoAfiliacion(),
                pac.getEdad(),pac.getEspecialidad(),pac.getFecha(),String.format("$%.0f",pac.getTotalPagar())});
        v.animarTablaFechas(m);
        if (lista.isEmpty()) msg("ℹ️  No hay citas registradas en ese rango de fechas.");
    }

    private void verTodasFechas() {
        List<Pacientes> lista = daoPac.listar();
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new String[]{"Documento","Nombre","Afiliación","Edad","Especialidad","Fecha","Total a Pagar"});
        for (Pacientes pac : lista)
            m.addRow(new Object[]{pac.getDocumentoPaciente(),pac.getNombre(),pac.getTipoAfiliacion(),
                pac.getEdad(),pac.getEspecialidad(),pac.getFecha(),String.format("$%.0f",pac.getTotalPagar())});
        v.animarTablaFechas(m);
    }

    private void msg(String text) {
        javax.swing.JOptionPane.showMessageDialog(v, text);
    }
}
