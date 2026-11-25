package application.modelo.datos;

import java.util.List;
import application.modelo.MovimientoInventario;

/**
 * Interfaz IInventarioDAO:
 * Operaciones para el registro de movimientos y consulta de existencias.
 */
public interface IInventarioDAO {
    boolean registrarMovimiento(MovimientoInventario mov);
    List<MovimientoInventario> listarExistencias();
    List<MovimientoInventario> listarBajoStock();
}
