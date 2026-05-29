package logica;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import  excepciones.*;
import java.util.ArrayList;

public abstract class Empleado extends Usuario implements Serializable {
	private String codigoDescuento;
	private ArrayList<Turno> turnos;
	
	//Constructor
	public Empleado(String login, String password, ArrayList<Juego> juegosFavoritos, String codigoDescuento,
			ArrayList<Turno> turnos) {
		super(login, password, juegosFavoritos);
		this.codigoDescuento = codigoDescuento;
		this.turnos = turnos;
	} 
	//getters y setters
	public String getCodigoDescuento() {
		return codigoDescuento;
	}

	public void setCodigoDescuento(String codigoDescuento) {
		this.codigoDescuento = codigoDescuento;
	}

	public ArrayList<Turno> getTurnos() {
	    return turnos;
	}

	public void setTurnos(ArrayList<Turno> turnos) {
	    this.turnos = turnos;
	}
	
	//Metodo en el que el empleado crea la solicitud de cambio
	public void solicitarCambioTurno(Cafe cafe, Turno actual, Turno nuevo)
	        throws SolicitudInvalidaException, NoPerteneceTurnoException, PersonalInsuficienteException, NoPuedeSalirTurnoException {

	    cafe.crearSolicitudCambio(this, actual, nuevo);
	}

	public boolean estaEnTurnoAhora(Cafe cafe) {

	    DayOfWeek hoy = LocalDateTime.now().getDayOfWeek();
	    String diaHoy = cafe.convertirDia(hoy);

	    for (Turno t : turnos) {
	        if (t.getJornada().equalsIgnoreCase(diaHoy)) {
	            return true;
	        }
	    }

	    return false;
	}
	
	public ArrayList<Turno> consultarTurnos(Cafe cafe) {
	    return cafe.consultarTurnosEmpleado(this);
	}
	@Override
	
	public void solicitarPrestamo(Cafe cafe, Juego juego, Reserva reserva)throws Exception {
	     cafe.solicitarPrestamo(this, juego, null);
	}
	public void hacerPedido(Cafe cafe,
	        Reserva reserva,
	        ArrayList<Platillo> platillos,
	        ArrayList<Juego> juegos) throws ReservaNoEncontradaException, EmpleadoNoExisteException, AlcoholReservaException {

	    cafe.crearPedido(reserva, this, platillos, juegos);
	}
	public boolean tieneTurno(String dia) {
		// TODO Auto-generated method stub
		for (Turno t : turnos) {
	        if (t.getJornada().equalsIgnoreCase(dia)) {
	            return true;
	        }
	    }
		return false;
		
	}
}
