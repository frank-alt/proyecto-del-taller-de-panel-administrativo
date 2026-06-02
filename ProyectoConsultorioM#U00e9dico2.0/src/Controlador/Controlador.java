package Controlador;

import Vista.InterfazGrafica;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import Conexion.Pacientes;
import Conexion.PacientesDAO;

public class Controlador {

    PacientesDAO dao   = new PacientesDAO();
    Pacientes    p     = new Pacientes();
    InterfazGrafica v;
    DefaultTableModel modelo = new DefaultTableModel();

    public Controlador(InterfazGrafica vista) {
        this.v = vista;
        cargarEspecialidades();
    

        v.btnAgregar .addActionListener(e -> agregar());
        v.btnEliminar.addActionListener(e -> eliminar());
        v.btnLimpiar .addActionListener(e -> limpiar());
        v.btnListar  .addActionListener(e -> {
            // Limpiar filtros y mostrar todos
            v.txtFechaDesde.setText("");
            v.txtFechaHasta.setText("");
            listar(v.tabla);
        });

        // Búsqueda por fecha al presionar el botón buscar
        v.btnBuscarFecha.addActionListener(e -> buscarPorFecha());
    }

    private void cargarEspecialidades() {
        v.jcbEspecialidad.removeAllItems();
        List<String> especialidades = dao.listarEspecialidades();
        for (String esp : especialidades) {
            v.jcbEspecialidad.addItem(esp);
        }
    }

    public void listar(JTable tabla) {
        modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new String[]{
            "Documento", "Nombre", "Afiliación", "Edad",
            "Especialidad", "Fecha", "Total a Pagar"});
        modelo.setRowCount(0);

        List<Pacientes> lista = dao.listar();
        for (Pacientes pac : lista) {
            modelo.addRow(new Object[]{
                pac.getDocumentoPaciente(),
                pac.getNombre(),
                pac.getTipoAfiliacion(),
                pac.getEdad(),
                pac.getEspecialidad(),
                pac.getFecha(),
                String.format("$%.0f", pac.getTotalPagar())
            });
        }
        // Animar filas una a una (efecto visual de carga)
        v.animarTabla(modelo);
        v.txtTotalPacientes.setText(String.valueOf(lista.size()));
    }

    /**
     * Filtra la tabla mostrando solo los pacientes cuya fecha de cita
     * esté dentro del rango [desde, hasta].
     * Formato esperado: YYYY-MM-DD  (o parcial como YYYY-MM)
     */
    private void buscarPorFecha() {
    String desde = v.txtFechaDesde.getText().trim();
    String hasta = v.txtFechaHasta.getText().trim();

    if (desde.isEmpty() && hasta.isEmpty()) {
        listar(v.tabla);
        return;
    }

    if (!desde.isEmpty() && !desde.matches("\\d{4}-\\d{2}-\\d{2}")) {
        javax.swing.JOptionPane.showMessageDialog(v,
            "Formato incorrecto en Desde.\nUse YYYY-MM-DD  ej: 2026-01-15",
            "Formato inválido", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (!hasta.isEmpty() && !hasta.matches("\\d{4}-\\d{2}-\\d{2}")) {
        javax.swing.JOptionPane.showMessageDialog(v,
            "Formato incorrecto en Hasta.\nUse YYYY-MM-DD  ej: 2026-03-31",
            "Formato inválido", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (desde.isEmpty()) desde = "0001-01-01";
    if (hasta.isEmpty()) hasta  = "9999-12-31";

    // ✅ imprimir en consola para verificar qué envía
    System.out.println("Buscando desde: " + desde + " hasta: " + hasta);

    List<Pacientes> lista = dao.listarPorFecha(desde, hasta);

    // ✅ imprimir cuántos resultados trae
    System.out.println("Resultados encontrados: " + lista.size());

    DefaultTableModel modeloBusqueda = new DefaultTableModel();
    modeloBusqueda.setColumnIdentifiers(new String[]{
        "Documento", "Nombre", "Afiliación", "Edad",
        "Especialidad", "Fecha", "Total a Pagar"
    });
    modeloBusqueda.setRowCount(0);

    for (Pacientes pac : lista) {
        // ✅ imprimir cada fecha que trae
        System.out.println("Fecha paciente: " + pac.getFecha());
        modeloBusqueda.addRow(new Object[]{
            pac.getDocumentoPaciente(),
            pac.getNombre(),
            pac.getTipoAfiliacion(),
            pac.getEdad(),
            pac.getEspecialidad(),
            pac.getFecha(),
            String.format("$%.0f", pac.getTotalPagar())
        });
    }

    // ✅ setModel directo sin animación para la búsqueda
    v.tabla.setModel(modeloBusqueda);
    v.txtTotalPacientes.setText(String.valueOf(lista.size()));

    if (lista.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(v,
            "No hay pacientes con citas en ese rango de fechas.",
            "Sin resultados", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
    private void agregar() {
        if (v.txtNombre.getText().trim().isEmpty() ||
            v.txtDocumento.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(v,
                "Nombre y Documento son obligatorios");
            return;
        }

        String afiliacion;
        if (v.rbtContributivo.isSelected()) {
            afiliacion = "Contributivo";
        } else if (v.rbtSubsidiado.isSelected()) {
            afiliacion = "Subsidiado";
        } else {
            javax.swing.JOptionPane.showMessageDialog(v,
                "Seleccione tipo de afiliación");
            return;
        }

        try {
            int idEspecialidad = v.jcbEspecialidad.getSelectedIndex() + 1;
            p.setDocumentoPaciente(v.txtDocumento.getText().trim());
            p.setNombre(v.txtNombre.getText().trim());
            p.setTipoAfiliacion(afiliacion);
            p.setEdad(Integer.parseInt(v.txtEdad.getText().trim()));

            int r = dao.insertar(p, idEspecialidad);
            if (r == 1) {
                javax.swing.JOptionPane.showMessageDialog(v,
                    "✅  Paciente agregado con éxito");
                listar(v.tabla);
                limpiar();
            } else {
                javax.swing.JOptionPane.showMessageDialog(v,
                    "No se pudo agregar el paciente.");
            }
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(v,
                "La edad debe ser un número válido.");
        }
    }

    private void eliminar() {
        int fila = v.tabla.getSelectedRow();
        if (fila == -1) {
            javax.swing.JOptionPane.showMessageDialog(v,
                "Selecciona un paciente de la tabla primero.");
            return;
        }
        String documento = v.tabla.getModel().getValueAt(fila, 0).toString();
        int confirm = javax.swing.JOptionPane.showConfirmDialog(v,
            "¿Deseas eliminar al paciente con documento: " + documento + "?",
            "Confirmar eliminación",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE);

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            int r = dao.eliminar(documento);
            if (r == 1) {
                javax.swing.JOptionPane.showMessageDialog(v, "✅  Paciente eliminado");
                listar(v.tabla);
            } else {
                javax.swing.JOptionPane.showMessageDialog(v, "No se pudo eliminar.");
            }
        }
    }

    private void limpiar() {
        v.txtNombre.setText("");
        v.txtDocumento.setText("");
        v.txtEdad.setText("");
        v.rbtContributivo.setSelected(false);
        v.rbtSubsidiado.setSelected(false);
        v.txtNombre.requestFocus();
    }
}
