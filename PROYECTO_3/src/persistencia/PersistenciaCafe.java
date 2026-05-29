package persistencia;

import java.io.*;

import logica.Cafe;

public class PersistenciaCafe {

    private static final String RUTA = "cafe.dat";

    public static void guardar(Cafe cafe) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(RUTA))) {
            out.writeObject(cafe);
            System.out.println("Datos guardados correctamente");
        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    public static Cafe cargar() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(RUTA))) {
            return (Cafe) in.readObject();
        } catch (Exception e) {
            System.out.println("No había datos guardados, se crea un nuevo café");
            return null;
        }
    }
}