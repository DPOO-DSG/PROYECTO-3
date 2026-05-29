package excepciones;

public class EmpleadoNoExisteException extends Exception {

    public EmpleadoNoExisteException(String login) {
        super("El empleado '" + login + "' no existe");
    }
}