package logica;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import excepciones.CopiasInsuficientesException;
import excepciones.JuegoNoEncontradoException;

public class InventarioPrestamo implements Serializable {
	private HashMap<Juego,Integer> stock;
	
	//constructor
		public InventarioPrestamo() {
			super();
		    this.stock = new HashMap<>();
		}

		//getters and setters
		

		public void setStock(HashMap<Juego, Integer> stock) {
			this.stock = stock;
		}

		public HashMap<Juego, Integer> getStock() {
			return stock;
		}

		public boolean estaDisponible(Juego juego) {
		    return stock.containsKey(juego) && stock.get(juego) > 0;
		}

		public void registrarPrestamo(Juego juego) throws JuegoNoEncontradoException {

		    if (juego == null || !stock.containsKey(juego)) {
		        throw new JuegoNoEncontradoException("El juego no existe en el inventario");
		    }

		    int cantidad = stock.get(juego);

		    if (cantidad <= 0) {
		        throw new JuegoNoEncontradoException("No hay unidades disponibles");
		    }

		    stock.put(juego, cantidad - 1);
		}
		
		public void registrarDevolucion(Juego juego) throws JuegoNoEncontradoException {

		    if (juego == null) {
		        throw new JuegoNoEncontradoException("Juego inválido");
		    }

		    if (!stock.containsKey(juego)) {
		        throw new JuegoNoEncontradoException("El juego no pertenece al inventario");
		    }

		    stock.put(juego, stock.get(juego) + 1);
		}
		
		public Juego buscarJuego(String nombre) throws JuegoNoEncontradoException {
		    for (Juego j : stock.keySet()) {
		        if (j.getNombre().equalsIgnoreCase(nombre)) {
		            return j;
		        }
		    }
		    throw new JuegoNoEncontradoException(nombre);
		}
		public void validarCopias(Juego juego, int participantes) 
		        throws CopiasInsuficientesException {

		    int disponibles = stock.getOrDefault(juego, 0);

		    if (disponibles < participantes) {
		        throw new CopiasInsuficientesException(juego.getNombre());
		    }
		}
		}

		

		
		
		
