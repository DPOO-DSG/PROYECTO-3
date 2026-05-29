package presentacion;

import logica.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * PFormulario: panel izquierdo (JPanel) que contiene todos los formularios y menús.
 * Delega TODA la lógica al Controlador. No llama directamente a Cafe.
 */
public class PFormulario extends JPanel {

    private Controlador controlador;
    private PLienzo lienzo;

    // Usuario autenticado en la sesión actual
    private Object usuarioActual = null;

    // Panel con CardLayout para mostrar distintas vistas
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private FPrincipal ventana;


    // Constantes de tarjetas
    private static final String CARD_INICIO     = "INICIO";
    private static final String CARD_LOGIN      = "LOGIN";
    private static final String CARD_MENU_CLI   = "MENU_CLIENTE";
    private static final String CARD_MENU_EMP   = "MENU_EMPLEADO";
    private static final String CARD_MENU_ADM   = "MENU_ADMIN";
    

    public PFormulario(Controlador controlador, PLienzo lienzo, FPrincipal ventana) {
        this.controlador = controlador;
        this.lienzo = lienzo;
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        cardPanel.add(crearPanelInicio(),    CARD_INICIO);
        cardPanel.add(crearPanelLogin(),     CARD_LOGIN);

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, CARD_INICIO);
    }

    // =========================================================
    // HELPERS DE UI
    // =========================================================
    private JButton btn(String texto) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        return b;
    }

    private JLabel titulo(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return l;
    }

    private void mostrar(String card) {
        cardLayout.show(cardPanel, card);
    }

    private void msg(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    private String input(String prompt) {
        return JOptionPane.showInputDialog(this, prompt);
    }

    private boolean confirm(String prompt) {
        return JOptionPane.showConfirmDialog(this, prompt, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void reemplazarTarjeta(String nombre, Component panel) {
        cardPanel.add(panel, nombre);
        cardLayout.show(cardPanel, nombre);
    }

    // =========================================================
    // PANEL INICIO
    // =========================================================
    private JPanel crearPanelInicio() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        p.add(titulo("=== SISTEMA DULCES & DADOS ==="), gbc);

        String[] opciones = {
            "Ingresar como Cliente",
            "Ingresar como Empleado",
            "Ingresar como Administrador",
            "Registrar nuevo cliente"
        };

        for (String op : opciones) {
            gbc.gridy++;
            JButton b = btn(op);
            String opFinal = op;
            b.addActionListener(e -> manejarOpcionInicio(opFinal));
            p.add(b, gbc);
        }

        return p;
    }

    private void manejarOpcionInicio(String opcion) {
        switch (opcion) {
            case "Ingresar como Cliente":
                mostrarLoginPara("CLIENTE");
                break;
            case "Ingresar como Empleado":
                mostrarLoginPara("EMPLEADO");
                break;
            case "Ingresar como Administrador":
                mostrarLoginPara("ADMIN");
                break;
            case "Registrar nuevo cliente":
                registrarCliente();
                break;
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================
    private String tipoLoginActual;

    private JPanel crearPanelLogin() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        wrapper.add(titulo("Iniciar Sesión"), gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        wrapper.add(new JLabel("Login:"), gbc);
        JTextField fLogin = new JTextField(15);
        gbc.gridx = 1;
        wrapper.add(fLogin, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        wrapper.add(new JLabel("Password:"), gbc);
        JPasswordField fPass = new JPasswordField(15);
        gbc.gridx = 1;
        wrapper.add(fPass, gbc);

        JButton btnLogin = btn("Ingresar");
        JButton btnVolver = btn("Volver");
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel botones = new JPanel(new FlowLayout());
        botones.setBackground(Color.WHITE);
        botones.add(btnLogin);
        botones.add(btnVolver);
        wrapper.add(botones, gbc);

        btnLogin.addActionListener(e -> {
            String login = fLogin.getText().trim();
            String pass  = new String(fPass.getPassword()).trim();
            if (login.isEmpty() || pass.isEmpty()) { msg("Complete los campos"); return; }
            Object u = controlador.login(login, pass);
            if (u == null) { msg("Credenciales inválidas"); return; }

            usuarioActual = u;
            fLogin.setText(""); fPass.setText("");

            if (u instanceof Administrador && "ADMIN".equals(tipoLoginActual)) {
                abrirMenuAdmin((Administrador) u);
            } else if (u instanceof Empleado && "EMPLEADO".equals(tipoLoginActual)) {
                abrirMenuEmpleado((Empleado) u);
            } else if (u instanceof Cliente && "CLIENTE".equals(tipoLoginActual)) {
                abrirMenuCliente((Cliente) u);
            } else {
                msg("Tipo de usuario no corresponde al ingreso seleccionado");
            }
        });

        btnVolver.addActionListener(e -> mostrar(CARD_INICIO));
        return wrapper;
    }

    private void mostrarLoginPara(String tipo) {
        tipoLoginActual = tipo;
        mostrar(CARD_LOGIN);
    }

    // =========================================================
    // REGISTRO CLIENTE
    // =========================================================
    private void registrarCliente() {
        String login = input("Login:");
        if (login == null || login.trim().isEmpty()) return;
        String pass  = input("Password:");
        if (pass == null || pass.trim().isEmpty()) return;
        boolean ok = controlador.registrarCliente(login.trim(), pass.trim());
        msg(ok ? "Cliente creado correctamente" : "El login ya existe");
    }

    // =========================================================
    // MENÚ CLIENTE
    // =========================================================
    private void abrirMenuCliente(Cliente c) {
    	ventana.setLienzoVisible(false);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        p.add(titulo("=== MENÚ CLIENTE: " + c.getLogin() + " ==="), gbc);

        String[][] items = {
            {"Reservar mesa",                "reservarMesa"},
            {"Consultar puntos de fidelidad","consultarPuntos"},
            {"Ver mis pedidos",              "verPedidos"},
            {"Consultar facturas",           "consultarFacturas"},
            {"Consultar menú del café",      "verMenu"},
            {"Solicitar préstamo de juego",  "prestamoJuego"},
            {"Catálogo de préstamos",        "catalogoPrestamo"},
            {"Guardar juego favorito",       "guardarFavorito"},
            {"Devolver juego prestado",      "devolverPrestamo"},
            {"Catálogo de ventas",           "catalogoVentas"},
            {"Mis juegos favoritos",         "juegosFavoritos"},
            {"Inscribirse a torneo",         "inscribirTorneo"},
            {"Borrar inscripción torneo",    "borrarInscripcion"},
        };

        for (String[] item : items) {
            gbc.gridy++;
            JButton b = btn(item[0]);
            String accion = item[1];
            b.addActionListener(e -> manejarAccionCliente(accion, c));
            p.add(b, gbc);
        }

        gbc.gridy++;
        JButton btnSalir = btn("Cerrar sesión");
        btnSalir.addActionListener(e -> { usuarioActual = null; ventana.setLienzoVisible(false); mostrar(CARD_INICIO); });
        p.add(btnSalir, gbc);

        reemplazarTarjeta(CARD_MENU_CLI, new JScrollPane(p));
    }

    private void manejarAccionCliente(String accion, Cliente c) {
        switch (accion) {
            case "reservarMesa":         reservarMesa(c); break;
            case "consultarPuntos":      msg("Tienes " + controlador.consultarPuntos(c) + " puntos"); break;
            case "verPedidos":           verPedidosCliente(c); break;
            case "consultarFacturas":    verFacturaCliente(c); break;
            case "verMenu":              verMenuCafe(); break;
            case "prestamoJuego":        prestamoJuego(c, null); break;
            case "catalogoPrestamo":     mostrarStockTabla("PRESTAMO"); break;
            case "guardarFavorito":      guardarJuegoFavorito(c); break;
            case "devolverPrestamo":     devolverPrestamo(c); break;
            case "catalogoVentas":       mostrarStockTabla("VENTA"); break;
            case "juegosFavoritos":      verJuegosFavoritos(c); break;
            case "inscribirTorneo":      inscribirTorneo(c); break;
            case "borrarInscripcion":    borrarInscripcionTorneo(c); break;
        }
    }

    // =========================================================
    // MENÚ EMPLEADO
    // =========================================================
    private void abrirMenuEmpleado(Empleado e) {
    	ventana.setLienzoVisible(false);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        p.add(titulo("=== MENÚ EMPLEADO: " + e.getLogin() + " ==="), gbc);

        String[][] items = {
            {"Consultar mis turnos",         "consultarTurnos"},
            {"Solicitar cambio de turno",    "cambioTurno"},
            {"Hacer pedido a cliente",       "hacerPedido"},
            {"Crear factura",                "crearFactura"},
            {"Solicitar platillo nuevo",     "solicitarPlatillo"},
            {"Solicitar préstamo de juego",  "prestamoJuego"},
            {"Devolver juego prestado",      "devolverPrestamo"},
            {"Guardar juego favorito",       "guardarFavorito"},
            {"Mis juegos favoritos",         "juegosFavoritos"},
            {"Aprender juego difícil",       "aprenderJuego"},
            {"Comprar juego (descuento 20%)", "comprarJuego"},
            {"Inscribirse a torneo",         "inscribirTorneo"},
            {"Borrar inscripción torneo",    "borrarInscripcion"},
        };

        for (String[] item : items) {
            gbc.gridy++;
            JButton b = btn(item[0]);
            String accion = item[1];
            b.addActionListener(ev -> manejarAccionEmpleado(accion, e));
            p.add(b, gbc);
        }

        gbc.gridy++;
        JButton btnSalir = btn("Cerrar sesión");
        btnSalir.addActionListener(ev -> { usuarioActual = null;ventana.setLienzoVisible(false); mostrar(CARD_INICIO); });
        p.add(btnSalir, gbc);

        reemplazarTarjeta(CARD_MENU_EMP, new JScrollPane(p));
    }

    private void manejarAccionEmpleado(String accion, Empleado e) {
        switch (accion) {
            case "consultarTurnos":   verTurnosEmpleado(e); break;
            case "cambioTurno":       solicitarCambioTurno(e); break;
            case "hacerPedido":       hacerPedidoEmpleado(e); break;
            case "crearFactura":      crearFacturaEmpleado(e); break;
            case "solicitarPlatillo": solicitarPlatilloEmpleado(); break;
            case "prestamoJuego":     prestamoJuego(null, e); break;
            case "devolverPrestamo":  devolverPrestamo(e); break;
            case "guardarFavorito":   guardarJuegoFavorito(e); break;
            case "juegosFavoritos":   verJuegosFavoritos(e); break;
            case "aprenderJuego":     aprenderJuegoDificil(e); break;
            case "comprarJuego":      comprarJuegoEmpleado(e); break;
            case "inscribirTorneo":   inscribirTorneo(e); break;
            case "borrarInscripcion": borrarInscripcionTorneo(e); break;
        }
    }

    // =========================================================
    // MENÚ ADMINISTRADOR
    // =========================================================
    private void abrirMenuAdmin(Administrador a) {
    	ventana.setLienzoVisible(true);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        p.add(titulo("=== MENÚ ADMINISTRADOR ==="), gbc);

        String[][] items = {
            {"Crear mesero",                          "crearMesero"},
            {"Crear cocinero",                        "crearCocinero"},
            {"Ver turnos del café",                   "verTurnos"},
            {"Ver solicitudes de cambio de turno",    "verSolicitudesTurno"},
            {"Aprobar / Rechazar solicitud de turno", "gestionarSolicitudTurno"},
            {"Añadir platillo al menú",               "anadirPlatillo"},
            {"Ver solicitudes de platillo",           "verSolicitudesPlatillo"},
            {"Aprobar / Rechazar solicitud platillo", "gestionarSolicitudPlatillo"},
            {"Consultar menú",                        "verMenu"},
            {"Añadir juego",                          "anadirJuego"},
            {"Gestionar inventario",                  "gestionarInventario"},
            {"Ver historial de ventas",               "historialVentas"},
            {"Ver historial de préstamos",            "historialPrestamos"},
            {"Ver empleados",                         "verEmpleados"},
            {"Ver clientes",                          "verClientes"},
            {"Crear torneo",                          "crearTorneo"},
            {"Otorgar premio torneo amistoso",        "otorgarPremio"},
            {"Seleccionar juego para gráfica",        "seleccionarJuegoGrafica"},
        };

        for (String[] item : items) {
            gbc.gridy++;
            JButton b = btn(item[0]);
            String accion = item[1];
            b.addActionListener(ev -> manejarAccionAdmin(accion));
            p.add(b, gbc);
        }

        gbc.gridy++;
        JButton btnSalir = btn("Cerrar sesión");
        btnSalir.addActionListener(ev -> { usuarioActual = null; ventana.setLienzoVisible(false); mostrar(CARD_INICIO); });
        p.add(btnSalir, gbc);

        reemplazarTarjeta(CARD_MENU_ADM, new JScrollPane(p));
    }

    private void manejarAccionAdmin(String accion) {
        switch (accion) {
            case "crearMesero":             crearMesero(); break;
            case "crearCocinero":           crearCocinero(); break;
            case "verTurnos":               verTurnosCafe(); break;
            case "verSolicitudesTurno":     verSolicitudesTurno(); break;
            case "gestionarSolicitudTurno": gestionarSolicitudTurno(); break;
            case "anadirPlatillo":          anadirPlatilloAlMenu(); break;
            case "verSolicitudesPlatillo":  verSolicitudesPlatillo(); break;
            case "gestionarSolicitudPlatillo": gestionarSolicitudPlatillo(); break;
            case "verMenu":                 verMenuCafe(); break;
            case "anadirJuego":             anadirJuego(); break;
            case "gestionarInventario":     gestionarInventario(); break;
            case "historialVentas":         verHistorialVentas(); break;
            case "historialPrestamos":      verHistorialPrestamos(); break;
            case "verEmpleados":            verEmpleados(); break;
            case "verClientes":             verClientes(); break;
            case "crearTorneo":             crearTorneo(); break;
            case "otorgarPremio":           otorgarPremio(); break;
            case "seleccionarJuegoGrafica": seleccionarJuegoParaGrafica(); break;
        }
    }

    // =========================================================
    // ACCIONES COMPARTIDAS CLIENTE / EMPLEADO
    // =========================================================

    // --- Reservar mesa (solo cliente) ---
    private void reservarMesa(Cliente c) {
        try {
            String sPersonas = input("Cantidad de personas:");
            if (sPersonas == null) return;
            int personas = Integer.parseInt(sPersonas.trim());

            boolean ninos   = confirm("¿Hay niños?");
            boolean jovenes = confirm("¿Hay jóvenes?");

            String sFecha = input("Fecha (dd/MM/yyyy HH:mm):");
            if (sFecha == null) return;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime fecha = LocalDateTime.parse(sFecha.trim(), fmt);

            msg(controlador.agendarReserva(c, personas, ninos, jovenes, fecha));
        } catch (Exception ex) {
            msg("Error: formato inválido. Use dd/MM/yyyy HH:mm");
        }
    }

    // --- Ver pedidos ---
    private void verPedidosCliente(Cliente c) {
        ArrayList<Reserva> reservas = controlador.getReservasCliente(c);
        if (reservas.isEmpty()) { msg("No tienes reservas"); return; }
        StringBuilder sb = new StringBuilder();
        for (Reserva r : reservas) {
            sb.append("Reserva: ").append(r.getId())
              .append(" | Fecha: ").append(r.getFechaReserva()).append("\n");
            for (Pedido p : r.getPedidos()) {
                sb.append("  Pedido: ").append(p).append("\n");
            }
            sb.append("---\n");
        }
        mostrarScrollDialog("Mis pedidos", sb.toString());
    }

    // --- Ver factura ---
    private void verFacturaCliente(Cliente c) {
        ArrayList<Reserva> reservas = controlador.getReservasCliente(c);
        if (reservas.isEmpty()) { msg("No tienes reservas"); return; }
        String[] opciones = reservas.stream().map(Reserva::getId).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione reserva:",
                "Facturas", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sel == null) return;
        Reserva r = reservas.stream().filter(x -> x.getId().equals(sel)).findFirst().orElse(null);
        if (r == null) return;
        CompraVenta factura = controlador.getFacturaPorReserva(r);
        msg(factura != null ? factura.toString() : "Aún no se ha generado factura");
    }

    // --- Ver menú ---
    private void verMenuCafe() {
        ArrayList<Platillo> menu = controlador.getMenu();
        if (menu.isEmpty()) { msg("El menú está vacío"); return; }
        StringBuilder sb = new StringBuilder("=== MENÚ DEL CAFÉ ===\n");
        for (Platillo p : menu) sb.append("• ").append(p).append("\n");
        mostrarScrollDialog("Menú", sb.toString());
    }

    // --- Stock en tabla ---
    private void mostrarStockTabla(String tipo) {
        HashMap<Juego, Integer> stock = tipo.equals("PRESTAMO")
                ? controlador.getStockPrestamo() : controlador.getStockVenta();
        String[] cols = {"Juego", "Stock", "Precio", "Categoría", "Difícil"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (HashMap.Entry<Juego, Integer> e : stock.entrySet()) {
            Juego j = e.getKey();
            model.addRow(new Object[]{j.getNombre(), e.getValue(), j.getprecio(),
                    j.getCategoria(), j.isDificl() ? "Sí" : "No"});
        }
        mostrarTablaDialog("Catálogo de " + tipo.toLowerCase(), model);
    }

    // --- Préstamo de juego ---
    private void prestamoJuego(Cliente c, Empleado emp) {
        ArrayList<Juego> juegos = controlador.getCatalogoPrestamo();
        if (juegos.isEmpty()) { msg("No hay juegos disponibles para préstamo"); return; }
        String[] nombres = juegos.stream().map(Juego::getNombre).toArray(String[]::new);
        String selJuego = (String) JOptionPane.showInputDialog(this, "Seleccione juego:",
                "Préstamo", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (selJuego == null) return;
        Juego juego = juegos.stream().filter(j -> j.getNombre().equals(selJuego)).findFirst().orElse(null);

        Reserva reserva = null;
        if (c != null) {
            ArrayList<Reserva> reservas = controlador.getReservasActivasCliente(c);
            if (reservas.isEmpty()) { msg("No tienes reservas activas"); return; }
            String[] idsReservas = reservas.stream().map(Reserva::getId).toArray(String[]::new);
            String selR = (String) JOptionPane.showInputDialog(this, "Seleccione reserva:",
                    "Préstamo", JOptionPane.PLAIN_MESSAGE, null, idsReservas, idsReservas[0]);
            if (selR == null) return;
            reserva = reservas.stream().filter(r -> r.getId().equals(selR)).findFirst().orElse(null);
        }

        Usuario u = c != null ? c : emp;
        msg(controlador.solicitarPrestamo(u, juego, reserva));
    }

    // --- Guardar juego favorito ---
    private void guardarJuegoFavorito(Usuario u) {
        ArrayList<Juego> catalogo = controlador.getCatalogoPrestamo();
        if (catalogo.isEmpty()) { msg("No hay juegos en el catálogo"); return; }
        String[] nombres = catalogo.stream().map(Juego::getNombre).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione juego:",
                "Favorito", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (sel == null) return;
        Juego juego = catalogo.stream().filter(j -> j.getNombre().equals(sel)).findFirst().orElse(null);
        if (juego == null) return;
        controlador.guardarJuegoFavorito(u, juego);
        msg(juego.getNombre() + " añadido a favoritos");
    }

    // --- Devolver préstamo ---
    private void devolverPrestamo(Usuario u) {
        ArrayList<Prestamo> prestamos = controlador.getPrestamosUsuario(u);
        if (prestamos.isEmpty()) { msg("No tienes préstamos activos"); return; }
        String[] opciones = prestamos.stream()
                .map(p -> p.getId() + " | " + p.getJuego().getNombre())
                .toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione préstamo a devolver:",
                "Devolución", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sel == null) return;
        String id = sel.split(" \\| ")[0];
        msg(controlador.devolverPrestamo(id));
    }

    // --- Ver juegos favoritos ---
    private void verJuegosFavoritos(Usuario u) {
        ArrayList<Juego> favs = controlador.getJuegosFavoritos(u);
        if (favs.isEmpty()) { msg("No tienes juegos favoritos"); return; }
        StringBuilder sb = new StringBuilder("=== JUEGOS FAVORITOS ===\n");
        for (Juego j : favs) sb.append("• ").append(j.getNombre()).append("\n");
        mostrarScrollDialog("Juegos favoritos", sb.toString());
    }

    // --- Inscribir torneo ---
    private void inscribirTorneo(Usuario u) {
        HashMap<String, Torneo> torneos = controlador.getTorneos();
        if (torneos.isEmpty()) { msg("No hay torneos disponibles"); return; }
        String[] nombres = torneos.keySet().toArray(new String[0]);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione torneo:",
                "Torneos", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (sel == null) return;
        String sCant = input("Cantidad de cupos:");
        if (sCant == null) return;
        try {
            int cant = Integer.parseInt(sCant.trim());
            msg(controlador.inscribirATorneo(sel, u, cant));
        } catch (NumberFormatException ex) {
            msg("Ingrese un número válido");
        }
    }

    // --- Borrar inscripción torneo ---
    private void borrarInscripcionTorneo(Usuario u) {
        HashMap<String, Torneo> torneos = controlador.getTorneos();
        if (torneos.isEmpty()) { msg("No hay torneos disponibles"); return; }
        String[] nombres = torneos.keySet().toArray(new String[0]);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione torneo:",
                "Eliminar inscripción", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (sel == null) return;
        msg(controlador.eliminarDeTorneo(sel, u));
    }

    // =========================================================
    // ACCIONES EMPLEADO
    // =========================================================

    private void verTurnosEmpleado(Empleado e) {
        ArrayList<Turno> turnos = controlador.consultarTurnosEmpleado(e);
        if (turnos.isEmpty()) { msg("No tienes turnos asignados"); return; }
        StringBuilder sb = new StringBuilder("=== TUS TURNOS ===\n");
        for (Turno t : turnos) sb.append("• ").append(t.mostrarSimple()).append("\n");
        mostrarScrollDialog("Mis turnos", sb.toString());
    }

    private void solicitarCambioTurno(Empleado emp) {
        String[] tipos = {"Cambio de turno (mismo empleado)", "Intercambio con otro empleado"};
        String sel = (String) JOptionPane.showInputDialog(this, "Tipo de cambio:",
                "Cambio de turno", JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (sel == null) return;

        ArrayList<Turno> misT = emp.getTurnos();
        if (misT.isEmpty()) { msg("No tienes turnos asignados"); return; }
        String[] misNombres = misT.stream().map(Turno::getJornada).toArray(String[]::new);
        String origen = (String) JOptionPane.showInputDialog(this, "Turno origen:",
                "Cambio turno", JOptionPane.PLAIN_MESSAGE, null, misNombres, misNombres[0]);
        if (origen == null) return;
        Turno turnoOrigen = misT.stream().filter(t -> t.getJornada().equals(origen)).findFirst().orElse(null);

        if (sel.startsWith("Cambio")) {
            HashMap<String, Turno> todos = controlador.getTurnos();
            String[] nombresT = todos.values().stream().map(Turno::getJornada).toArray(String[]::new);
            String destino = (String) JOptionPane.showInputDialog(this, "Turno destino:",
                    "Cambio turno", JOptionPane.PLAIN_MESSAGE, null, nombresT, nombresT[0]);
            if (destino == null) return;
            Turno turnoDestino = todos.values().stream()
                    .filter(t -> t.getJornada().equals(destino)).findFirst().orElse(null);
            msg(controlador.solicitarCambioTurno(emp, turnoOrigen, turnoDestino));
        } else {
            String loginOtro = input("Login del otro empleado:");
            if (loginOtro == null) return;
            Empleado otro = controlador.getEmpleados().get(loginOtro.trim());
            if (otro == null) { msg("Empleado no encontrado"); return; }
            ArrayList<Turno> turnosOtro = otro.getTurnos();
            if (turnosOtro.isEmpty()) { msg("El otro empleado no tiene turnos"); return; }
            String[] nombresOtro = turnosOtro.stream().map(Turno::getJornada).toArray(String[]::new);
            String selOtro = (String) JOptionPane.showInputDialog(this, "Turno del otro empleado:",
                    "Intercambio", JOptionPane.PLAIN_MESSAGE, null, nombresOtro, nombresOtro[0]);
            if (selOtro == null) return;
            Turno turnoOtro = turnosOtro.stream()
                    .filter(t -> t.getJornada().equals(selOtro)).findFirst().orElse(null);
            msg(controlador.solicitarIntercambioTurno(emp, otro, turnoOrigen, turnoOtro));
        }
    }

    private void hacerPedidoEmpleado(Empleado e) {
        String loginCli = input("Login del cliente:");
        if (loginCli == null) return;
        Cliente c = controlador.getClientes().get(loginCli.trim());
        if (c == null) { msg("Cliente no existe"); return; }

        ArrayList<Reserva> reservas = controlador.getReservasCliente(c);
        if (reservas.isEmpty()) { msg("El cliente no tiene reservas"); return; }
        String[] idsR = reservas.stream().map(Reserva::getId).toArray(String[]::new);
        String selR = (String) JOptionPane.showInputDialog(this, "Seleccione reserva:",
                "Pedido", JOptionPane.PLAIN_MESSAGE, null, idsR, idsR[0]);
        if (selR == null) return;
        Reserva reserva = reservas.stream().filter(r -> r.getId().equals(selR)).findFirst().orElse(null);

        // Platillos
        ArrayList<Platillo> menu = controlador.getMenu();
        ArrayList<Platillo> selPlatillos = new ArrayList<>();
        if (!menu.isEmpty()) {
            String[] nombresMenu = menu.stream().map(Platillo::getnombre).toArray(String[]::new);
            boolean mas = true;
            while (mas) {
                String selP = (String) JOptionPane.showInputDialog(this, "Agregar platillo (Cancelar = ninguno):",
                        "Pedido", JOptionPane.PLAIN_MESSAGE, null, nombresMenu, nombresMenu[0]);
                if (selP == null) break;
                menu.stream().filter(p -> p.getnombre().equals(selP)).findFirst().ifPresent(selPlatillos::add);
                mas = confirm("¿Agregar otro platillo?");
            }
        }

        // Juegos
        ArrayList<Juego> catalogo = controlador.getCatalogoVenta();
        ArrayList<Juego> selJuegos = new ArrayList<>();
        if (!catalogo.isEmpty()) {
            String[] nombresJ = catalogo.stream().map(Juego::getNombre).toArray(String[]::new);
            boolean mas = confirm("¿Agregar juegos al pedido?");
            while (mas) {
                String selJ = (String) JOptionPane.showInputDialog(this, "Seleccione juego:",
                        "Pedido", JOptionPane.PLAIN_MESSAGE, null, nombresJ, nombresJ[0]);
                if (selJ == null) break;
                catalogo.stream().filter(j -> j.getNombre().equals(selJ)).findFirst().ifPresent(selJuegos::add);
                mas = confirm("¿Agregar otro juego?");
            }
        }

        msg(controlador.crearPedido(reserva, e, selPlatillos, selJuegos));
    }

    private void crearFacturaEmpleado(Empleado e) {
        String loginCli = input("Login del cliente:");
        if (loginCli == null) return;
        Cliente c = controlador.getClientes().get(loginCli.trim());
        if (c == null) { msg("Cliente no existe"); return; }

        ArrayList<Reserva> reservas = controlador.getReservasCliente(c);
        if (reservas.isEmpty()) { msg("El cliente no tiene reservas"); return; }
        String[] idsR = reservas.stream().map(Reserva::getId).toArray(String[]::new);
        String selR = (String) JOptionPane.showInputDialog(this, "Seleccione reserva:",
                "Factura", JOptionPane.PLAIN_MESSAGE, null, idsR, idsR[0]);
        if (selR == null) return;
        Reserva r = reservas.stream().filter(res -> res.getId().equals(selR)).findFirst().orElse(null);

        try {
            double propina   = Double.parseDouble(input("Propina (0 si no hay):").trim());
            boolean puntos   = confirm("¿Usar puntos de fidelidad?");
            boolean bonoT    = confirm("¿Usar bono de torneo?");
            String  codigo   = input("Código de descuento (dejar vacío si no):");
            if (codigo == null) codigo = "";
            msg(controlador.crearFactura(c, propina, puntos, bonoT, codigo.trim(), r));
        } catch (Exception ex) {
            msg("Error: ingrese valores válidos");
        }
    }

    private void solicitarPlatilloEmpleado() {
        String[] tipos = {"Bebida", "Pastelería"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Tipo de platillo:",
                "Sugerir platillo", JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == null) return;

        String nombre = input("Nombre del platillo:");
        if (nombre == null) return;
        String sPrecio = input("Precio:");
        if (sPrecio == null) return;

        try {
            int precio = Integer.parseInt(sPrecio.trim());
            Platillo p = null;
            if (tipo.equals("Bebida")) {
                String tipoBeb = input("Tipo de bebida (fria/caliente):");
                if (tipoBeb == null) return;
                boolean alcohol = confirm("¿Es alcohólica?");
                p = new Bebida(nombre.trim(), precio, tipoBeb.trim(), alcohol);
            } else {
                String sAlerg = input("Alérgenos separados por coma:");
                ArrayList<String> alerg = new ArrayList<>();
                if (sAlerg != null && !sAlerg.trim().isEmpty()) {
                    for (String a : sAlerg.split(",")) alerg.add(a.trim());
                }
                p = new Pasteleria(nombre.trim(), precio, alerg);
            }
            controlador.crearSolicitudPlatillo(p);
            msg("Solicitud enviada correctamente");
        } catch (Exception ex) {
            msg("Error: " + ex.getMessage());
        }
    }

    private void aprenderJuegoDificil(Empleado e) {
        ArrayList<Juego> juegos = controlador.getCatalogoPrestamo();
        ArrayList<Juego> dificiles = new ArrayList<>();
        for (Juego j : juegos) if (j.isDificl()) dificiles.add(j);
        if (dificiles.isEmpty()) { msg("No hay juegos difíciles disponibles"); return; }
        String[] nombres = dificiles.stream().map(Juego::getNombre).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione juego difícil:",
                "Aprender", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (sel == null) return;
        controlador.anadirJuegoAMesero(e.getLogin(),
                dificiles.stream().filter(j -> j.getNombre().equals(sel)).findFirst().orElse(null));
        msg("Ahora sabes explicar este juego");
    }

    private void comprarJuegoEmpleado(Empleado e) {
        HashMap<Juego, Integer> stock = controlador.getStockVenta();
        if (stock.isEmpty()) { msg("No hay juegos en el inventario de ventas"); return; }
        ArrayList<Juego> lista = new ArrayList<>(stock.keySet());
        String[] opciones = lista.stream()
                .map(j -> j.getNombre() + " ($" + j.getprecio() + ") x" + stock.get(j))
                .toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione juego:",
                "Comprar juego", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sel == null) return;
        Juego juego = lista.get(java.util.Arrays.asList(opciones).indexOf(sel));
        try {
            double propina = Double.parseDouble(input("Propina (0 si no):").trim());
            msg(controlador.comprarJuegoEmpleado(e, juego, propina));
        } catch (Exception ex) {
            msg("Error: ingrese un valor válido");
        }
    }

    // =========================================================
    // ACCIONES ADMINISTRADOR
    // =========================================================

    private void crearMesero() {
        String login  = input("Login:"); if (login == null) return;
        String pass   = input("Password:"); if (pass == null) return;
        String cod    = input("Código de descuento:"); if (cod == null) return;
        String sDias  = input("Días separados por coma (ej: lunes,martes):"); if (sDias == null) return;
        ArrayList<String> dias = new ArrayList<>();
        for (String d : sDias.split(",")) dias.add(d.trim().toLowerCase());
        msg(controlador.crearMesero(login.trim(), pass.trim(), cod.trim(), dias));
    }

    private void crearCocinero() {
        String login  = input("Login:"); if (login == null) return;
        String pass   = input("Password:"); if (pass == null) return;
        String cod    = input("Código de descuento:"); if (cod == null) return;
        String sDias  = input("Días separados por coma (ej: lunes,martes):"); if (sDias == null) return;
        ArrayList<String> dias = new ArrayList<>();
        for (String d : sDias.split(",")) dias.add(d.trim().toLowerCase());
        msg(controlador.crearCocinero(login.trim(), pass.trim(), cod.trim(), dias));
    }

    private void verTurnosCafe() {
        HashMap<String, Turno> turnos = controlador.getTurnos();
        if (turnos.isEmpty()) { msg("No hay turnos creados"); return; }
        String[] orden = {"lunes","martes","miercoles","jueves","viernes","sabado","domingo"};
        StringBuilder sb = new StringBuilder("=== TURNOS DEL CAFÉ ===\n");
        for (String d : orden) {
            Turno t = turnos.get(d);
            if (t != null) sb.append(t.toString()).append("\n");
        }
        mostrarScrollDialog("Turnos del café", sb.toString());
    }

    private void verSolicitudesTurno() {
        HashMap<Integer, CambioDeTurno> pend = controlador.getSolicitudesPendientes();
        if (pend.isEmpty()) { msg("No hay solicitudes pendientes"); return; }
        StringBuilder sb = new StringBuilder("=== SOLICITUDES PENDIENTES ===\n");
        for (CambioDeTurno s : pend.values()) {
            if (s.getEmpleadoDestino() != null) {
                sb.append("ID: ").append(s.getId())
                  .append(" | Intercambio: ").append(s.getEmpleado().getLogin())
                  .append(" <-> ").append(s.getEmpleadoDestino().getLogin())
                  .append(" | ").append(s.getTurnoOriginal().getJornada())
                  .append(" <-> ").append(s.getTurnoCambio().getJornada()).append("\n");
            } else {
                sb.append("ID: ").append(s.getId())
                  .append(" | Cambio: ").append(s.getEmpleado().getLogin())
                  .append(" | ").append(s.getTurnoOriginal().getJornada())
                  .append(" -> ").append(s.getTurnoCambio().getJornada()).append("\n");
            }
        }
        mostrarScrollDialog("Solicitudes de turno", sb.toString());
    }

    private void gestionarSolicitudTurno() {
        HashMap<Integer, CambioDeTurno> pend = controlador.getSolicitudesPendientes();
        if (pend.isEmpty()) { msg("No hay solicitudes pendientes"); return; }
        String sId = input("ID de la solicitud:");
        if (sId == null) return;
        try {
            int id = Integer.parseInt(sId.trim());
            String[] ops = {"Aprobar", "Rechazar"};
            String op = (String) JOptionPane.showInputDialog(this, "¿Qué desea hacer?",
                    "Gestionar", JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
            if (op == null) return;
            boolean ok = op.equals("Aprobar")
                    ? controlador.aprobarSolicitud(id)
                    : controlador.rechazarSolicitud(id);
            msg(ok ? "Operación realizada" : "No se pudo realizar (ID no encontrado)");
        } catch (NumberFormatException ex) {
            msg("ID inválido");
        }
    }

    private void anadirPlatilloAlMenu() {
        String[] tipos = {"Bebida", "Pastelería"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Tipo de platillo:",
                "Añadir platillo", JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == null) return;
        String nombre = input("Nombre:"); if (nombre == null) return;
        String sPrecio = input("Precio:"); if (sPrecio == null) return;
        try {
            int precio = Integer.parseInt(sPrecio.trim());
            Platillo p = null;
            if (tipo.equals("Bebida")) {
                String tipoBeb = input("Tipo (fria/caliente):"); if (tipoBeb == null) return;
                boolean alc = confirm("¿Es alcohólica?");
                p = new Bebida(nombre.trim(), precio, tipoBeb.trim(), alc);
            } else {
                String sAlerg = input("Alérgenos separados por coma:");
                ArrayList<String> alerg = new ArrayList<>();
                if (sAlerg != null && !sAlerg.trim().isEmpty())
                    for (String a : sAlerg.split(",")) alerg.add(a.trim());
                p = new Pasteleria(nombre.trim(), precio, alerg);
            }
            boolean ok = controlador.anadirPlatilloAMenu(p);
            msg(ok ? "Platillo añadido" : "No se pudo añadir (posible duplicado)");
        } catch (Exception ex) {
            msg("Error: " + ex.getMessage());
        }
    }

    private void verSolicitudesPlatillo() {
        HashMap<Integer, Platillo> sols = controlador.getSolicitudesPlatillo();
        if (sols.isEmpty()) { msg("No hay solicitudes de platillo"); return; }
        StringBuilder sb = new StringBuilder("=== SOLICITUDES DE PLATILLO ===\n");
        for (HashMap.Entry<Integer, Platillo> e : sols.entrySet()) {
            sb.append("ID: ").append(e.getKey()).append(" | ").append(e.getValue()).append("\n");
        }
        mostrarScrollDialog("Solicitudes platillo", sb.toString());
    }

    private void gestionarSolicitudPlatillo() {
        HashMap<Integer, Platillo> sols = controlador.getSolicitudesPlatillo();
        if (sols.isEmpty()) { msg("No hay solicitudes"); return; }
        String sId = input("ID de la solicitud:"); if (sId == null) return;
        try {
            int id = Integer.parseInt(sId.trim());
            String[] ops = {"Aprobar", "Rechazar"};
            String op = (String) JOptionPane.showInputDialog(this, "¿Qué desea hacer?",
                    "Gestionar platillo", JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
            if (op == null) return;
            boolean ok = op.equals("Aprobar")
                    ? controlador.aprobarSolicitudPlatillo(id)
                    : controlador.rechazarSolicitudPlatillo(id);
            msg(ok ? "Operación realizada" : "ID no encontrado");
        } catch (NumberFormatException ex) {
            msg("ID inválido");
        }
    }

    private void anadirJuego() {
        try {
            String nombre    = input("Nombre del juego:"); if (nombre == null) return;
            String categoria = input("Categoría (Tablero/Cartas/Accion):"); if (categoria == null) return;
            int precio       = Integer.parseInt(input("Precio:").trim());
            int anio         = Integer.parseInt(input("Año de publicación:").trim());
            int cantidad     = Integer.parseInt(input("Cantidad inicial:").trim());
            int minJug       = Integer.parseInt(input("Mínimo de jugadores:").trim());
            int maxJug       = Integer.parseInt(input("Máximo de jugadores:").trim());
            String empresa   = input("Empresa matriz:"); if (empresa == null) return;
            String restriccion = input("Restricción de edad:"); if (restriccion == null) return;
            boolean dificil  = confirm("¿Es difícil?");
            String[] invs    = {"Inventario de venta", "Inventario de préstamo"};
            String selInv    = (String) JOptionPane.showInputDialog(this, "¿A qué inventario agregar?",
                    "Inventario", JOptionPane.PLAIN_MESSAGE, null, invs, invs[0]);
            if (selInv == null) return;
            String tipo = selInv.contains("venta") ? "VENTA" : "PRESTAMO";
            msg(controlador.crearJuego(categoria.trim(), nombre.trim(), cantidad, precio,
                    anio, empresa.trim(), minJug, maxJug, restriccion.trim(), dificil, tipo));
        } catch (Exception ex) {
            msg("Error: ingrese valores válidos");
        }
    }

    private void gestionarInventario() {
        String[] ops = {
            "Ver inventario de préstamos",
            "Ver inventario de ventas",
            "Agregar stock a préstamo",
            "Agregar stock a venta"
        };
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione operación:",
                "Inventario", JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
        if (sel == null) return;

        if (sel.startsWith("Ver inventario")) {
            boolean esPrestamo = sel.contains("préstamo");
            mostrarStockTabla(esPrestamo ? "PRESTAMO" : "VENTA");
        } else {
            boolean esPrestamo = sel.contains("préstamo");
            String nombre = input("Nombre del juego:"); if (nombre == null) return;
            String sCant  = input("Cantidad a agregar:"); if (sCant == null) return;
            try {
                int cant = Integer.parseInt(sCant.trim());
                String resultado = esPrestamo
                        ? controlador.agregarStockPrestamo(nombre.trim(), cant)
                        : controlador.agregarStockVenta(nombre.trim(), cant);
                msg(resultado);
            } catch (NumberFormatException ex) {
                msg("Cantidad inválida");
            }
        }
    }

    private void verHistorialVentas() {
        HashMap<Integer, CompraVenta> ventas = controlador.getRegistroVentas();
        if (ventas.isEmpty()) { msg("No hay ventas registradas"); return; }
        String[] cols = {"ID", "Total", "Subtotal", "Fecha"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (HashMap.Entry<Integer, CompraVenta> e : ventas.entrySet()) {
            CompraVenta cv = e.getValue();
            model.addRow(new Object[]{
                e.getKey(),
                cv.getTotal(),
                cv.getSubtotal(),
                cv.getFecha() != null ? cv.getFecha().toString() : "N/A"
            });
        }
        mostrarTablaDialog("Historial de ventas", model);
    }

    private void verHistorialPrestamos() {
        HashMap<String, Prestamo> prestamos = controlador.getRegistroPrestamos();
        if (prestamos.isEmpty()) { msg("No hay préstamos registrados"); return; }
        String[] cols = {"ID", "Usuario", "Juego", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Prestamo p : prestamos.values()) {
            model.addRow(new Object[]{
                p.getId(),
                p.getUsuario().getLogin(),
                p.getJuego().getNombre(),
                p.isDevuelto() ? "Devuelto" : "Activo"
            });
        }
        mostrarTablaDialog("Historial de préstamos", model);
    }

    private void verEmpleados() {
        HashMap<String, Empleado> emp = controlador.getEmpleados();
        if (emp.isEmpty()) { msg("No hay empleados registrados"); return; }
        String[] cols = {"Login", "Tipo"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Empleado e : emp.values()) {
            model.addRow(new Object[]{e.getLogin(), e instanceof Mesero ? "Mesero" : "Cocinero"});
        }
        mostrarTablaDialog("Empleados", model);
    }

    private void verClientes() {
        HashMap<String, Cliente> clis = controlador.getClientes();
        if (clis.isEmpty()) { msg("No hay clientes registrados"); return; }
        String[] cols = {"Login", "Puntos"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Cliente c : clis.values()) {
            model.addRow(new Object[]{c.getLogin(), c.getPuntosFidelidad()});
        }
        mostrarTablaDialog("Clientes", model);
    }

    private void crearTorneo() {
        String[] tipos = {"Amistoso", "Competitivo"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Tipo de torneo:",
                "Crear torneo", JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == null) return;
        try {
            String nombre     = input("Nombre del torneo:"); if (nombre == null) return;
            String nombreJuego = input("Nombre del juego:"); if (nombreJuego == null) return;
            int cupos         = Integer.parseInt(input("Número de participantes:").trim());
            String dia        = input("Día del torneo:"); if (dia == null) return;

            if (tipo.equals("Amistoso")) {
                msg(controlador.crearTorneoAmistoso(nombre.trim(), nombreJuego.trim(), cupos, dia.trim()));
            } else {
                int costo  = Integer.parseInt(input("Costo de entrada:").trim());
                int premio = Integer.parseInt(input("Premio:").trim());
                msg(controlador.crearTorneoCompetitivo(nombre.trim(), nombreJuego.trim(), cupos, dia.trim(), costo, premio));
            }
        } catch (Exception ex) {
            msg("Error: ingrese valores válidos");
        }
    }

    private void otorgarPremio() {
        HashMap<String, Torneo> torneos = controlador.getTorneos();
        if (torneos.isEmpty()) { msg("No hay torneos registrados"); return; }
        String[] nombres = torneos.keySet().toArray(new String[0]);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione torneo:",
                "Otorgar premio", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (sel == null) return;
        Torneo torneo = torneos.get(sel);
        HashMap<Usuario, Integer> inscritos = torneo.getInscripciones();
        if (inscritos.isEmpty()) { msg("No hay participantes en este torneo"); return; }
        String[] loginInscritos = inscritos.keySet().stream()
                .map(Usuario::getLogin).toArray(String[]::new);
        String ganadorLogin = (String) JOptionPane.showInputDialog(this, "Seleccione ganador:",
                "Premio", JOptionPane.PLAIN_MESSAGE, null, loginInscritos, loginInscritos[0]);
        if (ganadorLogin == null) return;
        Usuario ganador = inscritos.keySet().stream()
                .filter(u -> u.getLogin().equals(ganadorLogin)).findFirst().orElse(null);
        if (ganador == null) return;
        msg(controlador.otorgarPremio(torneo, ganador));
    }

    private void seleccionarJuegoParaGrafica() {
        HashMap<Juego, Integer> stockV = controlador.getStockVenta();
        HashMap<Juego, Integer> stockP = controlador.getStockPrestamo();
        ArrayList<String> nombres = new ArrayList<>();
        stockV.keySet().forEach(j -> nombres.add(j.getNombre()));
        stockP.keySet().forEach(j -> { if (!nombres.contains(j.getNombre())) nombres.add(j.getNombre()); });
        if (nombres.isEmpty()) { msg("No hay juegos en el inventario"); return; }
        String[] arr = nombres.toArray(new String[0]);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione juego para gráfica de pastel:",
                "Gráfica", JOptionPane.PLAIN_MESSAGE, null, arr, arr[0]);
        if (sel == null) return;
        lienzo.setJuegoSeleccionado(sel);
        msg("Gráfica actualizada con: " + sel);
    }

    // =========================================================
    // HELPERS DE DIÁLOGOS
    // =========================================================
    private void mostrarScrollDialog(String titulo, String contenido) {
        JTextArea area = new JTextArea(contenido);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 350));
        JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    private void mostrarTablaDialog(String titulo, DefaultTableModel model) {
        JTable tabla = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(550, 300));
        JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.PLAIN_MESSAGE);
    }
}
