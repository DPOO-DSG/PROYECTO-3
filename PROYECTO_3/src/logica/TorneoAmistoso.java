package logica;

import java.io.Serializable;
import java.util.ArrayList;

public class TorneoAmistoso extends Torneo implements Serializable{

	public TorneoAmistoso(String nombre, Juego juego, int cuposMaximos, String dia) {
		super(nombre, juego, cuposMaximos, dia);
	}

	@Override
	public String otorgarPremio(Usuario ganador) {

	    if (ganador instanceof Cliente) {
	        Cliente c = (Cliente) ganador;
	        c.agregarBonoDescuento();
	        return ("Entregado el premio correctamente");
	    }
		return null;

	}
	
	

	
	
	
	
	
}
