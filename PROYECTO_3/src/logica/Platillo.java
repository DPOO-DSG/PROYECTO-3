package logica;

import java.io.Serializable;

public abstract class Platillo implements Serializable {
	private String nombre;
	private int precio;
	public Platillo(String nombre, int precio) {
		super();
		this.nombre = nombre;
		this.precio = precio;
	}
	
	public String getnombre() {
		return nombre;
	}
	public void setnombre(String nombre) {
		this.nombre = nombre;
	}
	public int getprecio() {
		return precio;
	}
	public void setprecio(int precio) {
		this.precio = precio;
	}

	
	@Override
	public String toString() {
	    return nombre + " - $" + precio;
	}

	
	
	
	
	
	
}
