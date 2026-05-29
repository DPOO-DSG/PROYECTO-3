package excepciones;

public class UsuarioYaExisteException extends Exception {

    public UsuarioYaExisteException(String login) {
        super("El usuario con login '" + login + "' ya existe");
    }
}