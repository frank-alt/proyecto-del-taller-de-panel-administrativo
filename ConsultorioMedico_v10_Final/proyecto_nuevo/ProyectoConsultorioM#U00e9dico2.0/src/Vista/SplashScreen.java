package Vista;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Pantalla de bienvenida personalizada.
 * Aparece 3 segundos después del login con animación de entrada,
 * luego lanza la InterfazGrafica principal.
 */
public class SplashScreen extends JWindow {

    static final Color BG       = new Color(0x0D1B2A);
    static final Color CARD     = new Color(0x16263E);
    static final Color ACCENT   = new Color(0x00C9A7);
    static final Color TXT      = new Color(0xE8F4F8);
    static final Color MUTED    = new Color(0x7A9BB5);
    static final Color BORDER_C = new Color(0x1E3A5F);

    // Valores animados
    private float   alpha        = 0f;    // fade in general
    private float   logoScale    = 0.4f;  // escala del logo (crece)
    private float   textAlpha    = 0f;    // aparición del texto
    private float   barProgress  = 0f;    // barra de carga 0..1
    private float   animPhase    = 0f;    // ondas decorativas
    private int     dotCount     = 0;     // puntitos animados "..."
    private boolean fadeOut      = false; // fase de salida

    // Etiquetas que se actualizan
    private JLabel lblBienvenido, lblNombre, lblSub, lblDots;
    private Timer  masterTimer;

    private final String nombreUsuario;
    private final Runnable onFinish;

    /**
     * @param nombreUsuario  Nombre que se muestra en la bienvenida
     * @param onFinish       Acción a ejecutar cuando termina la animación
     */
    public SplashScreen(String nombreUsuario, Runnable onFinish) {
        this.nombreUsuario = nombreUsuario;
        this.onFinish      = onFinish;
        buildUI();
    }

    private void buildUI() {
        setSize(620, 380);
        setLocationRelativeTo(null);

        // Canvas principal — toda la animación se pinta aquí
        JPanel canvas = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // ── Fondo con gradiente ───────────────────────────────────────
                GradientPaint bg = new GradientPaint(
                    0, 0,             new Color(0x0A1628),
                    getWidth(), getHeight(), new Color(0x0D2540)
                );
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // ── Ondas decorativas ─────────────────────────────────────────
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                g2.setColor(ACCENT);
                double w1 = Math.sin(animPhase) * 40;
                g2.fill(new Ellipse2D.Double(-100 + w1, -100, 420, 420));
                g2.fill(new Ellipse2D.Double(getWidth()-220 - w1/2,
                                              getHeight()-220, 340, 340));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
                g2.setColor(new Color(0x7C5CBF));
                g2.fill(new Ellipse2D.Double(getWidth()/2-80, -60, 240, 240));

                // ── Borde de la ventana ───────────────────────────────────────
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(BORDER_C);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);

                // ── Logo animado (escala + fade) ──────────────────────────────
                float logoAlpha = Math.min(1f, alpha * 2);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, logoAlpha));
                int cx = getWidth() / 2;
                int logoY = 80;
                // círculo detrás del logo
                g2.setColor(new Color(0x00C9A7));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, logoAlpha * 0.15f));
                int circR = (int)(70 * logoScale);
                g2.fillOval(cx - circR, logoY - circR + 20, circR*2, circR*2);

                // emoji del logo escalado
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, logoAlpha));
                Font logoFont = new Font("Segoe UI Emoji", Font.PLAIN, (int)(52 * logoScale));
                g2.setFont(logoFont);
                FontMetrics fm = g2.getFontMetrics();
                String logoStr = "🏥";
                g2.drawString(logoStr,
                    cx - fm.stringWidth(logoStr)/2,
                    logoY + fm.getAscent()/2 + 20);

                // ── Barra de progreso ─────────────────────────────────────────
                int barW = 320; int barH = 5;
                int barX = cx - barW/2; int barY = getHeight() - 55;
                // fondo barra
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.4f));
                g2.setColor(BORDER_C);
                g2.fill(new RoundRectangle2D.Double(barX, barY, barW, barH, barH, barH));
                // progreso
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                GradientPaint barGrad = new GradientPaint(
                    barX, 0, ACCENT,
                    barX + barW, 0, new Color(0x7C5CBF)
                );
                g2.setPaint(barGrad);
                int filled = (int)(barW * barProgress);
                if (filled > 0)
                    g2.fill(new RoundRectangle2D.Double(barX, barY, filled, barH, barH, barH));

                // brillo en la barra
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.6f));
                g2.setColor(Color.WHITE);
                if (filled > 6)
                    g2.fill(new RoundRectangle2D.Double(barX, barY, filled, barH/2.0, barH/2.0, barH/2.0));

                g2.dispose();
            }
        };
        canvas.setBackground(BG);

        // ── Etiquetas de texto (se posicionan absolutas) ─────────────────────
        lblBienvenido = makeLabel("Bienvenido,", new Font("Segoe UI", Font.PLAIN, 16), MUTED);
        lblNombre     = makeLabel(nombreUsuario, new Font("Segoe UI", Font.BOLD,  32), ACCENT);
        lblSub        = makeLabel("Sistema de Gestión de Consultorio Médico",
                                   new Font("Segoe UI", Font.PLAIN, 13), MUTED);
        lblDots       = makeLabel("Iniciando sistema",
                                   new Font("Segoe UI", Font.PLAIN, 11), new Color(0x3A5A7A));

        // posiciones centradas
        int cw = 620;
        lblBienvenido.setBounds(0, 170, cw, 28);
        lblNombre    .setBounds(0, 198, cw, 48);
        lblSub       .setBounds(0, 248, cw, 22);
        lblDots      .setBounds(0, 300, cw, 20);

        canvas.add(lblBienvenido);
        canvas.add(lblNombre);
        canvas.add(lblSub);
        canvas.add(lblDots);

        setContentPane(canvas);

        // hacer la ventana redondeada si el SO lo soporta
        try {
        setOpacity(1.0f);
        setShape(new RoundRectangle2D.Double(0, 0, 620, 380, 20, 20));
        } catch (Exception ignored) {}

        // ── Timer maestro: 60 fps ─────────────────────────────────────────────
        long[] startTime  = { System.currentTimeMillis() };
        int[]  dotTick    = { 0 };
        int    totalMs    = 3200; // duración total visible

        masterTimer = new Timer(16, null);
        masterTimer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime[0];
            float t = Math.min(1f, elapsed / (float) totalMs);

            animPhase += 0.025f;

            // ── Fase 1 (0–30%): fade in + logo crece ─────────────────────────
            if (t < 0.30f) {
                float p = t / 0.30f;
                alpha     = easeOut(p);
                logoScale = 0.4f + 0.6f * easeOut(p);
                textAlpha = 0f;
                barProgress = 0f;
            }
            // ── Fase 2 (30–60%): texto aparece + barra avanza ────────────────
            else if (t < 0.60f) {
                float p = (t - 0.30f) / 0.30f;
                alpha     = 1f;
                logoScale = 1f;
                textAlpha = easeOut(p);
                barProgress = 0.5f * easeOut(p);
            }
            // ── Fase 3 (60–85%): barra completa ──────────────────────────────
            else if (t < 0.85f) {
                float p = (t - 0.60f) / 0.25f;
                textAlpha   = 1f;
                barProgress = 0.5f + 0.5f * easeOut(p);
            }
            // ── Fase 4 (85–100%): fade out ────────────────────────────────────
            else {
                float p = (t - 0.85f) / 0.15f;
                alpha = 1f - easeIn(Math.min(1f, p));
                fadeOut = true;
            }

            // puntitos animados
            dotTick[0]++;
            if (dotTick[0] % 20 == 0) {
                dotCount = (dotCount + 1) % 4;
                String dots = "Iniciando sistema" + ".".repeat(dotCount);
                lblDots.setText(dots);
            }

            // aplicar alpha a las etiquetas
            setLabelAlpha(lblBienvenido, textAlpha);
            setLabelAlpha(lblNombre,     textAlpha);
            setLabelAlpha(lblSub,        textAlpha * 0.85f);
            setLabelAlpha(lblDots,       textAlpha * 0.6f);

            repaint();

            // terminar
            if (t >= 1f) {
                masterTimer.stop();
                dispose();
                SwingUtilities.invokeLater(onFinish);
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(color);
        l.setOpaque(false);
        return l;
    }

    private void setLabelAlpha(JLabel l, float a) {
        // No hay setAlpha en JLabel, usamos un color con alpha
        Color base = l.getForeground();
        int alpha8 = Math.max(0, Math.min(255, (int)(a * 255)));
        l.setForeground(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha8));
    }

    /** Ease out cúbico: arranca rápido, frena suave */
    private float easeOut(float t) {
        return 1f - (1f - t) * (1f - t) * (1f - t);
    }
    /** Ease in cuadrático: arranca suave, acelera */
    private float easeIn(float t) {
        return t * t;
    }

    /** Mostrar y arrancar la animación */
    public void mostrar() {
        setVisible(true);
        masterTimer.start();
    }
}
