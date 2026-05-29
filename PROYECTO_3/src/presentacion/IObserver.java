package presentacion;

/**
 * Interfaz Observer: cualquier vista que quiera recibir notificaciones
 * del Controlador debe implementarla.
 */
public interface IObserver {
    void actualizar();
}
