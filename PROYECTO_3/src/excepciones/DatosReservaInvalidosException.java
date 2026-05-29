package excepciones;

public class DatosReservaInvalidosException extends Exception {
    public DatosReservaInvalidosException() {
        super("Datos inválidos para la reserva");
    }
}