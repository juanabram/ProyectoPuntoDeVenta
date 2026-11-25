package application.modelo.datos;

import java.util.List;
import application.modelo.Producto;

/**
 * Interfaz IProductoDAO:
 * Define las operaciones CRUD y de consulta para los productos del catálogo.
 */
public interface IProductoDAO {
    List<Producto> listarTodos();
    List<Producto> listarActivos();
    List<Producto> listarCriticos();
    Producto buscarPorCodigo(String codigo);
    List<Producto> buscarPorCriterio(String texto, String categoria);
    boolean insertar(Producto producto);
    boolean actualizar(Producto producto);
    boolean bajaLogica(String codigo);
    boolean actualizarStock(String codigo, int nuevoStock);
}
