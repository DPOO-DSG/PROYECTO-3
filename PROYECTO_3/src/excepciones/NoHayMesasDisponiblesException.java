package excepciones;

public class NoHayMesasDisponiblesException extends Exception {
    public NoHayMesasDisponiblesException() {
        super("No hay mesas disponibles para esa fecha y cantidad de personas");
    }
}
