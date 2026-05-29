package logica;

import java.io.Serializable;
import java.util.ArrayList;

public class Pasteleria extends Platillo implements Serializable {

    private ArrayList<String> alergenos;

    public Pasteleria(String nombre, int precio, ArrayList<String> alergenos) {
        super(nombre, precio);
        this.alergenos = alergenos;
    }

    public ArrayList<String> getAlergenos() {
        return alergenos;
    }

    @Override
    public String toString() {

        String alerta = "";

        if (alergenos != null && !alergenos.isEmpty()) {
            alerta = " Alergias: " + String.join(", ", alergenos);
        }

        return getnombre() + " - $" + getprecio() + alerta;
    }
}
	
	
	
	


