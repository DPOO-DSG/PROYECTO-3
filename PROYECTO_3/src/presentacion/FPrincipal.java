package presentacion;

import logica.Cafe;
import persistencia.PersistenciaCafe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * FPrincipal: ventana principal de la aplicación. Extiende JFrame.
 * Contiene composición con PFormulario (panel izquierdo) y PLienzo (panel derecho, gráficas).
 * Conecta ambos a través del Controlador.
 */
public class FPrincipal extends JFrame {

    private Controlador controlador;
    private PFormulario pFormulario;
    private PLienzo     pLienzo;

    public FPrincipal(Cafe cafe) {
        super("Dulces & Dados — Sistema de Gestión");

        // Controlador MVC
        controlador = new Controlador(cafe);

        // Paneles
        pLienzo    = new PLienzo(controlador);
        pFormulario = new PFormulario(controlador, pLienzo);

        // Registrar el lienzo como observador
        controlador.agregarObservador(pLienzo);

        inicializarVentana();
    }

    private void inicializarVentana() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel izquierdo: formularios (55% del ancho)
        JScrollPane scrollFormulario = new JScrollPane(pFormulario);
        scrollFormulario.setBorder(BorderFactory.createTitledBorder("Gestión"));
        scrollFormulario.setPreferredSize(new Dimension(520, 0));

        // Panel derecho: gráficas (45% del ancho)
        JPanel panelGraficas = new JPanel(new BorderLayout());
        panelGraficas.setBorder(BorderFactory.createTitledBorder("Visualizaciones en tiempo real"));
        panelGraficas.setPreferredSize(new Dimension(680, 0));
        panelGraficas.setMinimumSize(new Dimension(400, 200));
        panelGraficas.add(pLienzo, BorderLayout.CENTER);

        // Separador
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollFormulario, panelGraficas);
        split.setDividerLocation(520);
        split.setResizeWeight(0.43);
        split.setOneTouchExpandable(true);

        add(split, BorderLayout.CENTER);

        // Barra de estado inferior
        JLabel statusBar = new JLabel("  Dulces & Dados — Lista para usar");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 11));
        add(statusBar, BorderLayout.SOUTH);

        // Guardar al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        FPrincipal.this,
                        "¿Desea guardar antes de salir?",
                        "Salir",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controlador.guardar();
                    dispose();
                    System.exit(0);
                } else if (confirm == JOptionPane.NO_OPTION) {
                    dispose();
                    System.exit(0);
                }
                // CANCEL: no hace nada, la ventana queda abierta
            }
        });

        pack();
        setSize(1200, 720);
        setMinimumSize(new Dimension(900, 500));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        // Look and feel nativo del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            Cafe cafe = PersistenciaCafe.cargar();

            if (cafe == null) {
                cafe = new Cafe(50, 10);
                cafe.inicializarTurnos();
                cafe.inicializarMesas(10, 4);
            }

            if (cafe.getAdministradores().isEmpty()) {
                cafe.crearAdministrador("admin", "admin");
            }

            new FPrincipal(cafe);
        });
    }
}
