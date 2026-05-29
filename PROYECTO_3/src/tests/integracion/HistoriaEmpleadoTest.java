package tests.integracion;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import logica.*;

public class HistoriaEmpleadoTest {

    private Cafe cafe;
    private Cliente cliente;
    private Reserva reserva;
    private Pasteleria postre;
    private Juego juegoVenta;
    private Juego juegoDificil;
    private Turno turnoMartes;
    private Turno turnoMiercoles;

    @BeforeEach
    public void setUp() throws Exception {
        cafe = new Cafe(100, 10);
        cafe.inicializarTurnos();
        cafe.inicializarMesas(5, 4);

        postre = new Pasteleria("Torta de Chocolate", 6000, new ArrayList<>());
        cafe.anadirAMenu(postre);

        juegoVenta = new Juego("CARTAS", "Uno", 10, 15000, 2000, "Mattel", 2, 10, "+0", false);
        juegoDificil = new Juego("ESTRATEGIA", "Risk", 2, 0, 1959, "Hasbro", 2, 6, "+10", true);
        cafe.agregarJuego(juegoVenta, "VENTA");
        cafe.agregarJuego(juegoDificil, "PRESTAMO");

        cafe.crearCliente("cliente_mesa", "pass123", new ArrayList<>(), 0);
        cliente = cafe.getClientes().get("cliente_mesa");
        reserva = cafe.agendarReserva(cliente, 2, false, false, LocalDateTime.now().plusDays(1));

        ArrayList<String> diasTrabajo = new ArrayList<>();
        diasTrabajo.add("martes"); 
        
        cafe.crearMesero("mesero_protagonista", "secreto", "M01", new ArrayList<>(), diasTrabajo, new ArrayList<>());
        
        cafe.crearMesero("relleno1", "pass", "R01", new ArrayList<>(), diasTrabajo, new ArrayList<>());
        cafe.crearMesero("relleno2", "pass", "R02", new ArrayList<>(), diasTrabajo, new ArrayList<>());

        turnoMartes = cafe.getTurnos().get("martes");
        turnoMiercoles = cafe.getTurnos().get("miercoles");
    }

    @Test
    public void testFlujoAtencionAlCliente() {
        // 1. Historia: Ingresar al sistema
        Object usuario = cafe.login("mesero_protagonista", "secreto");
        assertNotNull(usuario, "El empleado debe poder iniciar sesión");
        assertTrue(usuario instanceof Mesero, "El objeto debe ser de tipo Mesero");
        
        Mesero mesero = (Mesero) usuario;

        // 2. Historia: Consultar el menú y Tomar pedido
        ArrayList<Platillo> menuActual = cafe.consultarMenu();
        assertTrue(menuActual.contains(postre), "El empleado debe poder ver el postre en el menú");

        ArrayList<Platillo> platillosPedidos = new ArrayList<>();
        platillosPedidos.add(postre);
                assertDoesNotThrow(() -> {
            mesero.hacerPedido(cafe, reserva, platillosPedidos, new ArrayList<>());
        });
        assertEquals(1, reserva.getPedidos().size(), "El pedido debió registrarse en la reserva del cliente");

        // 3. Historia: Crear factura
        assertDoesNotThrow(() -> {
            cafe.crearFactura(cliente, 1000, false, false, null, reserva);
        });
        
        CompraVenta factura = cafe.getFacturaPorReserva(reserva);
        assertNotNull(factura, "El sistema debió generar la factura para el cliente");
        
        assertEquals(8140.0, factura.getTotal(), "El total de la factura generada por el empleado debe ser exacto");
    }

    @Test
    public void testFlujoAccionesPersonalesEmpleado() {
        Mesero mesero = (Mesero) cafe.getEmpleados().get("mesero_protagonista");

        // 1. Historia: Aprender juego difícil
        mesero.anadirJuegosConocidos(juegoDificil);
        assertTrue(mesero.getJuegosConocidos().contains(juegoDificil), "El mesero debe registrar que aprendió el juego difícil");

        // 2. Historia: Comprar un juego del catálogo
        assertDoesNotThrow(() -> {
            cafe.comprarJuegoEmpleado(mesero, juegoVenta, 0.0);
        });
        
        assertEquals(9, cafe.getInventarioVentas().getStock().get(juegoVenta), "El stock debe disminuir tras la compra del empleado");

        // 3. Historia: Solicitar cambio de turno
        assertDoesNotThrow(() -> {
            mesero.solicitarCambioTurno(cafe, turnoMartes, turnoMiercoles);
        });
        
        // Verificamos que la solicitud se envió al sistema para que el admin la revise
        assertFalse(cafe.getSolicitudesCambioTurno().isEmpty(), "Debe existir una solicitud de cambio de turno pendiente");
    }
}