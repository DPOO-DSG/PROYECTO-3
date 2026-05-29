package tests;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import logica.*;
import excepciones.*;

public class InventarioVentaTest {

    private InventarioVenta inventarioVenta;
    private Juego juegoVenta;

    @BeforeEach
    public void setUp() {
        inventarioVenta = new InventarioVenta();
        juegoVenta = new Juego("CARTAS", "Poker", 5, 15000, 2020, "Bicycle", 2, 6, "+18", false);
        
        inventarioVenta.agregarAlInventario(juegoVenta, 5);
    }

    @Test
    public void testAgregarAlInventarioJuegoExistente() {
        inventarioVenta.agregarAlInventario(juegoVenta, 3);
        
        assertEquals(8, inventarioVenta.getStock().get(juegoVenta), "El stock debe acumularse si el juego ya existe");
    }

    @Test
    public void testRegistrarVentaExitosa() throws JuegoNoEncontradoException {
        inventarioVenta.registrarVenta(juegoVenta);
        
        assertEquals(4, inventarioVenta.getStock().get(juegoVenta), "El stock de venta debe disminuir en 1");
    }

    @Test
    public void testRegistrarVentaJuegoSinStock() {
        inventarioVenta.getStock().put(juegoVenta, 0);
        Exception excepcion = assertThrows(JuegoNoEncontradoException.class, () -> {
            inventarioVenta.registrarVenta(juegoVenta);
        });
        
        assertEquals("No hay unidades disponibles para la venta", excepcion.getMessage());
    }
}