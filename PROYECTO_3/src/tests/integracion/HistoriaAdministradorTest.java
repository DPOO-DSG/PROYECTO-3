package tests.integracion;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import logica.*;

public class HistoriaAdministradorTest {

    private Cafe cafe;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe(100, 10);
        cafe.inicializarTurnos();
        // El sistema arranca con un admin base
        cafe.crearAdministrador("adminPrincipal", "superClave123");
    }

    @Test
    public void testFlujoCompletoAdministrador() {
        // 1. Historia: Ingresar al Sistema
        Object usuarioLogueado = cafe.login("adminPrincipal", "superClave123");
        assertNotNull(usuarioLogueado, "El administrador debe poder iniciar sesión");
        assertTrue(usuarioLogueado instanceof Administrador, "El rol debe ser Administrador");
        
        Administrador admin = (Administrador) usuarioLogueado;

        // 2. Historia: Registrar Empleado (Mesero y Cocinero)
        ArrayList<String> dias = new ArrayList<>();
        dias.add("jueves");
        dias.add("viernes");
        
        assertDoesNotThrow(() -> {
            cafe.crearMesero("nuevoMesero", "pass1", "M01", new ArrayList<>(), dias, new ArrayList<>());
            cafe.crearCocinero("nuevoCocinero", "pass2", "C01", new ArrayList<>(), dias);
        });
        assertEquals(2, cafe.getEmpleados().size(), "Deben existir 2 empleados registrados en el sistema");

        // 3. Historia: Añadir juegos al café
        boolean juegoCreado = admin.crearJuego(cafe, "TABLERO", "Aventureros al Tren", 3, 0, 2004, "Days of Wonder", 2, 5, "+8", false, "PRESTAMO");
        assertTrue(juegoCreado, "El administrador debe poder agregar un juego");
        assertEquals(1, cafe.getInventarioPrestamo().getStock().size(), "El juego debe aparecer en el catálogo de préstamos");

        // 4. Historia: Aceptar solicitudes de platillos
        Bebida sugerencia = new Bebida("Limonada de Coco", 7000, "fria", false);
        cafe.crearSolicitudSugerencia(sugerencia);
        
        int idSolicitud = cafe.getSolicitudesPendientesPlatillos().keySet().iterator().next();
        
        boolean aprobado = cafe.aprobarSolicitudPlatillo(idSolicitud);
        assertTrue(aprobado, "El admin debe poder aprobar el platillo");
        assertTrue(cafe.getMenu().contains(sugerencia), "La limonada ahora debe ser parte del menú oficial");
    }
}