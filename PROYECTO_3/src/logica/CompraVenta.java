package logica;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CompraVenta implements Serializable {
	private int numeroFactura;
	private LocalDateTime fecha;

	private double iva;
	private double subtotal;
	private double propina;
	private double total;
	private int puntosGenerados;
	
	private Reserva reserva;
	private Usuario usuario;
	
	public CompraVenta(int numeroFactura, Usuario usuario, double propina, Reserva reserva) {
	    this.numeroFactura = numeroFactura;
	    this.usuario = usuario;
	    this.reserva = reserva;
	    this.propina = propina;
	    this.fecha = LocalDateTime.now();
	}
	
	public int getNumeroFactura() {
		return numeroFactura;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public double getIva() {
		return iva;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public double getPropina() {
		return propina;
	}

	public int getPuntosGenerados() {
		return puntosGenerados;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void calcularValores() {
	    double suma = 0;

        if (reserva != null) {
            for (Pedido ped : reserva.getPedidos()) {
                for (Platillo p : ped.getPlatillos()) {
                    suma += p.getprecio();
                }
                for (Juego j : ped.getJuegos()) {
                    suma += j.getprecio();
                }
            }
        } 
        
       
        if (reserva != null || subtotal == 0) { 
            this.subtotal = suma;
        }
        
	    this.iva = subtotal * 0.19;
	    this.total = subtotal + iva + propina;

	    this.puntosGenerados = (int) (total / 1000);
	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}
	
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	
	@Override
	public String toString() {

	    StringBuilder sb = new StringBuilder();

	    sb.append("===== FACTURA =====\n");
	    sb.append("ID: ").append(numeroFactura).append("\n");
	    sb.append("Cliente/Empleado: ").append(usuario.getLogin()).append("\n");
	    sb.append("Fecha: ").append(fecha).append("\n");

        if (reserva != null) {
            sb.append("\n--- PEDIDOS ---\n");
            for (Pedido p : reserva.getPedidos()) {
                sb.append(p.toString()).append("\n");
            }
        } else {
            sb.append("\n--- VENTA DIRECTA (Sin Reserva) ---\n");
            
        }

	    sb.append("\nSubtotal: ").append(subtotal).append("\n");
	    sb.append("IVA (19%): ").append(iva).append("\n");
	    sb.append("Propina: ").append(propina).append("\n");

	    sb.append("----------------------\n");
	    sb.append("TOTAL: ").append(total).append("\n");

	    return sb.toString();
	}

}