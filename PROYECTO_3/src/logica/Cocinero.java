package logica;

import java.io.Serializable;
import java.util.ArrayList;

public class Cocinero extends Empleado implements Serializable {
	//Constructor
	public Cocinero(String login, String password, ArrayList<Juego> juegosFavoritos, String codigoDescuento,
			ArrayList<Turno> turno) {
		super(login, password, juegosFavoritos, codigoDescuento, turno);
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
