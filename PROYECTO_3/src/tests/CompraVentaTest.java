package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import logica.*;

public class CompraVentaTest {

    private Cliente cliente;
    private Reserva reserva;
    private Pedido pedido;
    private Bebida cafe;
    private Juego juegoVenta;

    @BeforeEach
    public void setUp() {
        cliente = new Cliente("juan_perez", "pass123", new ArrayList<>(), 0.0);
        reserva = new Reserva("R1", null, cliente, 2, false, false);
        
        cafe = new Bebida("Cafe Latte", 5000, "caliente", false);
        juegoVenta = new Juego("TABLERO", "Ajedrez", 1, 50000, 2000, "Genérico", 2, 2, "+0", false);
        
        ArrayList<Platillo> platillos = new ArrayList<>();
        platillos.add(cafe);
        
        ArrayList<Juego> juegos = new ArrayList<>();
        juegos.add(juegoVenta);
        
        pedido = new Pedido(cliente, platillos, juegos);
        reserva.getPedidos().add(pedido);
    }

    @Test
    public void testCalcularValoresConReserva() {
  
        CompraVenta factura = new CompraVenta(1, cliente, 2000, reserva);
        factura.calcularValores();
        
        assertEquals(55000.0, factura.getSubtotal(), "El subtotal debe ser la suma exacta de los platillos y juegos");
        assertEquals(10450.0, factura.getIva(), "El IVA debe ser el 19% del subtotal");
        assertEquals(67450.0, factura.getTotal(), "El total debe sumar subtotal + IVA + propina");
        assertEquals(67, factura.getPuntosGenerados(), "Los puntos deben ser el total dividido entre 1000");
    }

    @Test
    public void testCalcularValoresVentaDirectaSinReserva() {

        
        CompraVenta factura = new CompraVenta(2, cliente, 0, null);
        factura.setSubtotal(100000.0); 
        factura.calcularValores();
        
        assertEquals(100000.0, factura.getSubtotal(), "El subtotal debe mantenerse como se asignó manualmente");
        assertEquals(19000.0, factura.getIva(), "El IVA debe ser el 19% del subtotal manual");
        assertEquals(119000.0, factura.getTotal(), "El total debe ser subtotal + IVA sin propina");
    }

    @Test
    public void testCalcularValoresReservaVacia() {
        Reserva reservaVacia = new Reserva("R2", null, cliente, 1, false, false);
        CompraVenta factura = new CompraVenta(3, cliente, 500, reservaVacia); 
        factura.calcularValores();
        
        assertEquals(0.0, factura.getSubtotal(), "El subtotal debe ser 0 si no hay pedidos");
        assertEquals(0.0, factura.getIva(), "El IVA debe ser 0 si el subtotal es 0");
        assertEquals(500.0, factura.getTotal(), "El total solo debe reflejar la propina");
    }
}