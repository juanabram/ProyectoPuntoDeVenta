package application.modelo.servicio;

import application.modelo.MovimientoInventario;
import application.modelo.Producto;
import application.modelo.datos.RepositorioDatos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Servicio de Lógica de Negocio: Control de Inventario y Alertas de Stock
 */
public class ServicioInventario {

    private final RepositorioDatos repo = RepositorioDatos.getInstancia();
    private final ObservableList<MovimientoInventario> listaInventario = FXCollections.observableArrayList();

    public ServicioInventario() {
        recargarInventario(false);
    }

    public ObservableList<MovimientoInventario> getListaInventario() {
        return listaInventario;
    }

    public void recargarInventario(boolean soloBajoStock) {
        if (soloBajoStock) {
            listaInventario.setAll(repo.listarBajoStock());
        } else {
            listaInventario.setAll(repo.listarExistencias());
        }
    }

    public void registrarEntrada(String codigo, int cantidad, String motivo) throws IllegalArgumentException {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        Producto p = repo.buscarProductoPorCodigo(codigo);
        if (p == null) throw new IllegalArgumentException("Producto no encontrado.");

        int nuevoStock = p.getStock() + cantidad;
        p.setStock(nuevoStock);
        repo.actualizarStock(codigo, nuevoStock);

        repo.registrarMovimiento(new MovimientoInventario(
            p.getCodigo(), p.getNombre(), p.getCategoria(), nuevoStock, p.getStockMinimo(), "Entrada (" + motivo + ")"
        ));
        recargarInventario(false);
    }

    public void registrarSalida(String codigo, int cantidad, String motivo) throws IllegalArgumentException {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        Producto p = repo.buscarProductoPorCodigo(codigo);
        if (p == null) throw new IllegalArgumentException("Producto no encontrado.");
        if (p.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente. Existencias actuales: " + p.getStock());
        }

        int nuevoStock = p.getStock() - cantidad;
        p.setStock(nuevoStock);
        repo.actualizarStock(codigo, nuevoStock);

        repo.registrarMovimiento(new MovimientoInventario(
            p.getCodigo(), p.getNombre(), p.getCategoria(), nuevoStock, p.getStockMinimo(), "Salida (" + motivo + ")"
        ));
        recargarInventario(false);
    }

    public void registrarAjuste(String codigo, int nuevoStockFisico, String motivo) throws IllegalArgumentException {
        if (nuevoStockFisico < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        Producto p = repo.buscarProductoPorCodigo(codigo);
        if (p == null) throw new IllegalArgumentException("Producto no encontrado.");

        p.setStock(nuevoStockFisico);
        repo.actualizarStock(codigo, nuevoStockFisico);

        repo.registrarMovimiento(new MovimientoInventario(
            p.getCodigo(), p.getNombre(), p.getCategoria(), nuevoStockFisico, p.getStockMinimo(), "Ajuste Físico (" + motivo + ")"
        ));
        recargarInventario(false);
    }

    public int obtenerCantidadProductosCriticos() {
        return repo.listarProductosCriticos().size();
    }
}
