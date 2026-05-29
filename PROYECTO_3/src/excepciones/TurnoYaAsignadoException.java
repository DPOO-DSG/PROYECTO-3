package excepciones;

public class TurnoYaAsignadoException extends Exception {

    public TurnoYaAsignadoException(String dia) {
        super("El empleado ya tiene asignado el turno '" + dia + "'");
    }
}