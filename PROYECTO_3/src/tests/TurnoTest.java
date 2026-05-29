package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import logica.*;

public class TurnoTest {

    private Turno turno;
    private Mesero mesero;
    private Cocinero cocinero;

    @BeforeEach
    public void setUp() {
        turno = new Turno("lunes", new ArrayList<>(), new ArrayList<>());
        
        mesero = new Mesero("mesero1", "pass", new ArrayList<>(), "DESC1", new ArrayList<>(), new ArrayList<>());
        cocinero = new Cocinero("cocinero1", "pass", new ArrayList<>(), "DESC2", new ArrayList<>());
    }

    @Test
    public void testAgregarEmpleadoClasificaCorrectamente() {
        turno.agregarEmpleado(mesero);
        turno.agregarEmpleado(cocinero);
        
        assertEquals(1, turno.getMeseros().size(), "Debe haber 1 mesero en la lista");
        assertTrue(turno.getMeseros().contains(mesero), "El mesero correcto debe estar en la lista");
        
        assertEquals(1, turno.getCocineros().size(), "Debe haber 1 cocinero en la lista");
        assertTrue(turno.getCocineros().contains(cocinero), "El cocinero correcto debe estar en la lista");
    }

    @Test
    public void testRemoverEmpleado() {
        turno.agregarEmpleado(mesero);
        
        turno.removerEmpleado(mesero);
        
        assertEquals(0, turno.getMeseros().size(), "La lista de meseros debe quedar vacía al removerlo");
    }
}