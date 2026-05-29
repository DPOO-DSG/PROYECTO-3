package tests.integracion;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import logica.*;

public class HistoriaClienteTest {

    private Cafe cafe;
    private Juego juegoCatalogo;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe(50, 5);
        cafe.inicializarMesas(5, 4);
        
        juegoCatalogo = new Juego("ACCION", "Jenga", 2, 0, 1983, "Hasbro", 1, 8, "+0", false);
        cafe.agregarJuego(juegoCatalogo, "PRESTAMO");
    }

    @Test
    public void testFlujoCompletoCliente() throws Exception {
        // 1. Historia: Registrar usuario en el sistema e Ingresar
        boolean registrado = cafe.crearCliente("cliente_nuevo", "secreto123", new ArrayList<>(), 0.0);
        assertTrue(registrado, "El cliente debe poder registrarse exitosamente");
        
        Object usuarioLogueado = cafe.login("cliente_nuevo", "secreto123");
        assertNotNull(usuarioLogueado, "El cliente debe poder iniciar sesión");
        Cliente cliente = (Cliente) usuarioLogueado;

        // 2. Historia: Realizar una reserva en el café
        LocalDateTime fechaDeseada = LocalDateTime.now().plusDays(2).withHour(18).withMinute(0);
        Reserva miReserva = cafe.agendarReserva(cliente, 3, false, true, fechaDeseada);
        assertNotNull(miReserva, "El sistema debe confirmar y agendar la reserva");
        assertEquals(1, cafe.getReservasCliente(cliente).size(), "El cliente debe tener 1 reserva activa");

        // 3. Historia: Consultar catálogo y Solicitar préstamo
        ArrayList<Juego> catalogo = cliente.consultarCatalogoPrestamo(cafe);
        assertTrue(catalogo.contains(juegoCatalogo), "El cliente debe poder ver el Jenga en el catálogo");
        
        assertDoesNotThrow(() -> {
            cliente.solicitarPrestamo(cafe, juegoCatalogo, miReserva);
        });
        
        ArrayList<Prestamo> misPrestamos = cafe.getPrestamosClienteEnReserva(cliente, miReserva);
        assertEquals(1, misPrestamos.size(), "El cliente debe tener 1 juego prestado en su mesa");
        Prestamo prestamoActivo = misPrestamos.get(0);

        // 4. Historia: Devolver juego prestado
        cafe.devolverPrestamo(prestamoActivo.getId());
        assertTrue(prestamoActivo.isDevuelto(), "El sistema debe registrar que el juego fue devuelto");
        assertEquals(2, cafe.getInventarioPrestamo().getStock().get(juegoCatalogo), "El stock del juego debe volver a la normalidad");
    }
}