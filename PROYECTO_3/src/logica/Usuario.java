package logica;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Usuario implements Serializable {
	private String login;
	private String password;
	private ArrayList<Juego> juegosFavoritos;
	//Constructor
	public Usuario(String login, String password, ArrayList<Juego> juegosFavoritos) {
		super();
		this.login = login;
		this.password = password;
		this.juegosFavoritos = juegosFavoritos;
	}
	//Getter y setter
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ArrayList<Juego> getJuegosFavoritos() {
		return juegosFavoritos;
	}
	public void setJuegosFavoritos(ArrayList<Juego> juegosFavoritos) {
		this.juegosFavoritos = juegosFavoritos;
	}
	
	//REQ FUNCIONAL CONSULTAR CATALOGO DE JUEGOS (PRESTAMOS Y VENTAS)
	public ArrayList<Juego> consultarCatalogoPrestamo(Cafe cafe) {
	    return cafe.consultarCatalogoPrestamo();
	}
	public ArrayList<Juego> consultarCatalogoVenta(Cafe cafe) {
	    return cafe.consultarCatalogoVenta();
	}
	
	//REQ FUNCIONAL SOLICITAR PRESTAMO 
	public abstract void solicitarPrestamo(Cafe cafe, Juego juego, Reserva reserva)throws Exception;
	
	public boolean esFanaticoDe(Juego juego) {
	    return juegosFavoritos.contains(juego);
	}
	public abstract void inscribirTorneo(Cafe cafe, String nombreTorneo, int cupos) throws Exception;{
	}
	

	public abstract void eliminarTorneo(Cafe cafe, String nombreTorneo) throws Exception;{
	}
	
} 


