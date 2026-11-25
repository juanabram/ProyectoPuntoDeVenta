package application.modelo.servicio;

import java.util.List;
import application.modelo.datos.IInventarioDAO;
import application.modelo.datos.IProductoDAO;
import application.modelo.datos.InventarioDAOImpl;
import application.modelo.datos.ProductoDAOImpl;
import application.modelo.MovimientoInventario;
import application.modelo.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Servicio de Lógica de Negocio: Control de Inventario y Alertas de Stock
 */
public class ServicioInventario {

    private final IInventarioDAO inventarioDAO = new InventarioDAOImpl();
    private final IProductoDAO productoDAO = new ProductoDAOImpl();
    private final ObservableList<MovimientoInventario> listaInventario = FXCollections.observableArrayList();

    public ServicioInventario() {
        recargarInventario(false);
    }

    public ObservableList<MovimientoInventario> getListaInventario() {
        return listaInventario;
    }

    public void recargarInventario(boolean soloBajoStock) {
        if (soloBajoStock) {
            listaInventario.setAll(inventarioDAO.listarBajoStock());
        } else {
            listaInventario.setAll(inventarioDAO.listarExistencias());
        }
    }

    public void registrarEntrada(String codigo, int cantidad, String motivo) throws IllegalArgumentException {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        Producto p = productoDAO.buscarPorCodigo(codigo);
        if (p == null) throw new IllegalArgumentException("Producto no encontrado.");

        int nuevoStock = p.getStock() + cantidad;
        p.setStock(nuevoStock);
        productoDAO.actualizarStock(codigo, nuevoStock);

        inventarioDAO.registrarMovimiento(new MovimientoInventario(
            p.getCodigo(), p.getNombre(), p.getCategoria(), nuevoStock, p.getStockMinimo(), "Entrada (" + motivo + ")"
        ));
        recargarInventario(false);
    }

    public void registrarSalida(String codigo, int cantidad, String motivo) throws IllegalArgumentException {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        Producto p = productoDAO.buscarPorCodigo(codigo);
        if (p == null) throw new IllegalArgumentException("Producto no encontrado.");
        if (p.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente. Existencias actuales: " + p.getStock());
        }

        int nuevoStock = p.getStock() - cantidad;
        p.setStock(nuevoStock);
        productoDAO.actualizarStock(codigo, nuevoStock);

        inventarioDAO.registrarMovimiento(new MovimientoInventario(
            p.getCodigo(), p.getNombre(), p.getCategoria(), nuevoStock, p.getStockMinimo(), "Salida (" + motivo + ")"
        ));
        recargarInventario(false);
    }

    public void registrarAjuste(String codigo, int nuevoStockFisico, String motivo) throws IllegalArgumentException {
        if (nuevoStockFisico < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        Producto p = productoDAO.buscarPorCodigo(codigo);
        if (p == null) throw new IllegalArgumentException("Producto no encontrado.");

        p.setStock(nuevoStockFisico);
        productoDAO.actualizarStock(codigo, nuevoStockFisico);

        inventarioDAO.registrarMovimiento(new MovimientoInventario(
            p.getCodigo(), p.getNombre(), p.getCategoria(), nuevoStockFisico, p.getStockMinimo(), "Ajuste Físico (" + motivo + ")"
        ));
        recargarInventario(false);
    }

    public int obtenerCantidadProductosCriticos() {
        return productoDAO.listarCriticos().size();
    }
}
