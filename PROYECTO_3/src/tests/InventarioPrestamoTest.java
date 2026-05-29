package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import logica.*;
import excepciones.*;

public class InventarioPrestamoTest {

    private InventarioPrestamo inventario;
    private Juego juego1;

    @BeforeEach
    public void setUp() {
        inventario = new InventarioPrestamo();
        juego1 = new Juego("TABLERO", "Catan", 2, 0, 1995, "Kosmos", 3, 4, "+10", true);
        
        inventario.getStock().put(juego1, juego1.getCantidad());
    }

    @Test
    public void testRegistrarPrestamoExitoso() throws JuegoNoEncontradoException {
        inventario.registrarPrestamo(juego1);
        
        assertEquals(1, inventario.getStock().get(juego1), "El stock debe disminuir en 1 al prestar");
        assertTrue(inventario.estaDisponible(juego1), "El juego debe seguir disponible porque queda 1 copia");
    }

    @Test
    public void testRegistrarPrestamoSinStockLanzaExcepcion() throws JuegoNoEncontradoException {
        inventario.registrarPrestamo(juego1);
        inventario.registrarPrestamo(juego1);
        
        Exception excepcion = assertThrows(JuegoNoEncontradoException.class, () -> {
            inventario.registrarPrestamo(juego1);
        });
        
        assertEquals("No hay unidades disponibles", excepcion.getMessage());
        assertFalse(inventario.estaDisponible(juego1), "El juego ya no debe estar disponible");
    }

    @Test
    public void testRegistrarDevolucionExitosa() throws JuegoNoEncontradoException {
        inventario.getStock().put(juego1, 1);
        
        inventario.registrarDevolucion(juego1);
        
        assertEquals(2, inventario.getStock().get(juego1), "El stock debe aumentar en 1 al devolver");
    }

    @Test
    public void testBuscarJuegoIgnoraMayusculas() throws JuegoNoEncontradoException {
        Juego encontrado = inventario.buscarJuego("CATAN");
        
        assertNotNull(encontrado, "Debe encontrar el juego sin importar mayúsculas/minúsculas");
        assertEquals("Catan", encontrado.getNombre());
    }

    @Test
    public void testValidarCopiasInsuficientesLanzaExcepcion() {
        assertThrows(CopiasInsuficientesException.class, () -> {
            inventario.validarCopias(juego1, 4);
        });
    }
}