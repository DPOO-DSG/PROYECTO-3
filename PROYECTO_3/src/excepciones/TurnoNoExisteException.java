package excepciones;


public class TurnoNoExisteException extends Exception {

    public TurnoNoExisteException(String dia) {
        super("El turno para el dia '" + dia + "' no existe");
    }
}