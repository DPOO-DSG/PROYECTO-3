package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import logica.*;
import excepciones.*;

public class CafeTest {

    private Cafe cafe;
    private Cliente cliente;
    private Reserva reserva;
    private Juego juego1;
    private Juego juego2;
    private Juego juego3;

    @BeforeEach
    public void setUp() throws Exception {
        cafe = new Cafe(100, 10);
        cafe.inicializarTurnos();
        cafe.inicializarMesas(5, 4);

        juego1 = new Juego("TABLERO", "Juego A", 5, 0, 2020, "Empresa", 2, 4, "+0", false);
        juego2 = new Juego("TABLERO", "Juego B", 5, 0, 2020, "Empresa", 2, 4, "+0", false);
        juego3 = new Juego("TABLERO", "Juego C", 5, 0, 2020, "Empresa", 2, 4, "+0", false);
        cafe.agregarJuego(juego1, "PRESTAMO");
        cafe.agregarJuego(juego2, "PRESTAMO");
        cafe.agregarJuego(juego3, "PRESTAMO");

        cafe.crearCliente("cliente_test", "123", new ArrayList<>(), 0);
        cliente = cafe.getClientes().get("cliente_test");
        reserva = cafe.agendarReserva(cliente, 2, false, false, LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testLoginsUnicosGlobales() {
        ArrayList<String> dias = new ArrayList<>();
        dias.add("lunes");

        Exception excepcion = assertThrows(UsuarioYaExisteException.class, () -> {
            cafe.crearMesero("cliente_test", "pass", "DESC", new ArrayList<>(), dias, new ArrayList<>());
        });
        
        assertTrue(excepcion.getMessage().contains("cliente_test"), "Debe rechazar la creación e indicar el login duplicado");
    }

    @Test
    public void testLimitePrestamosActivosCliente() throws Exception {
        cafe.solicitarPrestamo(cliente, juego1, reserva);
        cafe.solicitarPrestamo(cliente, juego2, reserva);

        Exception excepcion = assertThrows(LimitePrestamosException.class, () -> {
            cafe.solicitarPrestamo(cliente, juego3, reserva);
        });
        
        assertEquals("Máximo 2 préstamos activos", excepcion.getMessage());
    }

    @Test
    public void testAsignarTurnoDuplicadoLanzaExcepcion() throws Exception {
        ArrayList<String> dias = new ArrayList<>();
        dias.add("lunes");
        cafe.crearCocinero("cocinero1", "pass", "DESC", new ArrayList<>(), dias);
        
        Exception excepcion = assertThrows(TurnoYaAsignadoException.class, () -> {
            cafe.asignarTurnoEmpleado("cocinero1", "lunes");
        });
        
        assertNotNull(excepcion, "No se debe permitir asignar el mismo turno dos veces");
    }

    @Test
    public void testSolicitudCambioTurnoSinPersonalSuficiente() throws Exception {

        ArrayList<String> dias = new ArrayList<>();
        dias.add("lunes");
        cafe.crearCocinero("cocineroUnico", "pass", "COD", new ArrayList<>(), dias);
        Cocinero c = (Cocinero) cafe.getEmpleados().get("cocineroUnico");
        
        Turno turnoLunes = cafe.getTurnos().get("lunes");
        Turno turnoMartes = cafe.getTurnos().get("martes");

        Exception excepcion = assertThrows(NoPuedeSalirTurnoException.class, () -> {
            cafe.crearSolicitudCambio(c, turnoLunes, turnoMartes);
        });
        
        assertEquals("El empleado no puede salir del turno actual", excepcion.getMessage());
    }
    
    @Test
    public void testAnadirAMenuDuplicado() {
        Bebida b1 = new Bebida("Te Verde", 3000, "caliente", false);
        Bebida b2 = new Bebida("Te Verde", 4000, "fria", false); 
        
        assertTrue(cafe.anadirAMenu(b1), "Debe permitir añadir el primer platillo");
        assertFalse(cafe.anadirAMenu(b2), "Debe retornar false al intentar añadir un platillo con el mismo nombre");
    }

    @Test
    public void testAgregarJuegoDuplicado() {
        Juego copiaJuego = new Juego("ACCION", "Juego A", 2, 0, 2020, "Empresa", 2, 4, "+0", false);
        
        assertFalse(cafe.agregarJuego(copiaJuego, "PRESTAMO"), "El sistema debe retornar false si el juego ya existe en el inventario");
    }

    @Test
    public void testLoginUsuarios() {
        assertNotNull(cafe.login("cliente_test", "123"), "El login del cliente debe ser exitoso con la contraseña correcta");
        
        cafe.crearAdministrador("admin_test", "adminPass");
        assertNotNull(cafe.login("admin_test", "adminPass"), "El login del administrador debe ser exitoso");
        assertTrue(cafe.login("admin_test", "adminPass") instanceof Administrador, "El objeto retornado debe ser un Administrador");
        
        assertNull(cafe.login("cliente_test", "claveEquivocada"), "Debe retornar null si la contraseña es incorrecta");
        assertNull(cafe.login("usuarioInexistente", "123"), "Debe retornar null si el usuario no existe");
    }

    @Test
    public void testFlujoSolicitudSugerenciaPlatillo() {
        Bebida sugerencia = new Bebida("Jugo de Mora", 5000, "fria", false);
        cafe.crearSolicitudSugerencia(sugerencia);
        
        assertFalse(cafe.getSolicitudesPendientesPlatillos().isEmpty(), "Debe haber una solicitud pendiente");
        
        int idSolicitud = cafe.getSolicitudesPendientesPlatillos().keySet().iterator().next();
        
        assertTrue(cafe.aprobarSolicitudPlatillo(idSolicitud), "El método debe retornar true al aprobar con un ID válido");
        
        assertTrue(cafe.getMenu().contains(sugerencia), "El platillo debe estar ahora en el menú del café");
        assertTrue(cafe.getSolicitudesPendientesPlatillos().isEmpty(), "La lista de solicitudes debe quedar vacía tras ser aprobada");
    }

    @Test
    public void testValidarReservaConCapacidadExcedida() {
        Exception excepcion = assertThrows(NoHayMesasDisponiblesException.class, () -> {
            cafe.agendarReserva(cliente, 5, false, false, LocalDateTime.now().plusDays(2));
        });
        
        assertNotNull(excepcion, "Debe rechazar la reserva si ninguna mesa cumple con la capacidad de personas");
    }
}