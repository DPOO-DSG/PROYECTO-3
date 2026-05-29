package logica;

import java.io.Serializable;
import java.util.ArrayList;

public class Mesero extends Empleado implements Serializable{

	private ArrayList<Juego> juegosConocidos;

	public Mesero(String login, String password, ArrayList<Juego> juegosFavoritos, String codigoDescuento, ArrayList<Turno> turno,
			ArrayList<Juego> juegosConocidos) {
		super(login, password, juegosFavoritos, codigoDescuento, turno);
		this.juegosConocidos = new ArrayList<>();
	}

	public ArrayList<Juego> getJuegosConocidos() {
		return juegosConocidos;
	}

	public void setJuegosConocidos(ArrayList<Juego> juegos) {
		this.juegosConocidos = juegos;
	}
	
	public void anadirJuegosConocidos(Juego juego) {
		
	    if (juego != null && !juegosConocidos.contains(juego)) {
	        juegosConocidos.add(juego);
	    }
	}

	@Override
	public void inscribirTorneo(Cafe cafe, String nombreTorneo, int cupos) throws Exception {
		// TODO Auto-generated method stub
		cafe.inscribirATorneo(nombreTorneo, this, cupos);
	}
	@Override
	public void eliminarTorneo(Cafe cafe, String nombreTorneo) throws Exception {
		cafe.eliminarDeTorneo(nombreTorneo, this);
	}
}

