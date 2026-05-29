package excepciones;

public class CopiasInsuficientesException extends Exception {
    public CopiasInsuficientesException(String nombre) {
        super("No hay suficientes copias del juego para el torneo: " + nombre);
    }
}