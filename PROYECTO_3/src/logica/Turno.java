package logica;

import java.io.Serializable;
import java.util.ArrayList;

public class Turno implements Serializable {
	private String jornada;
	private ArrayList<Mesero> meseros;
	private ArrayList<Cocinero> cocineros;
	//Constructor
	public Turno(String jornada, ArrayList<Mesero> listaMeseros,
			ArrayList<Cocinero> listaCocineros) {
		this.jornada = jornada;
		this.meseros = listaMeseros;
		this.cocineros = listaCocineros;
	}
	//Getters
	public String getJornada() {
		return jornada;
	}
	public ArrayList<Mesero> getMeseros() {
		return meseros;
	}
	public ArrayList<Cocinero> getCocineros() {
		return cocineros;
	}
	
	public void agregarEmpleado(Empleado e) {
	    if (e instanceof Cocinero) {
	        cocineros.add((Cocinero) e);
	    } else if (e instanceof Mesero) {
	        meseros.add((Mesero) e);
	    }
	}

	public void removerEmpleado(Empleado e) {
	    if (e instanceof Cocinero) {
	        cocineros.remove(e);
	    } else if (e instanceof Mesero) {
	        meseros.remove(e);
	    }
	}
	
	@Override
	public String toString() {
	    return "Día: " + jornada +
	           " | Meseros: " + meseros.size() +
	           " | Cocineros: " + cocineros.size();
	}
	
	public String mostrarSimple() {
	    return jornada;
	}
	
}
