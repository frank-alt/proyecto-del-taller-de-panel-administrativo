package Vista;

import Controlador.Controlador;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;

public class InterfazGrafica extends javax.swing.JFrame {

    // ── Paleta ────────────────────────────────────────────────────────────────
    static final Color BG         = new Color(0x0D1B2A);
    static final Color CARD       = new Color(0x16263E);
    static final Color SIDEBAR_BG = new Color(0x0A1628);
    static final Color ACCENT     = new Color(0x00C9A7);
    static final Color ACCENT_H   = new Color(0x00E5BF);
    static final Color ACCENT_P   = new Color(0x008F76);
    static final Color DANGER     = new Color(0xFF5A5F);
    static final Color DANGER_H   = new Color(0xFF7A7F);
    static final Color DANGER_P   = new Color(0xBB3A3E);
    static final Color WARN       = new Color(0xFFB627);
    static final Color WARN_H     = new Color(0xFFCA55);
    static final Color WARN_P     = new Color(0xBB8418);
    static final Color NEUTRAL    = new Color(0x1E3A5F);
    static final Color NEUTRAL_H  = new Color(0x2A4F7A);
    static final Color NEUTRAL_P  = new Color(0x122338);
    static final Color PURPLE     = new Color(0x7C5CBF);
    static final Color PURPLE_H   = new Color(0x9B7FD4);
    static final Color PURPLE_P   = new Color(0x5A3F99);
    static final Color TXT        = new Color(0xE8F4F8);
    static final Color MUTED      = new Color(0x7A9BB5);
    static final Color BORDER_C   = new Color(0x1E3A5F);
    static final Color ROW_ALT    = new Color(0x122033);

    // ── Públicos para Controlador ─────────────────────────────────────────────
    public JButton      btnAgregar, btnEliminar, btnLimpiar, btnListar;
    public JButton      btnBuscarFecha;                  // ← nuevo
    public JTextField   txtNombre, txtDocumento, txtEdad, txtTotalPacientes;
    public JTextField   txtFechaDesde, txtFechaHasta;    // ← nuevos
    public JComboBox<String> jcbEspecialidad;
    public JRadioButton rbtContributivo, rbtSubsidiado;
    public JTable       tabla;

    private ButtonGroup grupoAfiliacion;
    private Timer       clockTimer;
    private JPanel      contentPanel;
    private CardLayout  cardLayout;

    public InterfazGrafica() {
        initComponents();
    }

    private void initComponents() {
        setTitle("MediCare – Consultorio Médico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);                              // ← más ancho
        setMinimumSize(new Dimension(1100, 630));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);

        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(buildDashboardView(), "dashboard");
        contentPanel.add(buildPacientesView(), "pacientes");

        body.add(buildSidebar(), BorderLayout.WEST);
        body.add(contentPanel,  BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
    }

    // =========================================================================
    //  TOP BAR
    // =========================================================================
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SIDEBAR_BG);
        bar.setPreferredSize(new Dimension(0, 52));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C));

        JLabel logo = new JLabel("   🏥   MediCare  —  Sistema de Consultorio");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logo.setForeground(ACCENT);
        bar.add(logo, BorderLayout.WEST);

        JLabel clock = new JLabel();
        clock.setFont(new Font("Consolas", Font.BOLD, 13));
        clock.setForeground(MUTED);
        clock.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        clockTimer = new Timer(1000, e -> {
            java.time.LocalDateTime n = java.time.LocalDateTime.now();
            clock.setText(String.format("⏰  %02d:%02d:%02d",
                n.getHour(), n.getMinute(), n.getSecond()));
        });
        clockTimer.setInitialDelay(0);
        clockTimer.start();
        bar.add(clock, BorderLayout.EAST);
        return bar;
    }

    // =========================================================================
    //  SIDEBAR
    // =========================================================================
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(SIDEBAR_BG);
        sb.setPreferredSize(new Dimension(200, 0));
        sb.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_C));

        sb.add(Box.createVerticalStrut(24));
        JLabel sec = new JLabel("MENÚ PRINCIPAL", SwingConstants.CENTER);
        sec.setFont(new Font("Segoe UI", Font.BOLD, 9));
        sec.setForeground(MUTED);
        sec.setAlignmentX(Component.CENTER_ALIGNMENT);
        sec.setMaximumSize(new Dimension(200, 16));
        sb.add(sec);
        sb.add(Box.createVerticalStrut(10));
        sb.add(navBtn("🏠", "Panel Principal", () -> cardLayout.show(contentPanel, "dashboard")));
        sb.add(Box.createVerticalStrut(2));
        sb.add(navBtn("👥", "Pacientes", () -> cardLayout.show(contentPanel, "pacientes")));
        sb.add(Box.createVerticalGlue());

        // chip usuario
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_C),
            BorderFactory.createEmptyBorder(14, 0, 14, 0)));
        JLabel av = new JLabel("👨‍⚕️", SwingConstants.CENTER);
        av.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        av.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel un = new JLabel("Administrador", SwingConstants.CENTER);
        un.setFont(new Font("Segoe UI", Font.BOLD, 12));
        un.setForeground(TXT);
        un.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel ur = new JLabel("admin@medicare", SwingConstants.CENTER);
        ur.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ur.setForeground(MUTED);
        ur.setAlignmentX(Component.CENTER_ALIGNMENT);
        chip.add(av); chip.add(Box.createVerticalStrut(5));
        chip.add(un); chip.add(Box.createVerticalStrut(2)); chip.add(ur);
        sb.add(chip);
        return sb;
    }

    private JPanel navBtn(String icon, String text, Runnable action) {
        boolean[] hov = {false};
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (hov[0]) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ACCENT);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                    g2.fillRoundRect(8, 3, getWidth()-16, getHeight()-6, 10, 10);
                    g2.setComposite(AlphaComposite.SrcOver);
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(0, 8, 4, getHeight()-16, 4, 4);
                    g2.dispose();
                }
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(200, 60));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel icoL = new JLabel(icon, SwingConstants.CENTER);
        icoL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        icoL.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel txtL = new JLabel(text, SwingConstants.CENTER);
        txtL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtL.setForeground(MUTED);
        txtL.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(Box.createVerticalStrut(8)); p.add(icoL);
        p.add(Box.createVerticalStrut(2)); p.add(txtL);
        p.add(Box.createVerticalStrut(8));
        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hov[0]=true;  txtL.setForeground(ACCENT); p.repaint(); }
            @Override public void mouseExited (MouseEvent e) { hov[0]=false; txtL.setForeground(MUTED);  p.repaint(); }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return p;
    }

    // =========================================================================
    //  DASHBOARD
    // =========================================================================
    private JPanel buildDashboardView() {
        JPanel v = new JPanel(new BorderLayout(0, 22));
        v.setBackground(BG);
        v.setBorder(BorderFactory.createEmptyBorder(30, 28, 28, 28));

        JLabel title = new JLabel("🏠   Panel Principal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TXT);
        JLabel sub = new JLabel("  Bienvenido al sistema de gestión del consultorio médico");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(MUTED);
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        hdr.add(title, BorderLayout.NORTH);
        hdr.add(sub,   BorderLayout.SOUTH);
        v.add(hdr, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 0));
        cards.setOpaque(false);
        cards.add(statCard("👥", "Pacientes",       "Gestión de pacientes registrados",  ACCENT));
        cards.add(statCard("📅", "Citas",            "Búsqueda de citas por fecha",        WARN));
        cards.add(statCard("🩺", "Especialidades",  "Odontología · Pediatría · más",      new Color(0xA78BFA)));
        v.add(cards, BorderLayout.CENTER);

        JLabel hint = new JLabel("  👈  Usa el menú lateral para navegar", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(MUTED);
        v.add(hint, BorderLayout.SOUTH);
        return v;
    }

    private JPanel statCard(String ico, String title, String sub, Color accent) {
        RoundPanel c = new RoundPanel(14, CARD);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80), 1),
            BorderFactory.createEmptyBorder(22, 22, 22, 22)));
        JLabel il = new JLabel(ico); il.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        il.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tl = new JLabel(title); tl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tl.setForeground(accent); tl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sl = new JLabel("<html>" + sub + "</html>");
        sl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sl.setForeground(MUTED); sl.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(il); c.add(Box.createVerticalStrut(10));
        c.add(tl); c.add(Box.createVerticalStrut(4)); c.add(sl);
        return c;
    }

    // =========================================================================
    //  VISTA PACIENTES
    // =========================================================================
    private JPanel buildPacientesView() {
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        JLabel pageTitle = new JLabel("👥   Gestión de Pacientes");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(TXT);
        JLabel pageSub = new JLabel("  Registra, consulta y administra los pacientes del consultorio");
        pageSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pageSub.setForeground(MUTED);
        hdr.add(pageTitle, BorderLayout.NORTH);
        hdr.add(pageSub,   BorderLayout.SOUTH);
        main.add(hdr, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setBackground(BG);
        body.add(buildFormCard(),  BorderLayout.WEST);
        body.add(buildTableCard(), BorderLayout.CENTER);
        main.add(body,          BorderLayout.CENTER);
        main.add(buildFooter(), BorderLayout.SOUTH);
        return main;
    }

    // =========================================================================
    //  FORMULARIO
    // =========================================================================
    private JPanel buildFormCard() {
        RoundPanel card = new RoundPanel(14, CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C, 1),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        card.setPreferredSize(new Dimension(248, 0));

        JLabel ft = new JLabel("📋   Registro de Paciente");
        ft.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ft.setForeground(TXT);
        ft.setAlignmentX(LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_C);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);

        txtNombre    = styledField("Ej: Juan Pérez");
        txtDocumento = styledField("Ej: 1096540167");
        txtEdad      = styledField("Ej: 28");

        jcbEspecialidad = new JComboBox<>();
        styleCombo(jcbEspecialidad);

        rbtContributivo = styledRadio("Contributivo");
        rbtSubsidiado   = styledRadio("Subsidiado");
        grupoAfiliacion = new ButtonGroup();
        grupoAfiliacion.add(rbtContributivo);
        grupoAfiliacion.add(rbtSubsidiado);

        JPanel afiliRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        afiliRow.setOpaque(false);
        afiliRow.setAlignmentX(LEFT_ALIGNMENT);
        afiliRow.add(rbtContributivo);
        afiliRow.add(rbtSubsidiado);

        btnAgregar  = compactBtn("➕", "Agregar",  ACCENT,   ACCENT_H,  ACCENT_P,  new Color(0x003D30));
        btnListar   = compactBtn("📋", "Listar",   WARN,     WARN_H,    WARN_P,    new Color(0x3D2A00));
        btnEliminar = compactBtn("🗑", "Eliminar", DANGER,   DANGER_H,  DANGER_P,  TXT);
        btnLimpiar  = compactBtn("🔄", "Limpiar",  NEUTRAL,  NEUTRAL_H, NEUTRAL_P, TXT);

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        btnGrid.setOpaque(false);
        btnGrid.setAlignmentX(LEFT_ALIGNMENT);
        btnGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        btnGrid.add(btnAgregar); btnGrid.add(btnListar);
        btnGrid.add(btnEliminar); btnGrid.add(btnLimpiar);

        card.add(ft);
        card.add(Box.createVerticalStrut(8));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));
        card.add(fLabel("👤  Nombre"));       card.add(Box.createVerticalStrut(4)); card.add(txtNombre);
        card.add(Box.createVerticalStrut(10));
        card.add(fLabel("🪪  Documento"));    card.add(Box.createVerticalStrut(4)); card.add(txtDocumento);
        card.add(Box.createVerticalStrut(10));
        card.add(fLabel("🎂  Edad"));         card.add(Box.createVerticalStrut(4)); card.add(txtEdad);
        card.add(Box.createVerticalStrut(10));
        card.add(fLabel("🩺  Especialidad")); card.add(Box.createVerticalStrut(4)); card.add(jcbEspecialidad);
        card.add(Box.createVerticalStrut(10));
        card.add(fLabel("🏥  Afiliación"));   card.add(Box.createVerticalStrut(4)); card.add(afiliRow);
        card.add(Box.createVerticalStrut(18));
        card.add(btnGrid);
        card.add(Box.createVerticalGlue());
        return card;
    }

    // =========================================================================
    //  TABLA + BARRA DE BÚSQUEDA POR FECHA
    // =========================================================================
    private JPanel buildTableCard() {
        RoundPanel card = new RoundPanel(14, CARD);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        // ── Encabezado de la tabla ────────────────────────────────────────────
        JPanel cardHead = new JPanel(new BorderLayout(0, 10));
        cardHead.setOpaque(false);

        JLabel tl = new JLabel("📊   Listado de Pacientes");
        tl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tl.setForeground(TXT);
        cardHead.add(tl, BorderLayout.NORTH);

        // ── Barra de búsqueda por fecha ───────────────────────────────────────
        JPanel searchBar = new JPanel(new GridBagLayout());
        searchBar.setBackground(new Color(0x0A1628));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 4, 0, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label Buscar
        JLabel searchIco = new JLabel("🔍  Buscar por fecha:");
        searchIco.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchIco.setForeground(ACCENT);
        gbc.gridx = 0; gbc.weightx = 0;
        searchBar.add(searchIco, gbc);

        // Label Desde
        JLabel lblDesde = new JLabel("Desde:");
        lblDesde.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesde.setForeground(MUTED);
        gbc.gridx = 1; gbc.weightx = 0;
        searchBar.add(lblDesde, gbc);

        // Campo Desde
        txtFechaDesde = dateField("YYYY-MM-DD");
        gbc.gridx = 2; gbc.weightx = 0.3;
        searchBar.add(txtFechaDesde, gbc);

        // Label Hasta
        JLabel lblHasta = new JLabel("Hasta:");
        lblHasta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHasta.setForeground(MUTED);
        gbc.gridx = 3; gbc.weightx = 0;
        searchBar.add(lblHasta, gbc);

        // Campo Hasta
        txtFechaHasta = dateField("YYYY-MM-DD");
        gbc.gridx = 4; gbc.weightx = 0.3;
        searchBar.add(txtFechaHasta, gbc);

        // Botón Buscar
        btnBuscarFecha = compactBtn("🔎", "Buscar", PURPLE, PURPLE_H, PURPLE_P, TXT);
        gbc.gridx = 5; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        searchBar.add(btnBuscarFecha, gbc);

        // Botón Limpiar fecha
        JButton btnLimpiarFecha = compactBtn("✕", "Limpiar", NEUTRAL, NEUTRAL_H, NEUTRAL_P, TXT);
        btnLimpiarFecha.addActionListener(e -> {
            txtFechaDesde.setText("");
            txtFechaHasta.setText("");
        });
        gbc.gridx = 6;
        searchBar.add(btnLimpiarFecha, gbc);

        cardHead.add(searchBar, BorderLayout.SOUTH);

        searchBar.add(searchIco);
        searchBar.add(lblDesde);
        searchBar.add(txtFechaDesde);
        searchBar.add(lblHasta);
        searchBar.add(txtFechaHasta);
        searchBar.add(btnBuscarFecha);
        searchBar.add(btnLimpiarFecha);

        cardHead.add(searchBar, BorderLayout.SOUTH);
        card.add(cardHead, BorderLayout.NORTH);

        // ── Tabla ─────────────────────────────────────────────────────────────
        tabla = new JTable(new DefaultTableModel(
                new String[]{"Documento", "Nombre", "Afiliación",
                             "Edad", "Especialidad", "Fecha", "Total a Pagar"}, 0)) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ACCENT);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                    c.setForeground(TXT);
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                }
                return c;
            }
        };
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(34);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setBackground(CARD);
        tabla.setForeground(TXT);
        tabla.setSelectionBackground(ACCENT);
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);

        JTableHeader th = tabla.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(SIDEBAR_BG);
        th.setForeground(ACCENT);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C));
        th.setPreferredSize(new Dimension(0, 38));
        DefaultTableCellRenderer hRender = new DefaultTableCellRenderer();
        hRender.setHorizontalAlignment(JLabel.CENTER);
        hRender.setBackground(SIDEBAR_BG);
        hRender.setForeground(ACCENT);
        hRender.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hRender.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        th.setDefaultRenderer(hRender);

        int[] widths = {100, 160, 90, 40, 120, 145, 100};
        for (int i = 0; i < widths.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(CARD);
        sp.getViewport().setBackground(CARD);
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = BORDER_C; trackColor = BG;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            private JButton zeroBtn() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b;
            }
        });
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    //  FOOTER
    // =========================================================================
    private JPanel buildFooter() {
        RoundPanel fp = new RoundPanel(10, CARD);
        fp.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 10));
        fp.setBorder(BorderFactory.createLineBorder(BORDER_C, 1));
        JLabel icon = new JLabel("👥");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel lbl = new JLabel("Total de pacientes registrados:");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(MUTED);
        txtTotalPacientes = new JTextField("0", 4);
        txtTotalPacientes.setEditable(false);
        txtTotalPacientes.setEnabled(false);
        txtTotalPacientes.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtTotalPacientes.setDisabledTextColor(ACCENT);
        txtTotalPacientes.setForeground(ACCENT);
        txtTotalPacientes.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        txtTotalPacientes.setOpaque(false);
        fp.add(icon); fp.add(lbl); fp.add(txtTotalPacientes);
        return fp;
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================
    private JTextField styledField(String ph) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),10,10));
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TXT); f.setCaretColor(ACCENT);
        f.setBackground(BG);  f.setOpaque(false);
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        applyFieldBorder(f, false);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { applyFieldBorder(f, true);  }
            @Override public void focusLost  (FocusEvent e) { applyFieldBorder(f, false); }
        });
        return f;
    }

    /** Campo de fecha compacto para la barra de búsqueda */
    private JTextField dateField(String ph) {
        JTextField f = styledField(ph);
        f.setPreferredSize(new Dimension(115, 32));
        f.setMaximumSize(new Dimension(115, 32));
        f.setMinimumSize(new Dimension(115, 32));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private void applyFieldBorder(JTextField f, boolean focused) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(focused ? ACCENT : BORDER_C, focused ? 2 : 1),
            BorderFactory.createEmptyBorder(focused?5:6, 9, focused?5:6, 9)));
    }

    private JLabel fLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setForeground(TXT); c.setBackground(BG);
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        c.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setBackground(sel ? ACCENT : new Color(0x0D1B2A));
                setForeground(sel ? Color.BLACK : TXT);
                setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return this;
            }
        });
    }

    private JRadioButton styledRadio(String text) {
        JRadioButton r = new JRadioButton(text);
        r.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        r.setForeground(TXT); r.setOpaque(false);
        r.setFocusPainted(false);
        r.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return r;
    }

    private JButton compactBtn(String icon, String label,
            Color base, Color hover, Color pressed, Color fg) {
        JButton b = new JButton(icon + " " + label) {
            private boolean isDown = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mousePressed (MouseEvent e) { isDown = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e) { isDown = false; repaint(); }
                    @Override public void mouseEntered (MouseEvent e) { setBackground(hover); }
                    @Override public void mouseExited  (MouseEvent e) { setBackground(base); isDown = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isDown) {
                    g2.setColor(new Color(0,0,0,50));
                    g2.fill(new RoundRectangle2D.Double(2,4,getWidth()-4,getHeight()-4,10,10));
                }
                g2.setColor(isDown ? pressed : getBackground());
                g2.fill(new RoundRectangle2D.Double(0,isDown?2:0,getWidth(),getHeight()-2,10,10));
                if (!isDown) {
                    g2.setColor(new Color(255,255,255,25));
                    g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),(getHeight()-2)/2.0,10,10));
                }
                g2.dispose();
                if (isDown) {
                    Graphics2D gt = (Graphics2D)g.create(); gt.translate(0,2);
                    super.paintComponent(gt); gt.dispose();
                } else super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(fg); b.setBackground(base);
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(7, 10, 9, 10));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        return b;
    }

    // =========================================================================
    //  ANIMACIÓN DE TABLA
    // =========================================================================

    /**
     * Recibe el modelo final y agrega las filas una a una con un pequeño delay,
     * dando efecto de "aparición suave" fila por fila.
     *
     * @param modeloFinal  El DefaultTableModel con todos los datos ya cargados
     */
    public void animarTabla(javax.swing.table.DefaultTableModel modeloFinal) {
        // Crear modelo vacío con las mismas columnas
        javax.swing.table.DefaultTableModel modeloVacio =
            new javax.swing.table.DefaultTableModel();
        int cols = modeloFinal.getColumnCount();
        Object[] colNames = new Object[cols];
        for (int c = 0; c < cols; c++) colNames[c] = modeloFinal.getColumnName(c);
        modeloVacio.setColumnIdentifiers(colNames);
        tabla.setModel(modeloVacio);

        int totalFilas = modeloFinal.getRowCount();
        if (totalFilas == 0) return;

        // Copiar filas con delay entre cada una
        int[] idx = {0};
        // delay decrece con más filas para no tardar demasiado
        int delay = totalFilas <= 10 ? 80 :
                    totalFilas <= 25 ? 45 : 20;

        Timer rowTimer = new Timer(delay, null);
        rowTimer.addActionListener(e -> {
            if (idx[0] < totalFilas) {
                Object[] rowData = new Object[cols];
                for (int c = 0; c < cols; c++) {
                    rowData[c] = modeloFinal.getValueAt(idx[0], c);
                }
                ((javax.swing.table.DefaultTableModel) tabla.getModel()).addRow(rowData);
                // scroll suave hacia la última fila visible
                int lastRow = tabla.getRowCount() - 1;
                tabla.scrollRectToVisible(tabla.getCellRect(lastRow, 0, true));
                idx[0]++;
            } else {
                ((Timer) e.getSource()).stop();
                // al terminar, volver al inicio
                if (tabla.getRowCount() > 0)
                    tabla.scrollRectToVisible(tabla.getCellRect(0, 0, true));
            }
        });
        rowTimer.start();
    }

    static class RoundPanel extends JPanel {
        private final int arc; private final Color bg;
        RoundPanel(int arc, Color bg) { this.arc=arc; this.bg=bg; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),arc,arc));
            g2.dispose(); super.paintComponent(g);
        }
    }

    // =========================================================================
    //  MAIN  →  ahora abre el Login primero
    // =========================================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
