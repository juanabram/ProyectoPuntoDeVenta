package application.modelo.servicio;

import java.util.List;
import application.modelo.Producto;
import application.modelo.datos.RepositorioDatos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Servicio de Lógica de Negocio: Catálogo de Productos y Categorías
 */
public class ServicioProductos {

    private final RepositorioDatos repo = RepositorioDatos.getInstancia();
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    public ServicioProductos() {
        recargarProductos();
    }

    public ObservableList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void recargarProductos() {
        listaProductos.setAll(repo.listarTodosProductos());
    }

    public void filtrar(String busqueda, String categoria) {
        List<Producto> res = repo.buscarProductosPorCriterio(busqueda, categoria);
        listaProductos.setAll(res);
    }

    public void registrarProducto(String codigo, String nombre, String categoria, String subcategoria,
                                  double precio, int stock, int stockMinimo) throws IllegalArgumentException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de barras es obligatorio.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a $0.00.");
        }
        if (stock < 0 || stockMinimo < 0) {
            throw new IllegalArgumentException("Las cantidades de stock no pueden ser negativas.");
        }

        if (repo.buscarProductoPorCodigo(codigo.trim()) != null) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el código '" + codigo + "'.");
        }

        Producto nuevo = new Producto(
            codigo.trim(),
            nombre.trim(),
            categoria != null ? categoria : "General",
            subcategoria != null ? subcategoria : "General",
            precio, stock, stockMinimo, "Activo"
        );

        repo.insertarProducto(nuevo);
        recargarProductos();
    }

    public void modificarProducto(Producto p, String nuevoNombre, String nuevaCategoria, String nuevaSubcategoria,
                                  double nuevoPrecio, int nuevoStock, int nuevoStockMinimo) throws IllegalArgumentException {
        if (p == null) return;
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (nuevoPrecio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (nuevoStock < 0 || nuevoStockMinimo < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        p.setNombre(nuevoNombre.trim());
        p.setCategoria(nuevaCategoria);
        p.setSubcategoria(nuevaSubcategoria);
        p.setPrecio(nuevoPrecio);
        p.setStock(nuevoStock);
        p.setStockMinimo(nuevoStockMinimo);

        repo.actualizarProducto(p);
        recargarProductos();
    }

    public void darBajaLogica(Producto p) {
        if (p != null) {
            repo.bajaLogicaProducto(p.getCodigo());
            recargarProductos();
        }
    }
}
