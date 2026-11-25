package application.modelo.datos;

import java.time.LocalDate;
import java.util.List;
import application.modelo.Venta;

/**
 * Interfaz IVentaDAO:
 * Operaciones para el registro y consulta histórica de ventas.
 */
public interface IVentaDAO {
    boolean registrarVenta(Venta venta);
    Venta buscarPorFolio(String folio);
    List<Venta> listarVentas();
    List<Venta> listarVentasPorFecha(LocalDate inicio, LocalDate fin);
    String generarSiguienteFolio();
}
