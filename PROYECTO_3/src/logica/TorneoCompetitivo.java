package logica;

import java.io.Serializable;

public class TorneoCompetitivo extends Torneo implements Serializable {
    
    private int costoEntrada;
    private int premio;

    public TorneoCompetitivo(String nombre, Juego juego, int cuposMaximos, String dia,
            int costoEntrada, int premio) {
        super(nombre, juego, cuposMaximos, dia);
        this.costoEntrada = costoEntrada;
        this.premio = premio;
    }

    @Override 
    public CompraVenta inscribirUsuario(Cafe cafe, Usuario u, int cantidad) throws Exception {
        super.inscribirUsuario(cafe, u, cantidad);

        if (u instanceof Cliente) {
            Cliente c = (Cliente) u;

            int subtotalInscripcion = this.costoEntrada * cantidad;
            int numeroFactura = cafe.getRegistroVentas().size() + 1;

            CompraVenta factura = new CompraVenta(numeroFactura, c, 0.0, null);
            factura.setSubtotal(subtotalInscripcion);
            factura.calcularValores();

            cafe.getRegistroVentas().put(numeroFactura, factura);

            double nuevosPuntos = factura.getTotal() * 0.01;
            c.setPuntosFidelidad(c.getPuntosFidelidad() + nuevosPuntos);

            return factura;
        }

        return null; // empleado no paga
    }

    public String otorgarPremio(Usuario ganador) {
        if (ganador instanceof Cliente) {
            return "Cliente ganador: " + ganador.getLogin() + " Premio: $" + this.premio;
        } else {
            return "Empleado ganador: " + ganador.getLogin() + " (sin premio monetario)";
        }
    
    }
}