package presentacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import excepciones.*;
import logica.*;
import persistencia.PersistenciaCafe;

/**
 * Controlador MVC: único punto de contacto entre la vista (Swing) y la lógica (Cafe).
 * No contiene ningún componente gráfico; solo delega a Cafe y notifica a los observadores.
 */
public class Controlador {

	private Cafe cafe;
	private List<IObserver> observadores = new ArrayList<>();

	public Controlador(Cafe cafe) {
		this.cafe = cafe;
	}

	// =========================================================
	// OBSERVERS
	// =========================================================
	public void agregarObservador(IObserver obs) {
		observadores.add(obs);
	}

	private void notificar() {
		for (IObserver obs : observadores) {
			obs.actualizar();
		}
	}

	// =========================================================
	// PERSISTENCIA
	// =========================================================
	public void guardar() {
		PersistenciaCafe.guardar(cafe);
	}

	// =========================================================
	// AUTH / LOGIN
	// =========================================================
	/** Retorna el objeto autenticado (Cliente, Empleado o Administrador) o null. */
	public Object login(String login, String password) {
		return cafe.login(login, password);
	}

	// =========================================================
	// CLIENTES
	// =========================================================
	public boolean registrarCliente(String login, String password) {
		boolean ok = cafe.crearCliente(login, password, new ArrayList<>(), 0);
		if (ok) notificar();
		return ok;
	}

	public HashMap<String, Cliente> getClientes() {
		return cafe.getClientes();
	}

	public double consultarPuntos(Cliente c) {
		return c.consultarPuntosFidelidad(cafe);
	}

	public ArrayList<Reserva> getReservasCliente(Cliente c) {
		return cafe.getReservasCliente(c);
	}

	public ArrayList<Reserva> getReservasActivasCliente(Cliente c) {
		return cafe.getReservasActivasCliente(c);
	}

	// =========================================================
	// EMPLEADOS
	// =========================================================
	public String crearMesero(String login, String password, String codigo, ArrayList<String> dias) {
		try {
			cafe.crearMesero(login, password, codigo, new ArrayList<>(), dias, new ArrayList<>());
			notificar();
			return "Mesero creado correctamente";
		} catch (UsuarioYaExisteException e) {
			return "Error: " + e.getMessage();
		} catch (TurnoNoExisteException e) {
			return "Error: " + e.getMessage();
		}
	}

	public String crearCocinero(String login, String password, String codigo, ArrayList<String> dias) {
		try {
			cafe.crearCocinero(login, password, codigo, new ArrayList<>(), dias);
			notificar();
			return "Cocinero creado correctamente";
		} catch (UsuarioYaExisteException e) {
			return "Error: " + e.getMessage();
		} catch (TurnoNoExisteException e) {
			return "Error: " + e.getMessage();
		}
	}

	public HashMap<String, Empleado> getEmpleados() {
		return cafe.getEmpleados();
	}

	public ArrayList<Turno> consultarTurnosEmpleado(Empleado e) {
		return cafe.consultarTurnosEmpleado(e);
	}

	// =========================================================
	// TURNOS Y CAMBIOS DE TURNO
	// =========================================================
	public HashMap<String, Turno> getTurnos() {
		return cafe.getTurnos();
	}

	public HashMap<Integer, CambioDeTurno> getSolicitudesPendientes() {
		return cafe.getSolicitudesPendientes();
	}

	public boolean aprobarSolicitud(int id) {
		boolean ok = cafe.aprobarSolicitud(id);
		if (ok) notificar();
		return ok;
	}

	public boolean rechazarSolicitud(int id) {
		boolean ok = cafe.rechazarSolicitud(id);
		if (ok) notificar();
		return ok;
	}

	public String solicitarCambioTurno(Empleado emp, Turno origen, Turno destino) {
		try {
			emp.solicitarCambioTurno(cafe, origen, destino);
			notificar();
			return "Solicitud enviada correctamente";
		} catch (SolicitudInvalidaException | NoPerteneceTurnoException |
				PersonalInsuficienteException | NoPuedeSalirTurnoException e) {
			return "Error: " + e.getMessage();
		}
	}

	public String solicitarIntercambioTurno(Empleado emp, Empleado otro, Turno turnoEmp, Turno turnoOtro) {
		try {
			cafe.crearSolicitudIntercambio(emp, otro, turnoEmp, turnoOtro);
			notificar();
			return "Solicitud de intercambio enviada";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	// =========================================================
	// MENÚ / PLATILLOS
	// =========================================================
	public ArrayList<Platillo> getMenu() {
		return cafe.consultarMenu();
	}

	public boolean anadirPlatilloAMenu(Platillo p) {
		boolean ok = cafe.anadirAMenu(p);
		if (ok) notificar();
		return ok;
	}

	public void crearSolicitudPlatillo(Platillo p) {
		cafe.crearSolicitudSugerencia(p);
		notificar();
	}

	public HashMap<Integer, Platillo> getSolicitudesPlatillo() {
		return cafe.getSolicitudesAnadirPlatillo();
	}

	public boolean aprobarSolicitudPlatillo(int id) {
		boolean ok = cafe.aprobarSolicitudPlatillo(id);
		if (ok) notificar();
		return ok;
	}

	public boolean rechazarSolicitudPlatillo(int id) {
		boolean ok = cafe.rechazarSolicitudPlatillo(id);
		if (ok) notificar();
		return ok;
	}

	// =========================================================
	// INVENTARIO / JUEGOS
	// =========================================================
	public String crearJuego(String categoria, String nombre, int cantidad, int precio,
			int anio, String empresa, int minJug, int maxJug,
			String restriccion, boolean dificil, String tipoInventario) {
		boolean ok = cafe.crearJuego(categoria, nombre, cantidad, precio, anio,
				empresa, minJug, maxJug, restriccion, dificil, tipoInventario);
		if (ok) {
			notificar();
			return "Juego agregado correctamente";
		}
		return "Error al agregar el juego (verifique los datos o si ya existe)";
	}

	public HashMap<Juego, Integer> getStockPrestamo() {
		return cafe.getInventarioPrestamo().getStock();
	}

	public HashMap<Juego, Integer> getStockVenta() {
		return cafe.getInventarioVentas().getStock();
	}

	public ArrayList<Juego> getCatalogoPrestamo() {
		return cafe.consultarCatalogoPrestamo();
	}

	public ArrayList<Juego> getCatalogoVenta() {
		return cafe.consultarCatalogoVenta();
	}

	public String agregarStockPrestamo(String nombreJuego, int cantidad) {
		HashMap<Juego, Integer> stock = cafe.getInventarioPrestamo().getStock();
		for (Juego j : stock.keySet()) {
			if (j.getNombre().equalsIgnoreCase(nombreJuego)) {
				stock.put(j, stock.get(j) + cantidad);
				notificar();
				return "Stock actualizado";
			}
		}
		return "Juego no encontrado";
	}

	public String agregarStockVenta(String nombreJuego, int cantidad) {
		HashMap<Juego, Integer> stock = cafe.getInventarioVentas().getStock();
		for (Juego j : stock.keySet()) {
			if (j.getNombre().equalsIgnoreCase(nombreJuego)) {
				stock.put(j, stock.get(j) + cantidad);
				notificar();
				return "Stock actualizado";
			}
		}
		return "Juego no encontrado";
	}

	// =========================================================
	// PRÉSTAMOS
	// =========================================================
	public String solicitarPrestamo(Usuario u, Juego juego, Reserva reserva) {
		try {
			u.solicitarPrestamo(cafe, juego, reserva);
			notificar();
			return "Préstamo realizado correctamente";
		} catch (EmpleadoEnTurnoException | JuegoNoDisponibleException |
				LimitePrestamosException | BebidaCalienteConAccionException |
				RestriccionEdadException | CapacidadJuegoException |
				ReservaRequeridaException | JuegoNoEncontradoException e) {
			return "Error: " + e.getMessage();
		} catch (Exception e) {
			return "Error inesperado: " + e.getMessage();
		}
	}

	public String devolverPrestamo(String idPrestamo) {
		try {
			cafe.devolverPrestamo(idPrestamo);
			notificar();
			return "Préstamo devuelto correctamente";
		} catch (PrestamoNoEncontradoException | JuegoNoEncontradoException e) {
			return "Error: " + e.getMessage();
		}
	}

	public HashMap<String, Prestamo> getRegistroPrestamos() {
		return cafe.getRegistroPrestamos();
	}

	public ArrayList<Prestamo> getPrestamosUsuario(Usuario u) {
		ArrayList<Prestamo> lista = new ArrayList<>();
		if (u instanceof Cliente) {
			Cliente c = (Cliente) u;
			for (Reserva r : cafe.getReservasCliente(c)) {
				lista.addAll(cafe.getPrestamosClienteEnReserva(c, r));
			}
		} else if (u instanceof Empleado) {
			for (Prestamo p : cafe.getRegistroPrestamos().values()) {
				if (p.getUsuario().equals(u) && !p.isDevuelto()) lista.add(p);
			}
		}
		return lista;
	}

	// =========================================================
	// RESERVAS
	// =========================================================
	public String agendarReserva(Cliente c, int personas, boolean ninos, boolean jovenes, LocalDateTime fecha) {
		try {
			cafe.agendarReserva(c, personas, ninos, jovenes, fecha);
			notificar();
			return "Reserva creada correctamente";
		} catch (NoHayMesasDisponiblesException e) {
			return "No hay mesas disponibles en esa fecha";
		} catch (DatosReservaInvalidosException e) {
			return "Error: " + e.getMessage();
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	public HashMap<String, Reserva> getReservas() {
		return cafe.getReservas();
	}

	// =========================================================
	// PEDIDOS
	// =========================================================
	public String crearPedido(Reserva reserva, Empleado empleado,
			ArrayList<Platillo> platillos, ArrayList<Juego> juegos) {
		try {
			cafe.crearPedido(reserva, empleado, platillos, juegos);
			notificar();
			return "Pedido creado correctamente";
		} catch (AlcoholReservaException e) {
			return "Error: " + e.getMessage();
		} catch (Exception e) {
			return "Error inesperado: " + e.getMessage();
		}
	}

	// =========================================================
	// FACTURAS / VENTAS
	// =========================================================
	public String crearFactura(Cliente c, double propina, boolean usarPuntos,
			boolean bonoTorneo, String codigo, Reserva r) {
		try {
			cafe.crearFactura(c, propina, usarPuntos, bonoTorneo, codigo, r);
			notificar();
			return "Factura generada correctamente";
		} catch (DatosFacturaInvalidosException | AlcoholReservaException |
				BebidaCalienteConAccionException | JuegoNoDisponibleException e) {
			return "Error: " + e.getMessage();
		} catch (Exception e) {
			return "Error inesperado: " + e.getMessage();
		}
	}

	public HashMap<Integer, CompraVenta> getRegistroVentas() {
		return cafe.getRegistroVentas();
	}

	public CompraVenta getFacturaPorReserva(Reserva r) {
		return cafe.getFacturaPorReserva(r);
	}

	public String comprarJuegoEmpleado(Empleado empleado, Juego juego, double propina) {
		try {
			cafe.comprarJuegoEmpleado(empleado, juego, propina);
			notificar();
			return "Compra realizada con éxito";
		} catch (JuegoNoDisponibleException | JuegoNoEncontradoException e) {
			return "Error: " + e.getMessage();
		}
	}

	// =========================================================
	// MESAS
	// =========================================================
	public HashMap<Integer, Mesa> getMesas() {
		return cafe.getMesas();
	}

	// =========================================================
	// TORNEOS
	// =========================================================
	public String crearTorneoAmistoso(String nombre, String nombreJuego, int participantes, String dia) {
		try {
			cafe.crearTorneoAmistoso(nombre, nombreJuego, participantes, dia);
			notificar();
			return "Torneo amistoso creado correctamente";
		} catch (TorneoYaExisteException e) {
			return "Error: " + e.getMessage();
		} catch (JuegoNoEncontradoException e) {
			return "No se creó el torneo: " + e.getMessage();
		} catch (CopiasInsuficientesException e) {
			return "Error: " + e.getMessage();
		}
	}

	public String crearTorneoCompetitivo(String nombre, String nombreJuego, int cupos,
			String dia, int costo, int premio) {
		try {
			cafe.crearTorneoCompetitivo(nombre, nombreJuego, cupos, dia, costo, premio);
			notificar();
			return "Torneo competitivo creado correctamente";
		} catch (TorneoYaExisteException e) {
			return "Error: " + e.getMessage();
		} catch (JuegoNoEncontradoException e) {
			return "No se creó el torneo: " + e.getMessage();
		} catch (CopiasInsuficientesException e) {
			return "Error: " + e.getMessage();
		}
	}

	public HashMap<String, Torneo> getTorneos() {
		return cafe.getTorneos();
	}

	public String inscribirATorneo(String nombreTorneo, Usuario u, int cantidad) {
		try {
			CompraVenta factura = cafe.inscribirATorneo(nombreTorneo, u, cantidad);
			notificar();
			if (factura != null) {
				return "Inscripción exitosa. Total: $" + factura.getTotal();
			}
			return "Inscripción exitosa (entrada gratuita)";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	public String eliminarDeTorneo(String nombreTorneo, Usuario u) {
		try {
			cafe.eliminarDeTorneo(nombreTorneo, u);
			notificar();
			return "Eliminación exitosa";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	public String otorgarPremio(Torneo torneo, Usuario ganador) {
		torneo.otorgarPremio(ganador);
		notificar();
		return "Premio otorgado a: " + ganador.getLogin();
	}

	// =========================================================
	// JUEGOS FAVORITOS / APRENDER JUEGO
	// =========================================================
	public void guardarJuegoFavorito(Usuario u, Juego juego) {
		u.getJuegosFavoritos().add(juego);
	}

	public ArrayList<Juego> getJuegosFavoritos(Usuario u) {
		return u.getJuegosFavoritos();
	}

	public void anadirJuegoAMesero(String login, Juego juego) {
		cafe.anadirJuegoAMesero(login, juego);
	}

	// =========================================================
	// DATOS PARA GRÁFICAS (PLienzo)
	// =========================================================

	/**
	 * Retorna distribución venta vs préstamo de un juego (para gráfica de pastel).
	 * result[0] = copias inventario venta, result[1] = copias inventario préstamo
	 */
	public int[] getDistribucionJuego(String nombreJuego) {
		int venta = 0, prestamo = 0;
		for (HashMap.Entry<Juego, Integer> e : cafe.getInventarioVentas().getStock().entrySet()) {
			if (e.getKey().getNombre().equalsIgnoreCase(nombreJuego)) {
				venta = e.getValue();
			}
		}
		for (HashMap.Entry<Juego, Integer> e : cafe.getInventarioPrestamo().getStock().entrySet()) {
			if (e.getKey().getNombre().equalsIgnoreCase(nombreJuego)) {
				prestamo = e.getValue();
			}
		}
		return new int[]{venta, prestamo};
	}

	/**
	 * Ventas por día en un rango de fechas (para gráfica de barras).
	 * Retorna mapa: fecha (dd/MM) -> double[]{ventasCafeteria, ventasJuegos}
	 */
	public HashMap<String, double[]> getVentasPorRango(LocalDateTime inicio, LocalDateTime fin) {
	    HashMap<String, double[]> mapa = new HashMap<>();
	    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

	    for (CompraVenta cv : cafe.getRegistroVentas().values()) {
	        if (cv.getFecha() == null) continue;
	        LocalDateTime fecha = cv.getFecha();
	        if (fecha.isBefore(inicio) || fecha.isAfter(fin)) continue;

	        String dia = fecha.format(fmt);
	        if (!mapa.containsKey(dia)) mapa.put(dia, new double[]{0, 0});

	        if (cv.getReserva() != null) {
	            for (Pedido ped : cv.getReserva().getPedidos()) {
	                // Sumar platillos
	                if (ped.getPlatillos() != null) {
	                    for (Platillo p : ped.getPlatillos()) {
	                        mapa.get(dia)[0] += p.getprecio();
	                    }
	                }
	                // Sumar juegos
	                if (ped.getJuegos() != null) {
	                    for (Juego j : ped.getJuegos()) {
	                        mapa.get(dia)[1] += j.getprecio();
	                    }
	                }
	            }
	        } else {
	            // Compra directa de empleado sin reserva → va a cafetería
	            mapa.get(dia)[0] += cv.getTotal();
	        }
	    }
	    return mapa;
	}

	/**
	 * Reservas por día de la semana (para gráfica de líneas).
	 * Retorna arreglo de 7 posiciones: lunes=0 ... domingo=6
	 */
	public int[] getReservasPorSemana() {
		int[] conteo = new int[7];
		for (Reserva r : cafe.getReservas().values()) {
			if (r.getFechaReserva() == null) continue;
			int dow = r.getFechaReserva().getDayOfWeek().getValue() - 1; // 0=lunes
			conteo[dow]++;
		}
		return conteo;
	}

	public Cafe getCafe() {
		return cafe;
	}
}
