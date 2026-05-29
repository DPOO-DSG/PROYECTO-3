package excepciones;

public class LimitePrestamosException extends Exception {
    public LimitePrestamosException(String mensaje) {
        super(mensaje);
    }
}