package Vista;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Pantalla de inicio de sesión animada.
 * Credenciales por defecto: admin / admin
 */
public class LoginFrame extends JFrame {

    // ── Paleta (igual que InterfazGrafica) ────────────────────────────────────
    static final Color BG       = new Color(0x0D1B2A);
    static final Color CARD     = new Color(0x16263E);
    static final Color ACCENT   = new Color(0x00C9A7);
    static final Color ACCENT_H = new Color(0x00E5BF);
    static final Color ACCENT_P = new Color(0x008F76);
    static final Color DANGER   = new Color(0xFF5A5F);
    static final Color TXT      = new Color(0xE8F4F8);
    static final Color MUTED    = new Color(0x7A9BB5);
    static final Color BORDER_C = new Color(0x1E3A5F);

    private JTextField     txtUsuario;
    private JPasswordField txtPassword;
    private Timer          animTimer;
    private float          animPhase = 0f;

    public LoginFrame() {
        setTitle("MediCare – Iniciar Sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 520);
        setLocationRelativeTo(null);
        setUndecorated(true);          // sin bordes del SO
        setResizable(false);

        // ── Fondo animado ─────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // gradiente animado
                float shift = (float)(Math.sin(animPhase) * 60);
                GradientPaint gp = new GradientPaint(
                    0, 0,                 new Color(0x0A1628),
                    getWidth()+shift, getHeight(), new Color(0x0D2540)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // círculos decorativos
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                g2.setColor(ACCENT);
                g2.fill(new Ellipse2D.Double(-80 + shift/2, -80, 380, 380));
                g2.fill(new Ellipse2D.Double(getWidth()-200 - shift/3,
                                              getHeight()-200, 320, 320));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        root.setOpaque(true);

        // ── Botón cerrar (esquina superior derecha) ───────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        topBar.setOpaque(false);
        JButton closeBtn = flatBtn("✕", MUTED, DANGER);
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(closeBtn);

        // ── Panel izquierdo: branding ──────────────────────────────────────────
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 30));

        JLabel logoIco = new JLabel("🏥");
        logoIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        logoIco.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoTxt = new JLabel("MediCare");
        logoTxt.setFont(new Font("Segoe UI", Font.BOLD, 38));
        logoTxt.setForeground(ACCENT);
        logoTxt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoSub = new JLabel("Sistema de Consultorio Médico");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoSub.setForeground(MUTED);
        logoSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // separador
        JSeparator sepH = new JSeparator();
        sepH.setForeground(new Color(0x1E3A5F));
        sepH.setMaximumSize(new Dimension(220, 1));
        sepH.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(Box.createVerticalGlue());
        left.add(logoIco);
        left.add(Box.createVerticalStrut(10));
        left.add(logoTxt);
        left.add(Box.createVerticalStrut(4));
        left.add(logoSub);
        left.add(Box.createVerticalStrut(24));
        left.add(sepH);
        left.add(Box.createVerticalStrut(20));
        left.add(featureLbl("📋", "Registro de Pacientes"));
        left.add(Box.createVerticalStrut(8));
        left.add(featureLbl("📅", "Gestión de Citas y Fechas"));
        left.add(Box.createVerticalStrut(8));
        left.add(featureLbl("🩺", "Control de Especialidades"));
        left.add(Box.createVerticalGlue());

        // ── Panel derecho: formulario de login ────────────────────────────────
        RoundPanel formCard = new RoundPanel(18, CARD);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C, 1),
            BorderFactory.createEmptyBorder(36, 34, 36, 34)
        ));
        formCard.setPreferredSize(new Dimension(340, 380));

        JLabel welcome = new JLabel("Bienvenido");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(TXT);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Ingresa tus credenciales para continuar");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsuario  = loginField("Ej: admin");
        txtPassword = new JPasswordField();
        stylePasswordField(txtPassword);

        JButton btnEntrar = new JButton("🚀   Ingresar al Sistema") {
            private boolean isDown = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mousePressed (MouseEvent e) { isDown = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e) { isDown = false; repaint(); }
                    @Override public void mouseEntered (MouseEvent e) { setBackground(ACCENT_H); }
                    @Override public void mouseExited  (MouseEvent e) { setBackground(ACCENT); isDown = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isDown) {
                    g2.setColor(new Color(0,0,0,50));
                    g2.fill(new RoundRectangle2D.Double(2,4,getWidth()-4,getHeight()-4,12,12));
                }
                g2.setColor(isDown ? ACCENT_P : getBackground());
                g2.fill(new RoundRectangle2D.Double(0, isDown?2:0, getWidth(), getHeight()-2, 12, 12));
                if (!isDown) {
                    g2.setColor(new Color(255,255,255,30));
                    g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),(getHeight()-2)/2.0,12,12));
                }
                g2.dispose();
                if (isDown) {
                    Graphics2D gt = (Graphics2D)g.create(); gt.translate(0,2);
                    super.paintComponent(gt); gt.dispose();
                } else super.paintComponent(g);
            }
        };
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEntrar.setForeground(new Color(0x003D30));
        btnEntrar.setBackground(ACCENT);
        btnEntrar.setOpaque(false);
        btnEntrar.setContentAreaFilled(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEntrar.setBorder(BorderFactory.createEmptyBorder(11, 20, 13, 20));
        btnEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JLabel footer = new JLabel("© 2026 MediCare · Proyecto POO", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(new Color(0x3A5A7A));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        formCard.add(welcome);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(hint);
        formCard.add(Box.createVerticalStrut(28));
        formCard.add(fieldLabel("👤  Usuario"));
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(txtUsuario);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(fieldLabel("🔒  Contraseña"));
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(txtPassword);
        formCard.add(Box.createVerticalStrut(26));
        formCard.add(btnEntrar);
        formCard.add(Box.createVerticalStrut(18));
        formCard.add(footer);

        // ── Armar layout principal ─────────────────────────────────────────────
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH; gc.weighty = 1;
        gc.weightx = 0.5; gc.gridx = 0; content.add(left, gc);
        gc.weightx = 0.5; gc.gridx = 1;
        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(formCard);
        content.add(rightWrap, gc);

        // layout raíz con topBar arriba
        setLayout(new BorderLayout());
        add(topBar,  BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        // permitir arrastrar ventana
        addDragSupport(root);
        addDragSupport(content);
        addDragSupport(left);

        // ── Acción de login ───────────────────────────────────────────────────
        Action login = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                String user = txtUsuario.getText().trim();
                String pass = new String(txtPassword.getPassword()).trim();
                if (user.equals("JohanRomero") && pass.equals("1096540167")) {
                    animTimer.stop();
                    dispose();
                    // Mostrar splash de bienvenida antes de abrir el sistema
                    SwingUtilities.invokeLater(() -> {
                        SplashScreen splash = new SplashScreen("Johan Romero", () -> {
                            InterfazGrafica vista = new InterfazGrafica();
                            new Controlador.Controlador(vista);
                            vista.setVisible(true);
                            vista.setLocationRelativeTo(null);
                        });
                        splash.mostrar();
                    });
                } else {
                    sacudir(formCard);
                    txtPassword.setText("");
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Usuario o contraseña incorrectos.\nIngresa las credenciales correctas",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        btnEntrar.addActionListener(login);
        txtPassword.addActionListener(login);

        // ── Timer animación ───────────────────────────────────────────────────
        animTimer = new Timer(25, e -> {
            animPhase += 0.018f;
            repaint();
        });
        animTimer.start();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel featureLbl(String ico, String txt) {
        JLabel l = new JLabel(ico + "  " + txt, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(MUTED);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel fieldLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField loginField(String ph) {
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
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        applyBorder(f, false);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { applyBorder(f, true);  }
            @Override public void focusLost  (FocusEvent e) { applyBorder(f, false); }
        });
        return f;
    }

    private void stylePasswordField(JPasswordField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TXT); f.setCaretColor(ACCENT);
        f.setBackground(BG);  f.setOpaque(false);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        applyBorderPwd(f, false);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { applyBorderPwd(f, true);  }
            @Override public void focusLost  (FocusEvent e) { applyBorderPwd(f, false); }
        });
    }

    private void applyBorder(JTextField f, boolean focused) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(focused ? ACCENT : BORDER_C, focused ? 2 : 1),
            BorderFactory.createEmptyBorder(focused?5:6, 10, focused?5:6, 10)));
    }
    private void applyBorderPwd(JPasswordField f, boolean focused) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(focused ? ACCENT : BORDER_C, focused ? 2 : 1),
            BorderFactory.createEmptyBorder(focused?5:6, 10, focused?5:6, 10)));
    }

    private JButton flatBtn(String txt, Color normal, Color hover) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(normal);
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(32, 28));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(hover); }
            @Override public void mouseExited (MouseEvent e) { b.setForeground(normal); }
        });
        return b;
    }

    /** Efecto de sacudida horizontal al fallar el login */
    private void sacudir(JComponent comp) {
        Point orig = comp.getLocation();
        int[] offsets = {-10, 10, -8, 8, -5, 5, -2, 2, 0};
        int[] idx = {0};
        Timer t = new Timer(35, null);
        t.addActionListener(e -> {
            if (idx[0] < offsets.length) {
                comp.setLocation(orig.x + offsets[idx[0]], orig.y);
                idx[0]++;
            } else {
                comp.setLocation(orig);
                ((Timer) e.getSource()).stop();
            }
        });
        t.start();
    }

    private void addDragSupport(Component comp) {
        Point[] start = {null};
        comp.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { start[0] = e.getPoint(); }
        });
        comp.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (start[0] != null) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - start[0].x,
                                loc.y + e.getY() - start[0].y);
                }
            }
        });
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

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
