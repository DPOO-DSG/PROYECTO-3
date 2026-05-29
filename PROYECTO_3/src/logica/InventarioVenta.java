package logica;

import java.io.Serializable;
import java.util.HashMap;
import excepciones.JuegoNoEncontradoException; //
public class InventarioVenta implements Serializable {
	private HashMap<Juego,Integer> stock;
	
	public InventarioVenta() {
		super();
		this.stock = new HashMap<Juego,Integer>();
	}

	public HashMap<Juego, Integer> getStock() {
		return stock;
	}

	public void setStock(HashMap<Juego, Integer> stock) {
		this.stock = stock;
	}

	public boolean estaDisponible(Juego juego) {
		return stock.containsKey(juego) && stock.get(juego) > 0;
	}

	public void registrarVenta(Juego j) throws JuegoNoEncontradoException {
		if (j == null || !stock.containsKey(j)) {
		    throw new JuegoNoEncontradoException("El juego no existe en el inventario de ventas");
		}

		int cantidad = stock.get(j);

		if (cantidad <= 0) {
		    throw new JuegoNoEncontradoException("No hay unidades disponibles para la venta");
		}

		stock.put(j, cantidad - 1);
	}
	
	public void agregarAlInventario(Juego juego, int cantidad) {
		if (stock.containsKey(juego)) {
			stock.put(juego, stock.get(juego) + cantidad);
		} else {
			stock.put(juego, cantidad);
		}
	}
}