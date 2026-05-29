package excepciones;

public class TorneoYaExisteException extends Exception {
	public TorneoYaExisteException(String nombre) {
        super("Ya existe un torneo con el nombre: " + nombre);
    }
}
