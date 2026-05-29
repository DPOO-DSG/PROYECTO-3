package logica;

import java.io.Serializable;
import java.util.ArrayList;


public class Cliente extends Usuario implements Serializable {
	private double puntosFidelidad;
	private boolean bonoTorneo; 

	public boolean isBonoTorneo() {
		return bonoTorneo;
	}


	public void setBonoTorneo(boolean bonoTorneo) {
		this.bonoTorneo = bonoTorneo;
	}


	public Cliente(String login, String password, ArrayList<Juego> juegosFavoritos, double puntosFidelidad) {
		super(login, password, juegosFavoritos);
		this.puntosFidelidad = puntosFidelidad;
	}
	
	
	public double getPuntosFidelidad() {
		return puntosFidelidad;
	}


	public void setPuntosFidelidad(double puntosFidelidad) {
		this.puntosFidelidad = puntosFidelidad;
	}


	@Override
	public void solicitarPrestamo(Cafe cafe, Juego juego, Reserva reserva) throws Exception{
	    cafe.solicitarPrestamo(this, juego, reserva);
	}
	public double consultarPuntosFidelidad(Cafe cafe) {
		return cafe.consultarPuntosFidelidad(this);
	
	
	}

	public void agregarBonoDescuento() {
	    if (!bonoTorneo) {
	        bonoTorneo = true;
	    }
	}
	public boolean usarBonoDescuento() {
	    if (bonoTorneo) {
	        bonoTorneo = false;
	        return true;
	    }
	    return false;
	}


	@Override
	public void inscribirTorneo(Cafe cafe, String nombreTorneo, int cupos) throws Exception {
		cafe.inscribirATorneo(nombreTorneo, this, cupos);

		
	}
 

	@Override
	public void eliminarTorneo(Cafe cafe, String nombreTorneo) throws Exception {
		cafe.eliminarDeTorneo(nombreTorneo, this);
	}
	
}
