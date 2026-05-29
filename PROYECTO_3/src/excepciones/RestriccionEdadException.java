package excepciones;

public class RestriccionEdadException extends Exception {
    public RestriccionEdadException(String mensaje) {
        super(mensaje);
    }
}