package logica;

import java.io.Serializable;

public class Bebida extends Platillo implements Serializable{
	private String tipo;
	private boolean alcoholica;
	
	public Bebida(String nombre, int precio, String tipo, boolean alcoholica) {
		super(nombre, precio);
		this.tipo = tipo;
		this.alcoholica = alcoholica;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isAlcoholica() {
		return alcoholica;
	}

	public void setAlcoholica(boolean alcoholica) {
		this.alcoholica = alcoholica;
	}




	

}
