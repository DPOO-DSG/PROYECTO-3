package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import logica.*;

public class TorneoTest {

    private TorneoCompetitivo torneo;
    private Juego juegoTorneo;
    private Cliente fanatico;
    private Cliente usuarioNormal;
    private Cafe cafeMock; 

    @BeforeEach
    public void setUp() {
        cafeMock = new Cafe(50, 10);
        juegoTorneo = new Juego("ACCION", "Super Smash", 5, 0, 2018, "Nintendo", 2, 8, "+0", false);
        
        torneo = new TorneoCompetitivo("Torneo Smash", juegoTorneo, 10, "sabado", 5000, 50000);
        
        ArrayList<Juego> favs = new ArrayList<>();
        favs.add(juegoTorneo);
        fanatico = new Cliente("fan1", "pass", favs, 0);
        
        usuarioNormal = new Cliente("normal1", "pass", new ArrayList<>(), 0);
    }

    @Test
    public void testInscribirUsuarioRespetaLimiteTresCupos() {
        Exception excepcion = assertThrows(Exception.class, () -> {
            torneo.inscribirUsuario(cafeMock, usuarioNormal, 4);
        });
        assertEquals("Máximo 3 cupos por usuario.", excepcion.getMessage());
    }

    @Test
    public void testReservaCuposParaFanaticos() throws Exception {
        for (int i = 0; i < 4; i++) {
            Cliente c = new Cliente("relleno" + i, "pass", new ArrayList<>(), 0);
            torneo.inscribirUsuario(cafeMock, c, 2); 
        }
        
        Exception excepcionNormal = assertThrows(Exception.class, () -> {
            torneo.inscribirUsuario(cafeMock, usuarioNormal, 1);
        });
        assertTrue(excepcionNormal.getMessage().contains("exclusivos para fanáticos"));
        
        assertDoesNotThrow(() -> {
            torneo.inscribirUsuario(cafeMock, fanatico, 1);
        });
        
        assertEquals(1, torneo.getCuposReservadosOcupados(), "Debe haber 1 cupo de fanático ocupado");
    }

    @Test
    public void testEliminarInscripcionLiberaCupoFanatico() throws Exception {
        torneo.inscribirUsuario(cafeMock, fanatico, 2);
        assertEquals(2, torneo.getCuposReservadosOcupados());
        
        torneo.eliminarInscripcion(fanatico);
        
        assertFalse(torneo.getInscripciones().containsKey(fanatico), "El usuario debió ser eliminado del mapa");
        assertEquals(0, torneo.getCuposReservadosOcupados(), "Los cupos reservados deben liberarse (volver a 0)");
    }
}