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

    // ── Menú 1: Registrar Paciente ────────────────────────────────────────────
    public JTextField   txtNombre, txtDocumento, txtEdad;
    public JComboBox<String> jcbEspecialidad;
    public JRadioButton rbtContributivo, rbtSubsidiado;
    public JButton      btnAgregar, btnLimpiarReg;

    // ── Menú 2: Listar Pacientes ──────────────────────────────────────────────
    public JTable   tabla;
    public JButton  btnListar, btnEliminar;
    public JTextField txtTotalPacientes;

    // ── Menú 3: Agendar Cita ─────────────────────────────────────────────────
    public JComboBox<String> cboAnio, cboMes, cboDia;  // selector fecha visual
    public JTextField        txtBuscarDoc;
    public JLabel            lblNombreEncontrado;
    public JComboBox<String> jcbEspCita;
    public JButton           btnBuscarPaciente, btnAgendarCita, btnEliminarCita, btnListarCitas;
    public JTable            tablaCitas;

    // ── Menú 4: Buscar Citas por Fecha ───────────────────────────────────────
    public JTextField txtFechaDesde, txtFechaHasta;
    public JButton    btnBuscarFecha, btnVerTodas;
    public JTable     tablaFechas;

    // Interno
    private String      docResaltado = "";
    private ButtonGroup grupoAfiliacion;
    private Timer       clockTimer;
    private JPanel      contentPanel;
    private CardLayout  cardLayout;

    public InterfazGrafica() { initComponents(); }

    private void initComponents() {
        setTitle("MediCare – Sistema de Consultorio");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);
        root.add(buildTopBar(), BorderLayout.NORTH);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(buildDashboard(),    "dashboard");
        contentPanel.add(buildRegistrar(),    "registrar");
        contentPanel.add(buildListar(),       "listar");
        contentPanel.add(buildAgendar(),      "agendar");
        contentPanel.add(buildBuscarFechas(), "fechas");

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
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
        bar.setPreferredSize(new Dimension(0,52));
        bar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER_C));
        JLabel logo = new JLabel("   🏥   MediCare  —  Sistema de Consultorio");
        logo.setFont(new Font("Segoe UI",Font.BOLD,15)); logo.setForeground(ACCENT);
        bar.add(logo, BorderLayout.WEST);
        JLabel clock = new JLabel();
        clock.setFont(new Font("Consolas",Font.BOLD,13)); clock.setForeground(MUTED);
        clock.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
        clockTimer = new Timer(1000, e -> {
            java.time.LocalDateTime n = java.time.LocalDateTime.now();
            clock.setText(String.format("⏰  %02d:%02d:%02d",n.getHour(),n.getMinute(),n.getSecond()));
        });
        clockTimer.setInitialDelay(0); clockTimer.start();
        bar.add(clock, BorderLayout.EAST);
        return bar;
    }

    // =========================================================================
    //  SIDEBAR — 5 secciones
    // =========================================================================
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb,BoxLayout.Y_AXIS));
        sb.setBackground(SIDEBAR_BG);
        sb.setPreferredSize(new Dimension(210,0));
        sb.setBorder(BorderFactory.createMatteBorder(0,0,0,1,BORDER_C));
        sb.add(Box.createVerticalStrut(22));
        JLabel sec = new JLabel("MENÚ PRINCIPAL",SwingConstants.CENTER);
        sec.setFont(new Font("Segoe UI",Font.BOLD,9)); sec.setForeground(MUTED);
        sec.setAlignmentX(Component.CENTER_ALIGNMENT); sec.setMaximumSize(new Dimension(210,16));
        sb.add(sec); sb.add(Box.createVerticalStrut(8));
        sb.add(navBtn("🏠","Panel Principal",  ()->cardLayout.show(contentPanel,"dashboard")));
        sb.add(Box.createVerticalStrut(2));
        sb.add(navBtn("➕","Registrar Paciente",()->cardLayout.show(contentPanel,"registrar")));
        sb.add(Box.createVerticalStrut(2));
        sb.add(navBtn("👥","Listar Pacientes",  ()->cardLayout.show(contentPanel,"listar")));
        sb.add(Box.createVerticalStrut(2));
        sb.add(navBtn("📅","Agendar Cita",      ()->cardLayout.show(contentPanel,"agendar")));
        sb.add(Box.createVerticalStrut(2));
        sb.add(navBtn("🔍","Buscar por Fecha",  ()->cardLayout.show(contentPanel,"fechas")));
        sb.add(Box.createVerticalGlue());
        // chip usuario
        JPanel chip = new JPanel(); chip.setLayout(new BoxLayout(chip,BoxLayout.Y_AXIS));
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,BORDER_C),
            BorderFactory.createEmptyBorder(12,0,12,0)));
        JLabel av = new JLabel("👨‍⚕️",SwingConstants.CENTER);
        av.setFont(new Font("Segoe UI Emoji",Font.PLAIN,24)); av.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel un = new JLabel("Administrador",SwingConstants.CENTER);
        un.setFont(new Font("Segoe UI",Font.BOLD,11)); un.setForeground(TXT); un.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel ur = new JLabel("admin@medicare",SwingConstants.CENTER);
        ur.setFont(new Font("Segoe UI",Font.PLAIN,10)); ur.setForeground(MUTED); ur.setAlignmentX(Component.CENTER_ALIGNMENT);
        chip.add(av); chip.add(Box.createVerticalStrut(4)); chip.add(un); chip.add(Box.createVerticalStrut(2)); chip.add(ur);
        sb.add(chip);
        return sb;
    }

    private JPanel navBtn(String icon, String text, Runnable action) {
        boolean[] hov={false};
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(!hov[0]) return;
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.10f));
                g2.fillRoundRect(8,3,getWidth()-16,getHeight()-6,10,10);
                g2.setComposite(AlphaComposite.SrcOver); g2.setColor(ACCENT);
                g2.fillRoundRect(0,8,4,getHeight()-16,4,4); g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); p.setOpaque(false);
        p.setMaximumSize(new Dimension(210,58)); p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel icoL=new JLabel(icon,SwingConstants.CENTER);
        icoL.setFont(new Font("Segoe UI Emoji",Font.PLAIN,18)); icoL.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel txtL=new JLabel(text,SwingConstants.CENTER);
        txtL.setFont(new Font("Segoe UI",Font.PLAIN,11)); txtL.setForeground(MUTED); txtL.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(Box.createVerticalStrut(7)); p.add(icoL); p.add(Box.createVerticalStrut(2)); p.add(txtL); p.add(Box.createVerticalStrut(7));
        p.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){hov[0]=true; txtL.setForeground(ACCENT); p.repaint();}
            @Override public void mouseExited (MouseEvent e){hov[0]=false;txtL.setForeground(MUTED);  p.repaint();}
            @Override public void mouseClicked(MouseEvent e){action.run();}
        });
        return p;
    }

    // =========================================================================
    //  DASHBOARD
    // =========================================================================
    private JPanel buildDashboard() {
        JPanel v = new JPanel(new BorderLayout(0,22)); v.setBackground(BG);
        v.setBorder(BorderFactory.createEmptyBorder(30,28,28,28));
        JLabel title=new JLabel("🏠   Panel Principal"); title.setFont(new Font("Segoe UI",Font.BOLD,22)); title.setForeground(TXT);
        JLabel sub=new JLabel("  Bienvenido al sistema de gestión del Consultorio Médico"); sub.setFont(new Font("Segoe UI",Font.PLAIN,12)); sub.setForeground(MUTED);
        JPanel hdr=new JPanel(new BorderLayout()); hdr.setOpaque(false);
        hdr.add(title,BorderLayout.NORTH); hdr.add(sub,BorderLayout.SOUTH);
        v.add(hdr,BorderLayout.NORTH);
        JPanel cards=new JPanel(new GridLayout(2,2,16,16)); cards.setOpaque(false);
        cards.add(statCard("➕","Registrar Paciente","Agrega nuevos pacientes a la base de datos",ACCENT,()->cardLayout.show(contentPanel,"registrar")));
        cards.add(statCard("👥","Listar Pacientes","Consulta todos los pacientes registrados",WARN,()->cardLayout.show(contentPanel,"listar")));
        cards.add(statCard("📅","Agendar Cita","Busca un paciente y agenda su cita médica",new Color(0xA78BFA),()->cardLayout.show(contentPanel,"agendar")));
        cards.add(statCard("🔍","Buscar por Fecha","Filtra citas según rango de fechas",new Color(0xFF7F7F),()->cardLayout.show(contentPanel,"fechas")));
        v.add(cards,BorderLayout.CENTER);
        JLabel hint=new JLabel("👆  Haz clic en cualquier tarjeta o usa el menú lateral",SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI",Font.PLAIN,11)); hint.setForeground(MUTED);
        v.add(hint,BorderLayout.SOUTH);
        return v;
    }

    private JPanel statCard(String ico,String title,String sub,Color accent,Runnable action){
        RoundPanel c=new RoundPanel(14,CARD);
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),80),1),
            BorderFactory.createEmptyBorder(20,20,20,20)));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel il=new JLabel(ico); il.setFont(new Font("Segoe UI Emoji",Font.PLAIN,30)); il.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tl=new JLabel(title); tl.setFont(new Font("Segoe UI",Font.BOLD,14)); tl.setForeground(accent); tl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sl=new JLabel("<html>"+sub+"</html>"); sl.setFont(new Font("Segoe UI",Font.PLAIN,11)); sl.setForeground(MUTED); sl.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(il); c.add(Box.createVerticalStrut(8)); c.add(tl); c.add(Box.createVerticalStrut(4)); c.add(sl);
        c.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){action.run();}
            @Override public void mouseEntered(MouseEvent e){c.repaint();}
        });
        return c;
    }

    // =========================================================================
    //  MENÚ 1 — REGISTRAR PACIENTE
    // =========================================================================
    private JPanel buildRegistrar() {
        JPanel main=new JPanel(new BorderLayout(0,14)); main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(22,22,22,22));
        main.add(pageHeader("➕","Registrar Nuevo Paciente","Completa el formulario para registrar un paciente en el sistema"),BorderLayout.NORTH);

        // formulario centrado
        RoundPanel form=new RoundPanel(14,CARD);
        form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C,1),
            BorderFactory.createEmptyBorder(28,28,28,28)));
        form.setMaximumSize(new Dimension(520,Integer.MAX_VALUE));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ft=new JLabel("📋   Datos del Paciente"); ft.setFont(new Font("Segoe UI",Font.BOLD,14)); ft.setForeground(TXT); ft.setAlignmentX(LEFT_ALIGNMENT);
        JSeparator sep=new JSeparator(); sep.setForeground(BORDER_C); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); sep.setAlignmentX(LEFT_ALIGNMENT);

        txtNombre    = sField("Nombre completo");
        txtDocumento = sField("Número de documento");
        txtEdad      = sField("Edad");
        jcbEspecialidad = new JComboBox<>(); styleCombo(jcbEspecialidad);

        rbtContributivo=styledRadio("Contributivo"); rbtSubsidiado=styledRadio("Subsidiado");
        grupoAfiliacion=new ButtonGroup(); grupoAfiliacion.add(rbtContributivo); grupoAfiliacion.add(rbtSubsidiado);
        JPanel afiliRow=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); afiliRow.setOpaque(false); afiliRow.setAlignmentX(LEFT_ALIGNMENT);
        afiliRow.add(rbtContributivo); afiliRow.add(rbtSubsidiado);

        btnAgregar    = cBtn("✅","Registrar Paciente",ACCENT,  ACCENT_H, ACCENT_P,new Color(0x003D30));
        btnLimpiarReg = cBtn("🔄","Limpiar Campos",   NEUTRAL, NEUTRAL_H,NEUTRAL_P,TXT);
        for(JButton b:new JButton[]{btnAgregar,btnLimpiarReg}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));}

        form.add(ft); form.add(Box.createVerticalStrut(10)); form.add(sep); form.add(Box.createVerticalStrut(18));
        form.add(fLabel("👤  Nombre Completo"));   form.add(Box.createVerticalStrut(5)); form.add(txtNombre);   form.add(Box.createVerticalStrut(12));
        form.add(fLabel("🪪  Número de Documento")); form.add(Box.createVerticalStrut(5)); form.add(txtDocumento);form.add(Box.createVerticalStrut(12));
        form.add(fLabel("🎂  Edad"));              form.add(Box.createVerticalStrut(5)); form.add(txtEdad);     form.add(Box.createVerticalStrut(12));
        form.add(fLabel("🩺  Especialidad"));      form.add(Box.createVerticalStrut(5)); form.add(jcbEspecialidad); form.add(Box.createVerticalStrut(12));
        form.add(fLabel("🏥  Tipo de Afiliación")); form.add(Box.createVerticalStrut(5)); form.add(afiliRow); form.add(Box.createVerticalStrut(22));
        form.add(btnAgregar); form.add(Box.createVerticalStrut(10)); form.add(btnLimpiarReg);

        JPanel center=new JPanel(new GridBagLayout()); center.setBackground(BG);
        GridBagConstraints gc=new GridBagConstraints(); gc.fill=GridBagConstraints.BOTH; gc.weightx=1; gc.weighty=1;
        center.add(form,gc);
        main.add(center,BorderLayout.CENTER);
        return main;
    }

    // =========================================================================
    //  MENÚ 2 — LISTAR PACIENTES
    // =========================================================================
    private JPanel buildListar() {
        JPanel main=new JPanel(new BorderLayout(0,12)); main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(22,22,22,22));
        main.add(pageHeader("👥","Listado de Pacientes","Consulta todos los pacientes registrados en el sistema"),BorderLayout.NORTH);

        RoundPanel card=new RoundPanel(14,CARD);
        card.setLayout(new BorderLayout(0,10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C,1),
            BorderFactory.createEmptyBorder(16,16,16,16)));

        // barra de acciones
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); actions.setOpaque(false);
        btnListar  = cBtn("📋","Listar Todos",WARN,   WARN_H,  WARN_P,  new Color(0x3D2A00));
        btnEliminar= cBtn("🗑","Eliminar Seleccionado",DANGER,DANGER_H,DANGER_P,TXT);
        btnListar.setPreferredSize(new Dimension(150,36));
        btnEliminar.setPreferredSize(new Dimension(200,36));
        actions.add(btnListar); actions.add(btnEliminar);

        // footer total
        JPanel footerRow=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); footerRow.setOpaque(false);
        JLabel fl=new JLabel("Total de pacientes:"); fl.setFont(new Font("Segoe UI",Font.PLAIN,12)); fl.setForeground(MUTED);
        txtTotalPacientes=new JTextField("0",4); txtTotalPacientes.setEditable(false); txtTotalPacientes.setEnabled(false);
        txtTotalPacientes.setFont(new Font("Segoe UI",Font.BOLD,16)); txtTotalPacientes.setDisabledTextColor(ACCENT);
        txtTotalPacientes.setBorder(BorderFactory.createEmptyBorder(0,4,0,0)); txtTotalPacientes.setOpaque(false);
        footerRow.add(fl); footerRow.add(txtTotalPacientes);

        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(actions,BorderLayout.WEST); top.add(footerRow,BorderLayout.EAST);
        card.add(top,BorderLayout.NORTH);

        tabla=styledTable(new String[]{"Documento","Nombre","Afiliación","Edad","Especialidad","Fecha","Total a Pagar"});
        int[]w={100,160,90,40,120,100,100};
        for(int i=0;i<w.length;i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        card.add(styledScroll(tabla),BorderLayout.CENTER);
        main.add(card,BorderLayout.CENTER);
        return main;
    }

    // =========================================================================
    //  MENÚ 3 — AGENDAR CITA
    // =========================================================================
    private JPanel buildAgendar() {
        JPanel main=new JPanel(new BorderLayout(0,14)); main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(22,22,22,22));
        main.add(pageHeader("📅","Agendar Cita Médica","Busca el paciente por documento y agenda su cita"),BorderLayout.NORTH);

        JPanel body=new JPanel(new BorderLayout(16,0)); body.setBackground(BG);
        body.add(buildAgendarForm(),  BorderLayout.WEST);
        body.add(buildAgendarTabla(), BorderLayout.CENTER);
        main.add(body,BorderLayout.CENTER);
        return main;
    }

    private JPanel buildAgendarForm() {
        RoundPanel card=new RoundPanel(14,CARD);
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C,1),
            BorderFactory.createEmptyBorder(20,18,20,18)));
        card.setPreferredSize(new Dimension(290,0));

        JLabel ft=new JLabel("📅   Nueva Cita"); ft.setFont(new Font("Segoe UI",Font.BOLD,13)); ft.setForeground(TXT); ft.setAlignmentX(LEFT_ALIGNMENT);
        JSeparator sep=new JSeparator(); sep.setForeground(BORDER_C); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); sep.setAlignmentX(LEFT_ALIGNMENT);

        // paso 1
        JLabel p1=badge("PASO 1","Buscar Paciente",ACCENT);
        txtBuscarDoc=sField("Número de documento");
        btnBuscarPaciente=cBtn("🔍","Buscar Paciente",PURPLE,PURPLE_H,PURPLE_P,TXT);
        btnBuscarPaciente.setAlignmentX(LEFT_ALIGNMENT); btnBuscarPaciente.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));

        RoundPanel chip=new RoundPanel(8,new Color(0x0A1628));
        chip.setLayout(new BorderLayout());
        chip.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_C,1),BorderFactory.createEmptyBorder(8,12,8,12)));
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE,42)); chip.setAlignmentX(LEFT_ALIGNMENT);
        lblNombreEncontrado=new JLabel("— Sin paciente seleccionado —");
        lblNombreEncontrado.setFont(new Font("Segoe UI",Font.ITALIC,12)); lblNombreEncontrado.setForeground(MUTED);
        chip.add(lblNombreEncontrado,BorderLayout.CENTER);

        // paso 2
        JLabel p2=badge("PASO 2","Datos de la Cita",WARN);
        //txtFechaCita=sField("YYYY-MM-DD   ej: 2026-06-20");
        jcbEspCita=new JComboBox<>(); styleCombo(jcbEspCita);

        // botones
        btnAgendarCita  =cBtn("✅","Agendar Cita",  ACCENT,  ACCENT_H, ACCENT_P,new Color(0x003D30));
        btnEliminarCita =cBtn("🗑","Eliminar Cita", DANGER,  DANGER_H, DANGER_P,TXT);
        btnListarCitas  =cBtn("📋","Ver Todas",     WARN,    WARN_H,   WARN_P,  new Color(0x3D2A00));
        for(JButton b:new JButton[]{btnAgendarCita,btnEliminarCita,btnListarCitas}){b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));}

        card.add(ft); card.add(Box.createVerticalStrut(8)); card.add(sep); card.add(Box.createVerticalStrut(16));
        card.add(p1); card.add(Box.createVerticalStrut(8));
        card.add(fLabel("🪪  Documento del paciente")); card.add(Box.createVerticalStrut(4)); card.add(txtBuscarDoc); card.add(Box.createVerticalStrut(8));
        card.add(btnBuscarPaciente); card.add(Box.createVerticalStrut(8));
        card.add(fLabel("👤  Resultado:")); card.add(Box.createVerticalStrut(4)); card.add(chip);
        card.add(Box.createVerticalStrut(18));
        card.add(p2); card.add(Box.createVerticalStrut(8));
        card.add(fLabel("📅  Fecha de la cita")); card.add(Box.createVerticalStrut(6));
        JPanel fechaRow = new JPanel(new GridLayout(1,3,6,0));
        fechaRow.setOpaque(false); fechaRow.setAlignmentX(LEFT_ALIGNMENT);
        fechaRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        String[] anios = new String[11]; anios[0]="Año";
        for(int i=1;i<=10;i++) anios[i]=String.valueOf(2025+i);
        cboAnio=new JComboBox<>(anios); styleCombo(cboAnio);
        cboMes=new JComboBox<>(new String[]{"Mes","01-Ene","02-Feb","03-Mar","04-Abr",
            "05-May","06-Jun","07-Jul","08-Ago","09-Sep","10-Oct","11-Nov","12-Dic"});
        styleCombo(cboMes);
        String[] dias=new String[32]; dias[0]="Día";
        for(int i=1;i<=31;i++) dias[i]=String.format("%02d",i);
        cboDia=new JComboBox<>(dias); styleCombo(cboDia);
        fechaRow.add(cboAnio); fechaRow.add(cboMes); fechaRow.add(cboDia);
        card.add(fechaRow); card.add(Box.createVerticalStrut(10));
        card.add(fLabel("🩺  Especialidad")); card.add(Box.createVerticalStrut(4)); card.add(jcbEspCita);
        card.add(Box.createVerticalStrut(20));
        card.add(btnAgendarCita); card.add(Box.createVerticalStrut(8));
        card.add(btnEliminarCita); card.add(Box.createVerticalStrut(8));
        card.add(btnListarCitas);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel buildAgendarTabla() {
        RoundPanel card=new RoundPanel(14,CARD);
        card.setLayout(new BorderLayout(0,10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C,1),
            BorderFactory.createEmptyBorder(16,16,16,16)));
        JPanel ch=new JPanel(new BorderLayout()); ch.setOpaque(false);
        JLabel tl=new JLabel("🗓   Citas Registradas"); tl.setFont(new Font("Segoe UI",Font.BOLD,14)); tl.setForeground(TXT);
        JLabel hint=new JLabel("Selecciona una fila y usa Eliminar Cita · Pulsa Ver Todas para actualizar");
        hint.setFont(new Font("Segoe UI",Font.PLAIN,10)); hint.setForeground(MUTED);
        ch.add(tl,BorderLayout.NORTH); ch.add(hint,BorderLayout.SOUTH);
        card.add(ch,BorderLayout.NORTH);
        tablaCitas=styledTableDoc(new String[]{"ID","Documento","Paciente","Fecha","Especialidad","Copago"});
        int[]w={40,100,160,95,130,90};
        for(int i=0;i<w.length;i++) tablaCitas.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        card.add(styledScroll(tablaCitas),BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    //  MENÚ 4 — BUSCAR POR FECHA
    // =========================================================================
    private JPanel buildBuscarFechas() {
        JPanel main=new JPanel(new BorderLayout(0,14)); main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(22,22,22,22));
        main.add(pageHeader("🔍","Buscar Citas por Fecha","Filtra los pacientes según el rango de fechas de sus citas"),BorderLayout.NORTH);

        RoundPanel card=new RoundPanel(14,CARD);
        card.setLayout(new BorderLayout(0,12));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C,1),
            BorderFactory.createEmptyBorder(16,16,16,16)));

        // barra de filtro
        JPanel bar=new JPanel(new FlowLayout(FlowLayout.LEFT,12,0)); bar.setOpaque(false);
        JLabel lDesde=new JLabel("📅  Desde:"); lDesde.setFont(new Font("Segoe UI",Font.BOLD,12)); lDesde.setForeground(ACCENT);
        JLabel lHasta=new JLabel("Hasta:"); lHasta.setFont(new Font("Segoe UI",Font.PLAIN,12)); lHasta.setForeground(MUTED);
        txtFechaDesde=dateF("YYYY-MM-DD"); txtFechaHasta=dateF("YYYY-MM-DD");
        btnBuscarFecha=cBtn("🔎","Buscar",  PURPLE, PURPLE_H, PURPLE_P,TXT);
        btnVerTodas   =cBtn("📋","Ver Todas",WARN,   WARN_H,   WARN_P,  new Color(0x3D2A00));
        JButton btnLF =cBtn("✕","Limpiar", NEUTRAL,NEUTRAL_H,NEUTRAL_P,TXT);
        btnLF.addActionListener(e->{ txtFechaDesde.setText(""); txtFechaHasta.setText(""); });
        for(JButton b:new JButton[]{btnBuscarFecha,btnVerTodas,btnLF}) b.setPreferredSize(new Dimension(110,34));
        bar.add(lDesde); bar.add(txtFechaDesde); bar.add(lHasta); bar.add(txtFechaHasta);
        bar.add(btnBuscarFecha); bar.add(btnVerTodas); bar.add(btnLF);
        card.add(bar,BorderLayout.NORTH);

        tablaFechas=styledTable(new String[]{"Documento","Nombre","Afiliación","Edad","Especialidad","Fecha","Total a Pagar"});
        int[]w={100,160,90,40,120,100,100};
        for(int i=0;i<w.length;i++) tablaFechas.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        card.add(styledScroll(tablaFechas),BorderLayout.CENTER);
        main.add(card,BorderLayout.CENTER);
        return main;
    }

    // =========================================================================
    //  ANIMACIONES
    // =========================================================================
    public void animarTabla(DefaultTableModel m) { animarEn(tabla, m); }
    public void animarTablaCitas(DefaultTableModel m) { animarEn(tablaCitas, m); }
    public void animarTablaFechas(DefaultTableModel m) { animarEn(tablaFechas, m); }

    private void animarEn(JTable t, DefaultTableModel m) {
        DefaultTableModel v=new DefaultTableModel();
        int cols=m.getColumnCount(); Object[]names=new Object[cols];
        for(int c=0;c<cols;c++) names[c]=m.getColumnName(c);
        v.setColumnIdentifiers(names); t.setModel(v);
        int total=m.getRowCount(); if(total==0) return;
        int[]idx={0}; int delay=total<=10?75:total<=25?40:18;
        Timer tm=new Timer(delay,null);
        tm.addActionListener(e->{
            if(idx[0]<total){
                Object[]row=new Object[cols];
                for(int c=0;c<cols;c++) row[c]=m.getValueAt(idx[0],c);
                ((DefaultTableModel)t.getModel()).addRow(row);
                t.scrollRectToVisible(t.getCellRect(t.getRowCount()-1,0,true));
                idx[0]++;
            } else {
                ((Timer)e.getSource()).stop();
                if(t.getRowCount()>0) t.scrollRectToVisible(t.getCellRect(0,0,true));
            }
        });
        tm.start();
    }

    public void resaltarDocumentoEnTabla(String documento) {
        this.docResaltado=documento;
        tablaCitas.repaint();
        if(!documento.isEmpty()){
            for(int i=0;i<tablaCitas.getRowCount();i++){
                Object val=tablaCitas.getModel().getValueAt(i,1);
                if(val!=null&&val.toString().equals(documento)){
                    tablaCitas.scrollRectToVisible(tablaCitas.getCellRect(i,0,true)); break;
                }
            }
        }
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================
    private JPanel pageHeader(String ico, String title, String sub) {
        JPanel h=new JPanel(new BorderLayout()); h.setOpaque(false); h.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        JLabel tl=new JLabel(ico+"   "+title); tl.setFont(new Font("Segoe UI",Font.BOLD,20)); tl.setForeground(TXT);
        JLabel sl=new JLabel("  "+sub); sl.setFont(new Font("Segoe UI",Font.PLAIN,11)); sl.setForeground(MUTED);
        h.add(tl,BorderLayout.NORTH); h.add(sl,BorderLayout.SOUTH);
        return h;
    }

    private JLabel badge(String num, String txt, Color color) {
        JLabel l=new JLabel("  "+num+" — "+txt);
        l.setFont(new Font("Segoe UI",Font.BOLD,10)); l.setForeground(color); l.setAlignmentX(LEFT_ALIGNMENT); return l;
    }

    private JTable styledTable(String[] cols) {
        JTable t=new JTable(new DefaultTableModel(cols,0)){
            @Override public boolean isCellEditable(int r,int c){return false;}
            @Override public Component prepareRenderer(TableCellRenderer r,int row,int col){
                Component c=super.prepareRenderer(r,row,col);
                if(isRowSelected(row)){c.setBackground(ACCENT);c.setForeground(Color.BLACK);}
                else{c.setBackground(row%2==0?CARD:ROW_ALT);c.setForeground(TXT);}
                if(c instanceof JLabel){((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,8,0,8));}
                return c;
            }
        };
        styleTableBase(t); return t;
    }

    /** Tabla con resaltado por documento (para tablaCitas) */
    private JTable styledTableDoc(String[] cols) {
        JTable t=new JTable(new DefaultTableModel(cols,0)){
            @Override public boolean isCellEditable(int r,int c){return false;}
            @Override public Component prepareRenderer(TableCellRenderer r,int row,int col){
                Component c=super.prepareRenderer(r,row,col);
                boolean res=!docResaltado.isEmpty()&&getColumnCount()>1&&getModel().getValueAt(row,1)!=null&&getModel().getValueAt(row,1).toString().equals(docResaltado);
                if(isRowSelected(row)){c.setBackground(ACCENT);c.setForeground(Color.BLACK);}
                else if(res){c.setBackground(new Color(0x1A4A3E));c.setForeground(ACCENT);}
                else{c.setBackground(row%2==0?CARD:ROW_ALT);c.setForeground(TXT);}
                if(c instanceof JLabel){
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                    if(res&&!isRowSelected(row))((JLabel)c).setFont(getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        };
        styleTableBase(t); return t;
    }

    private void styleTableBase(JTable t) {
        t.setFont(new Font("Segoe UI",Font.PLAIN,13)); t.setRowHeight(34);
        t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,0));
        t.setBackground(CARD); t.setForeground(TXT);
        t.setSelectionBackground(ACCENT); t.setSelectionForeground(Color.BLACK);
        t.setFillsViewportHeight(true); t.getTableHeader().setReorderingAllowed(false);
        JTableHeader th=t.getTableHeader();
        th.setFont(new Font("Segoe UI",Font.BOLD,12)); th.setBackground(SIDEBAR_BG); th.setForeground(ACCENT);
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER_C)); th.setPreferredSize(new Dimension(0,38));
        DefaultTableCellRenderer hr=new DefaultTableCellRenderer();
        hr.setHorizontalAlignment(JLabel.CENTER); hr.setBackground(SIDEBAR_BG); hr.setForeground(ACCENT);
        hr.setFont(new Font("Segoe UI",Font.BOLD,12)); hr.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
        th.setDefaultRenderer(hr);
    }

    private JScrollPane styledScroll(JTable t) {
        JScrollPane sp=new JScrollPane(t); sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(CARD); sp.getViewport().setBackground(CARD);
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
            @Override protected void configureScrollBarColors(){thumbColor=BORDER_C;trackColor=BG;}
            @Override protected JButton createDecreaseButton(int o){return z();}
            @Override protected JButton createIncreaseButton(int o){return z();}
            JButton z(){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
        });
        return sp;
    }

    JTextField sField(String ph){
        JTextField f=new JTextField(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG);g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),10,10));g2.dispose();super.paintComponent(g);}};
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));f.setForeground(TXT);f.setCaretColor(ACCENT);
        f.setBackground(BG);f.setOpaque(false);f.setAlignmentX(LEFT_ALIGNMENT);f.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        applyFB(f,false);
        f.addFocusListener(new FocusAdapter(){@Override public void focusGained(FocusEvent e){applyFB(f,true);}@Override public void focusLost(FocusEvent e){applyFB(f,false);}});
        return f;}

    private JTextField dateF(String ph){
        JTextField f=sField(ph);f.setPreferredSize(new Dimension(120,34));f.setMaximumSize(new Dimension(120,34));f.setMinimumSize(new Dimension(120,34));return f;}

    private void applyFB(JTextField f,boolean focused){
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(focused?ACCENT:BORDER_C,focused?2:1),BorderFactory.createEmptyBorder(focused?5:6,9,focused?5:6,9)));}

    JLabel fLabel(String text){
        JLabel l=new JLabel(text);l.setFont(new Font("Segoe UI",Font.PLAIN,11));l.setForeground(MUTED);l.setAlignmentX(LEFT_ALIGNMENT);return l;}

    void styleCombo(JComboBox<String> c){
        c.setFont(new Font("Segoe UI",Font.PLAIN,13));c.setForeground(TXT);c.setBackground(BG);
        c.setAlignmentX(LEFT_ALIGNMENT);c.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_C,1),BorderFactory.createEmptyBorder(4,8,4,8)));
        c.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?>list,Object val,int idx,boolean sel,boolean focus){
                super.getListCellRendererComponent(list,val,idx,sel,focus);
                setBackground(sel?ACCENT:new Color(0x0D1B2A));setForeground(sel?Color.BLACK:TXT);
                setBorder(BorderFactory.createEmptyBorder(7,12,7,12));setFont(new Font("Segoe UI",Font.PLAIN,13));return this;}});}

    private JRadioButton styledRadio(String text){
        JRadioButton r=new JRadioButton(text);r.setFont(new Font("Segoe UI",Font.PLAIN,12));r.setForeground(TXT);
        r.setOpaque(false);r.setFocusPainted(false);r.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return r;}

    JButton cBtn(String icon,String label,Color base,Color hover,Color pressed,Color fg){
        JButton b=new JButton(icon+" "+label){
            private boolean isDown=false;
            {addMouseListener(new MouseAdapter(){
                @Override public void mousePressed (MouseEvent e){isDown=true; repaint();}
                @Override public void mouseReleased(MouseEvent e){isDown=false;repaint();}
                @Override public void mouseEntered (MouseEvent e){setBackground(hover);}
                @Override public void mouseExited  (MouseEvent e){setBackground(base);isDown=false;repaint();}
            });}
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(!isDown){g2.setColor(new Color(0,0,0,50));g2.fill(new RoundRectangle2D.Double(2,4,getWidth()-4,getHeight()-4,10,10));}
                g2.setColor(isDown?pressed:getBackground());g2.fill(new RoundRectangle2D.Double(0,isDown?2:0,getWidth(),getHeight()-2,10,10));
                if(!isDown){g2.setColor(new Color(255,255,255,25));g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),(getHeight()-2)/2.0,10,10));}
                g2.dispose();
                if(isDown){Graphics2D gt=(Graphics2D)g.create();gt.translate(0,2);super.paintComponent(gt);gt.dispose();}else super.paintComponent(g);}};
        b.setFont(new Font("Segoe UI",Font.BOLD,11));b.setForeground(fg);b.setBackground(base);
        b.setOpaque(false);b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setBorder(BorderFactory.createEmptyBorder(7,12,9,12));
        b.setHorizontalAlignment(SwingConstants.CENTER);return b;}

    static class RoundPanel extends JPanel{
        private final int arc;private final Color bg;
        RoundPanel(int arc,Color bg){this.arc=arc;this.bg=bg;setOpaque(false);}
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),arc,arc));g2.dispose();super.paintComponent(g);}}

    public static void main(String[] args){
        try{UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}catch(Exception ignored){}
        SwingUtilities.invokeLater(()->new LoginFrame().setVisible(true));}
}
