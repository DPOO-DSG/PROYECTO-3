package excepciones;

public class PersonalInsuficienteException extends Exception {
    public PersonalInsuficienteException(String mensaje) {
        super(mensaje);
    }
}