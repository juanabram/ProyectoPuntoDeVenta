package application.modelo.datos;

import java.util.ArrayList;
import java.util.List;
import application.modelo.MovimientoInventario;
import application.modelo.Producto;

/**
 * Implementación de IInventarioDAO:
 * Gestiona la sincronización de inventario, stock crítico y kardex de movimientos.
 */
public class InventarioDAOImpl implements IInventarioDAO {

    private final RepositorioDatos repo = RepositorioDatos.getInstancia();

    @Override
    public boolean registrarMovimiento(MovimientoInventario mov) {
        repo.registrarMovimiento(mov);
        return true;
    }

    @Override
    public List<MovimientoInventario> listarExistencias() {
        List<Producto> productos = repo.obtenerTodosLosProductos();
        List<MovimientoInventario> lista = new ArrayList<>();
        for (Producto p : productos) {
            lista.add(new MovimientoInventario(
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getStock(),
                p.getStockMinimo(),
                "Consulta Existencias"
            ));
        }
        return lista;
    }

    @Override
    public List<MovimientoInventario> listarBajoStock() {
        List<MovimientoInventario> todos = listarExistencias();
        List<MovimientoInventario> bajoStock = new ArrayList<>();
        for (MovimientoInventario m : todos) {
            if (m.getExistencias() <= m.getStockMinimo()) {
                bajoStock.add(m);
            }
        }
        return bajoStock;
    }
}
