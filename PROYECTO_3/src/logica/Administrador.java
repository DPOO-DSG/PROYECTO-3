package logica;

import java.io.Serializable;
import java.util.HashMap;

import excepciones.CopiasInsuficientesException;
import excepciones.JuegoNoEncontradoException;
import excepciones.TorneoYaExisteException;


public class Administrador implements Serializable {
	
	
	public HashMap<Integer, CambioDeTurno> verSolicitudes(Cafe cafe) {
	    return cafe.getSolicitudesPendientes();
	} 
	
	public boolean aprobarSolicitud(Cafe cafe, int idSolicitud) {
	    return cafe.aprobarSolicitud(idSolicitud);
	}
	
	public boolean rechazarSolicitud(Cafe cafe, int idSolicitud) {
	    return cafe.rechazarSolicitud(idSolicitud);
	}
	public boolean crearPlatillo(Cafe cafe, Platillo platillo) {
		return cafe.anadirAMenu(platillo);
	}
	public boolean crearJuego(Cafe cafe, String categoria, String nombre, int cantidad, int precio, int añoPublicacion, String empresaMatriz, int minJugadores, int maxJugadores, String restriccionEdad, boolean dificil, String tipoInventario) {	
		return cafe.crearJuego(categoria, nombre, cantidad, precio, añoPublicacion, empresaMatriz, minJugadores, maxJugadores, restriccionEdad, dificil, tipoInventario);
		
	}
	public void crearTorneoAmistoso(Cafe cafe,String nombre,String nombreJuego, int participantes, String dia) throws TorneoYaExisteException, JuegoNoEncontradoException, CopiasInsuficientesException {	
		cafe.crearTorneoAmistoso(nombre,nombreJuego,participantes,dia);
	}	
	public void crearTorneoCompetitivo (Cafe cafe,String nombre,String nombreJuego, int participantes, String dia, int costoEntrada, int premio) throws TorneoYaExisteException, JuegoNoEncontradoException, CopiasInsuficientesException {
		cafe.crearTorneoCompetitivo(nombre,nombreJuego,participantes, dia, costoEntrada, premio);
    }
	
}
