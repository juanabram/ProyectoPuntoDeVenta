package application.modelo.datos;

import java.util.List;
import application.modelo.Devolucion;

/**
 * Interfaz IDevolucionDAO:
 * Operaciones para la gestión de notas de crédito y devoluciones.
 */
public interface IDevolucionDAO {
    boolean registrarDevolucion(Devolucion dev);
    Devolucion buscarPorFolio(String folio);
    List<Devolucion> listarDevoluciones();
}
