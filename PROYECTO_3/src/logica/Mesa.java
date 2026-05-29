package logica;

import java.util.ArrayList;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Mesa implements Serializable {
	
	private int idMesa;
    private int capacidad;
    private ArrayList<Reserva> agenda; 
    private ArrayList<Juego> juegosPrestados;
    private Pedido pedido;
    
	public Mesa(int idMesa, int capacidad, ArrayList<Reserva> agenda, ArrayList<Juego> juegosPrestados, Pedido pedido) {
		super();
		this.idMesa = idMesa;
		this.capacidad = capacidad;
		this.agenda = new ArrayList<>();
		this.juegosPrestados = new ArrayList<Juego>();
		this.pedido = pedido;
	}


    public int getIdMesa() {
		return idMesa;
	}

	public void setIdMesa(int idMesa) {
		this.idMesa = idMesa;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public ArrayList<Reserva> getAgenda() {
		return agenda;
	}

	public void setAgenda(ArrayList<Reserva> agenda) {
		this.agenda = agenda;
	}

    public boolean estaDisponibleEnFecha(LocalDateTime fechaSolicitada) {
        for (Reserva r : agenda) {
            
            if (r.getFechaReserva().equals(fechaSolicitada)) {
                return false; 
            }
        }
        return true; 
    }

    public void agregarReserva(Reserva nuevaReserva) {
        this.agenda.add(nuevaReserva);
    }

	public boolean tieneMenores() {
		return false;
	}


	public ArrayList<Juego> getJuegosPrestados() {
		return juegosPrestados;
	}


	public void setJuegosPrestados(ArrayList<Juego> juegosPrestados) {
		this.juegosPrestados = juegosPrestados;
	}
	
	public Pedido getPedido() {
	    return pedido;
	}

	public void setPedido(Pedido pedido) {
	    this.pedido = pedido;
	}
}