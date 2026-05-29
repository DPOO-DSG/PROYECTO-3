package presentacion;

import logica.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * PLienzo: panel de gráficas. Implementa IObserver para recibir notificaciones del Controlador.
 * Dibuja 3 gráficas usando Java2D puro (sin dependencias externas):
 *   1. Pastel: distribución copias venta vs préstamo de un juego
 *   2. Barras: ventas cafetería vs juegos en 5 días
 *   3. Líneas: evolución de reservas en la semana
 */
public class PLienzo extends JPanel implements IObserver {

    private Controlador controlador;

    // Datos para las gráficas
    private String juegoSeleccionado = null;
    private int[] distribucionJuego = {0, 0}; // [venta, prestamo]
    private LinkedHashMap<String, double[]> ventasPorDia = new LinkedHashMap<>();
    private int[] reservasSemana = new int[7];

    // Colores corporativos
    private static final Color COLOR_VENTA   = new Color(70, 130, 180);
    private static final Color COLOR_PRESTAMO = new Color(220, 80, 80);
    private static final Color COLOR_CAFETERIA = new Color(70, 130, 180);
    private static final Color COLOR_JUEGOS   = new Color(220, 80, 80);
    private static final Color COLOR_LINEA    = new Color(220, 80, 80);
    private static final Color FONDO_GRAFICA  = new Color(245, 245, 245);
    private static final Color COLOR_GRID     = new Color(200, 200, 200);

    public PLienzo(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new GridLayout(1, 3, 8, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        actualizarDatos();
    }

    // =========================================================
    // IOBSERVER
    // =========================================================
    @Override
    public void actualizar() {
        actualizarDatos();
        repaint();
    }

    private void actualizarDatos() {
        // Distribución de juego
        if (juegoSeleccionado != null) {
            distribucionJuego = controlador.getDistribucionJuego(juegoSeleccionado);
        }
        // Ventas últimos 5 días
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = fin.minusDays(4);
        HashMap<String, double[]> raw = controlador.getVentasPorRango(inicio, fin);
        ventasPorDia.clear();
        // Asegurar que aparezcan los 5 días aunque no haya ventas
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
        for (int i = 0; i < 5; i++) {
            String dia = inicio.plusDays(i).format(fmt);
            ventasPorDia.put(dia, raw.getOrDefault(dia, new double[]{0, 0}));
        }
        // Reservas por semana
        reservasSemana = controlador.getReservasPorSemana();
    }

    /** Cambia el juego para la gráfica de pastel y refresca. */
    public void setJuegoSeleccionado(String nombre) {
        this.juegoSeleccionado = nombre;
        actualizarDatos();
        repaint();
    }

    // =========================================================
    // PINTAR
    // =========================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int tercio = w / 3;

        // Pastel
        dibujarPastel(g2, 0, 0, tercio, h);
        // Barras
        dibujarBarras(g2, tercio, 0, tercio, h);
        // Líneas
        dibujarLineas(g2, tercio * 2, 0, tercio, h);
    }

    // =========================================================
    // GRÁFICA 1: PASTEL
    // =========================================================
    private void dibujarPastel(Graphics2D g, int x, int y, int w, int h) {
        // Fondo
        g.setColor(FONDO_GRAFICA);
        g.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);
        g.setColor(COLOR_GRID);
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);

        // Título
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        String titulo = juegoSeleccionado != null
                ? "Disponibilidad: " + juegoSeleccionado
                : "Distribución (seleccione juego)";
        g.drawString(titulo, x + 8, y + 20);

        int total = distribucionJuego[0] + distribucionJuego[1];
        int cx = x + w / 2;
        int cy = y + h / 2 + 10;
        int radio = Math.min(w, h) / 2 - 40;

        if (total == 0 || radio < 10) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString("Sin datos", cx - 25, cy);
        } else {
            double angVenta = 360.0 * distribucionJuego[0] / total;
            double angPrestamo = 360.0 - angVenta;

            // Sector venta
            g.setColor(COLOR_VENTA);
            g.fillArc(cx - radio, cy - radio, radio * 2, radio * 2, 0, (int) angVenta);

            // Sector préstamo
            g.setColor(COLOR_PRESTAMO);
            g.fillArc(cx - radio, cy - radio, radio * 2, radio * 2,
                    (int) angVenta, (int) angPrestamo);

            // Borde
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2));
            g.drawArc(cx - radio, cy - radio, radio * 2, radio * 2, 0, 360);
            g.setStroke(new BasicStroke(1));
        }

        // Leyenda
        int ly = y + h - 40;
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(COLOR_VENTA);
        g.fillRect(x + 8, ly, 12, 10);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Inventario (" + distribucionJuego[0] + ")", x + 24, ly + 10);

        g.setColor(COLOR_PRESTAMO);
        g.fillRect(x + 8, ly + 15, 12, 10);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Préstamo (" + distribucionJuego[1] + ")", x + 24, ly + 25);
    }

    // =========================================================
    // GRÁFICA 2: BARRAS
    // =========================================================
    private void dibujarBarras(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(FONDO_GRAFICA);
        g.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);
        g.setColor(COLOR_GRID);
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("Ventas por período (últimos 5 días)", x + 8, y + 20);

        int margenIzq = x + 45;
        int margenDer = x + w - 10;
        int margenSup = y + 30;
        int margenInf = y + h - 50;
        int areaAncho = margenDer - margenIzq;
        int areaAlto  = margenInf - margenSup;

        // Valor máximo
        double maxVal = 1;
        for (double[] vals : ventasPorDia.values()) {
            maxVal = Math.max(maxVal, Math.max(vals[0], vals[1]));
        }

        // Ejes
        g.setColor(Color.GRAY);
        g.drawLine(margenIzq, margenSup, margenIzq, margenInf);
        g.drawLine(margenIzq, margenInf, margenDer, margenInf);

        // Grillas horizontales
        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g.setColor(COLOR_GRID);
        for (int i = 1; i <= 4; i++) {
            int yGrid = margenInf - (int)(areaAlto * i / 4.0);
            g.drawLine(margenIzq, yGrid, margenDer, yGrid);
            g.setColor(Color.GRAY);
            g.drawString(String.format("%.0f", maxVal * i / 4), x + 2, yGrid + 4);
            g.setColor(COLOR_GRID);
        }

        ArrayList<String> dias = new ArrayList<>(ventasPorDia.keySet());
        int n = dias.size();
        if (n == 0) return;

        int grupoPaso = areaAncho / n;
        int barraAncho = Math.max(4, grupoPaso / 3);

        for (int i = 0; i < n; i++) {
            double[] vals = ventasPorDia.get(dias.get(i));
            int grupoX = margenIzq + i * grupoPaso + grupoPaso / 2;

            // Barra cafetería
            int altoCaf = (int)(areaAlto * vals[0] / maxVal);
            g.setColor(COLOR_CAFETERIA);
            g.fillRect(grupoX - barraAncho - 1, margenInf - altoCaf, barraAncho, altoCaf);

            // Barra juegos
            int altoJuegos = (int)(areaAlto * vals[1] / maxVal);
            g.setColor(COLOR_JUEGOS);
            g.fillRect(grupoX + 1, margenInf - altoJuegos, barraAncho, altoJuegos);

            // Etiqueta día
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g.drawString(dias.get(i), grupoX - 10, margenInf + 12);
        }

        // Leyenda
        int ly = y + h - 18;
        g.setColor(COLOR_CAFETERIA);
        g.fillRect(x + 8, ly, 10, 8);
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g.drawString("Cafetería", x + 20, ly + 8);

        g.setColor(COLOR_JUEGOS);
        g.fillRect(x + 75, ly, 10, 8);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Juegos", x + 87, ly + 8);
    }

    // =========================================================
    // GRÁFICA 3: LÍNEAS
    // =========================================================
    private void dibujarLineas(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(FONDO_GRAFICA);
        g.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);
        g.setColor(COLOR_GRID);
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("Reservas por semana", x + 8, y + 20);

        int margenIzq = x + 40;
        int margenDer = x + w - 10;
        int margenSup = y + 30;
        int margenInf = y + h - 40;
        int areaAncho = margenDer - margenIzq;
        int areaAlto  = margenInf - margenSup;

        // Máximo
        int maxVal = 1;
        for (int v : reservasSemana) maxVal = Math.max(maxVal, v);

        // Ejes
        g.setColor(Color.GRAY);
        g.drawLine(margenIzq, margenSup, margenIzq, margenInf);
        g.drawLine(margenIzq, margenInf, margenDer, margenInf);

        // Grillas
        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        for (int i = 1; i <= 4; i++) {
            int yGrid = margenInf - (int)(areaAlto * i / 4.0);
            g.setColor(COLOR_GRID);
            g.drawLine(margenIzq, yGrid, margenDer, yGrid);
            g.setColor(Color.GRAY);
            g.drawString("" + (maxVal * i / 4), x + 2, yGrid + 4);
        }

        String[] etiquetas = {"Lun","Mar","Mié","Jue","Vie","Sáb","Dom"};
        int paso = areaAncho / 6;

        // Puntos y línea
        int[] puntosX = new int[7];
        int[] puntosY = new int[7];
        for (int i = 0; i < 7; i++) {
            puntosX[i] = margenIzq + i * paso;
            puntosY[i] = margenInf - (int)(areaAlto * reservasSemana[i] / (double) maxVal);
        }

        g.setColor(COLOR_LINEA);
        g.setStroke(new BasicStroke(2));
        for (int i = 0; i < 6; i++) {
            g.drawLine(puntosX[i], puntosY[i], puntosX[i + 1], puntosY[i + 1]);
        }
        g.setStroke(new BasicStroke(1));

        // Círculos y valores
        for (int i = 0; i < 7; i++) {
            g.setColor(COLOR_LINEA);
            g.fillOval(puntosX[i] - 4, puntosY[i] - 4, 8, 8);
            g.setColor(Color.WHITE);
            g.fillOval(puntosX[i] - 2, puntosY[i] - 2, 4, 4);

            // Etiqueta día
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g.drawString(etiquetas[i], puntosX[i] - 10, margenInf + 12);

            // Valor encima del punto
            if (reservasSemana[i] > 0) {
                g.drawString("" + reservasSemana[i], puntosX[i] - 4, puntosY[i] - 6);
            }
        }

        // Leyenda
        int ly = y + h - 15;
        g.setColor(COLOR_LINEA);
        g.setStroke(new BasicStroke(2));
        g.drawLine(x + 8, ly + 4, x + 20, ly + 4);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g.drawString("Reservas", x + 22, ly + 8);
    }
}
