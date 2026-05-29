package excepciones;

public class NoPerteneceTurnoException extends Exception {
    public NoPerteneceTurnoException(String mensaje) {
        super(mensaje);
    }
}