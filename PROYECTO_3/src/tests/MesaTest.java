package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import logica.*;

public class MesaTest {

    private Mesa mesa;
    private Cliente cliente;
    private LocalDateTime fechaMañana;

    @BeforeEach
    public void setUp() {
        mesa = new Mesa(1, 4, new ArrayList<>(), new ArrayList<>(), null);
        cliente = new Cliente("maria99", "pass", new ArrayList<>(), 0);
        
        fechaMañana = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    public void testMesaDisponibleSinReservas() {
        assertTrue(mesa.estaDisponibleEnFecha(fechaMañana), "La mesa debe estar disponible si su agenda está vacía");
    }

    @Test
    public void testAgregarReservaOcupaLaMesa() {
        Reserva reserva = new Reserva("R1", fechaMañana, cliente, 2, false, false);
        mesa.agregarReserva(reserva);
        
        assertFalse(mesa.estaDisponibleEnFecha(fechaMañana), "La mesa NO debe estar disponible en la fecha que ya fue reservada");
        
        LocalDateTime fechaDespues = fechaMañana.plusHours(1);
        assertTrue(mesa.estaDisponibleEnFecha(fechaDespues), "La mesa SÍ debe estar disponible en un horario diferente");
    }
}