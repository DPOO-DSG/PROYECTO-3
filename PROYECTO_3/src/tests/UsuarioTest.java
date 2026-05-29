package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import logica.*;

public class UsuarioTest {

    private Cliente cliente;
    private Cocinero empleado;
    private Juego juegoFavorito;
    private Juego juegoNormal;
    private Cafe cafeMock;

    @BeforeEach
    public void setUp() {
        cafeMock = new Cafe(50, 10); 
        
        juegoFavorito = new Juego("TABLERO", "Monopoly", 1, 0, 2000, "Hasbro", 2, 4, "+0", false);
        juegoNormal = new Juego("CARTAS", "Poker", 1, 0, 2000, "Bicycle", 2, 4, "+18", false);
        
        ArrayList<Juego> favs = new ArrayList<>();
        favs.add(juegoFavorito);
        
        cliente = new Cliente("cliente1", "123", favs, 0);
        
        empleado = new Cocinero("emp1", "123", new ArrayList<>(), "COD", new ArrayList<>());
    }

    @Test
    public void testEsFanaticoDe() {
        assertTrue(cliente.esFanaticoDe(juegoFavorito), "Debe retornar true si el juego está en favoritos");
        assertFalse(cliente.esFanaticoDe(juegoNormal), "Debe retornar false si el juego no está en favoritos");
    }

    @Test
    public void testEstaEnTurnoAhora() {
        String diaDeHoy = cafeMock.convertirDia(LocalDateTime.now().getDayOfWeek());
        
        Turno turnoHoy = new Turno(diaDeHoy, new ArrayList<>(), new ArrayList<>());
        
        empleado.getTurnos().add(turnoHoy);
        
        assertTrue(empleado.estaEnTurnoAhora(cafeMock), "El empleado debe estar en turno porque tiene asignado el día actual");
    }
}