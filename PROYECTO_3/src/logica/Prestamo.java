package logica;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Prestamo implements Serializable {
	private String id;
    private Usuario usuario;
    private Juego juego;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean devuelto;
    private Reserva reserva;

    // Constructor
    public Prestamo(String id, Usuario usuario, Juego juego, Reserva reserva) {
        this.id = id;
        this.usuario = usuario;
        this.juego = juego;
        this.fechaInicio = LocalDateTime.now();
        this.devuelto = false;
        this.reserva = reserva;
    }

    public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public void devolver() {
        this.devuelto = true;
        this.fechaFin = LocalDateTime.now();
    }

    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Juego getJuego() {
		return juego;
	}

	public void setJuego(Juego juego) {
		this.juego = juego;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setDevuelto(boolean devuelto) {
		this.devuelto = devuelto;
	}

	public boolean isDevuelto() {
        return devuelto;  
    }
}