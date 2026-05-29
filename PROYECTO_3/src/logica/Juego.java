package logica;

import java.io.Serializable;

public class Juego implements Serializable {
	
	private String categoria;
	private String nombre;
	private int cantidad;
	private int precio;
	private int añoPublicacion;
	private String empresaMatriz;
	private int minJugadores;
	private int maxJugadores;
	private String restriccionEdad;
	private boolean dificl;
	

	//Constructor
	
	public Juego(String categoria, String nombre, int cantidad, int precio,
            int añoPublicacion, String empresaMatriz,
            int minJugadores, int maxJugadores,
            String restriccionEdad, boolean dificil) {

   this.categoria = categoria;
   this.nombre = nombre;
   this.cantidad = cantidad;
   this.precio = precio;
   this.añoPublicacion = añoPublicacion;
   this.empresaMatriz = empresaMatriz;
   this.minJugadores = minJugadores;
   this.maxJugadores = maxJugadores;
   this.restriccionEdad = restriccionEdad;
   this.dificl = dificil;
}


	//getters y setters
	
	
	public boolean esDeAccion() {
	    return this.categoria.equals("ACCION");
	}
	
	public boolean isDificl() {
		return dificl;
	}
	
	public void setDificl(boolean dificl) {
		this.dificl = dificl;
	}


	public String getCategoria() {
		return categoria;
	}


	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public int getCantidad() {
		return cantidad;
	}


	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}


	public int getAñoPublicacion() {
		return añoPublicacion;
	}


	public void setAñoPublicacion(int añoPublicacion) {
		this.añoPublicacion = añoPublicacion;
	}


	public String getEmpresaMatriz() {
		return empresaMatriz;
	}


	public void setEmpresaMatriz(String empresaMatriz) {
		this.empresaMatriz = empresaMatriz;
	}


	public int getMinJugadores() {
		return minJugadores;
	}


	public void setMinJugadores(int minJugadores) {
		this.minJugadores = minJugadores;
	}


	public int getMaxJugadores() {
		return maxJugadores;
	}


	public void setMaxJugadores(int maxJugadores) {
		this.maxJugadores = maxJugadores;
	}


	public String getRestriccionEdad() {
		return restriccionEdad;
	}


	public void setRestriccionEdad(String restriccionEdad) {
		this.restriccionEdad = restriccionEdad;
	}


	public int getprecio() {
		return precio;
	}


	public void setprecio(int precio) {
		this.precio = precio;
	}


	public String getEdadMinima() {
		return restriccionEdad;
	}

	@Override
	public String toString() {
		return  nombre +  ": " + precio;
	}


	
	
	

}