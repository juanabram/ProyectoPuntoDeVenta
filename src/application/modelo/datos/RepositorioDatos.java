package application.modelo.datos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import application.modelo.Categoria;
import application.modelo.Devolucion;
import application.modelo.ItemDevolucion;
import application.modelo.ItemVenta;
import application.modelo.MovimientoInventario;
import application.modelo.Producto;
import application.modelo.Venta;

/**
 * Repositorio de Datos Central:
 * Almacena en memoria el estado operativo del Punto de Venta.
 * Provee datos iniciales realistas para pruebas inmediatas y actúa como
 * fuente de datos persistente durante la sesión mientras el compañero
 * configura el servidor de Base de Datos relacional.
 */
public class RepositorioDatos {

    private static RepositorioDatos instancia;

    private final Map<String, Producto> productosMap = new LinkedHashMap<>();
    private final Map<String, Venta> ventasMap = new LinkedHashMap<>();
    private final Map<String, Devolucion> devolucionesMap = new LinkedHashMap<>();
    private final List<Categoria> categoriasList = new ArrayList<>();
    private final List<MovimientoInventario> movimientosList = new ArrayList<>();
    private int contadorFolioVenta = 1001;
    private int contadorFolioDevolucion = 501;

    public static synchronized RepositorioDatos getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioDatos();
        }
        return instancia;
    }

    private RepositorioDatos() {
        inicializarCategorias();
        inicializarProductosSemilla();
    }

    private void inicializarCategorias() {
        String[] nombres = {
            "Bebidas", "Lácteos", "Botanas", "Panadería", "Enlatados",
            "Limpieza", "Higiene Personal", "Farmacia", "Dulcería", "Carnes Frías",
            "Frutas y Verduras", "Mascotas", "Papelería", "Abarrotes", "Cuidado del Hogar",
            "Licores y Cervezas", "Congelados", "Cuidado de Bebé", "Despensa General", "Snacks y Confitería"
        };
        for (int i = 0; i < nombres.length; i++) {
            categoriasList.add(new Categoria(i + 1, nombres[i]));
        }
    }

    private void inicializarProductosSemilla() {
        // Catálogo de productos representativos con códigos de barras comerciales estándar
        agregarProducto(new Producto("7501055310884", "Coca Cola Original 600ml", "Bebidas", "Refrescos", 19.50, 48, 10, "Activo"));
        agregarProducto(new Producto("7501055300076", "Agua Ciel Purificada 1L", "Bebidas", "Aguas", 14.00, 35, 8, "Activo"));
        agregarProducto(new Producto("7501000111207", "Leche Lala Entera 1L", "Lácteos", "Leches", 28.50, 24, 6, "Activo"));
        agregarProducto(new Producto("7501000141679", "Yogurt Yoplait Fresa 1kg", "Lácteos", "Yogurt", 38.00, 15, 5, "Activo"));
        agregarProducto(new Producto("7501011115683", "Papas Sabritas Sal 45g", "Botanas", "Papas Fritas", 22.00, 30, 8, "Activo"));
        agregarProducto(new Producto("7501011131072", "Doritos Nacho 58g", "Botanas", "Tortillas Fritas", 22.00, 25, 6, "Activo"));
        agregarProducto(new Producto("7501000153108", "Pan Blanco Bimbo Grande 680g", "Panadería", "Pan de Caja", 45.00, 18, 5, "Activo"));
        agregarProducto(new Producto("7501030467022", "Atún Dolores en Agua 140g", "Enlatados", "Pescados", 21.50, 40, 10, "Activo"));
        agregarProducto(new Producto("7501025401345", "Cloro Cloralex El Rendidor 950ml", "Limpieza", "Desinfectantes", 18.00, 20, 5, "Activo"));
        agregarProducto(new Producto("7501035911476", "Jabón Zote Rosa 400g", "Limpieza", "Jabones", 24.00, 16, 4, "Activo"));
        agregarProducto(new Producto("7501008041018", "Pasta Dental Colgate Total 12 100ml", "Higiene Personal", "Cuidado Bucal", 36.50, 12, 4, "Activo"));
        agregarProducto(new Producto("7501125134501", "Paracetamol 500mg 20 Tabletas", "Farmacia", "Analgésicos", 25.00, 3, 5, "Activo")); // Stock crítico para probar alertas
        agregarProducto(new Producto("7501000128908", "Galletas Emperador Chocolate 101g", "Dulcería", "Galletas", 18.50, 22, 6, "Activo"));
        agregarProducto(new Producto("7501040001209", "Salchicha FUD Viena 500g", "Carnes Frías", "Salchichas", 42.00, 14, 4, "Activo"));
        agregarProducto(new Producto("7501050012345", "Manzana Red Delicious kg", "Frutas y Verduras", "Frutas", 39.90, 2, 5, "Activo")); // Stock crítico
        agregarProducto(new Producto("7501060012346", "Alimento para Perro Pedigree 2kg", "Mascotas", "Caninos", 115.00, 8, 3, "Activo"));
        agregarProducto(new Producto("7501070012347", "Cuaderno Profesional Scribe Rayas", "Papelería", "Cuadernos", 32.00, 15, 5, "Activo"));
        agregarProducto(new Producto("7501080012348", "Arroz Verde Valle 1kg", "Abarrotes", "Granos", 34.50, 20, 5, "Activo"));
        agregarProducto(new Producto("7501090012349", "Frijol Negro Verde Valle 1kg", "Abarrotes", "Legumbres", 38.00, 18, 5, "Activo"));
        agregarProducto(new Producto("7501100012350", "Aceite 1-2-3 Mixto 1L", "Abarrotes", "Aceites", 41.00, 25, 6, "Activo"));
    }

    public synchronized void agregarProducto(Producto p) {
        productosMap.put(p.getCodigo(), p);
    }

    public synchronized Producto obtenerProducto(String codigo) {
        return productosMap.get(codigo);
    }

    public synchronized List<Producto> obtenerTodosLosProductos() {
        return new ArrayList<>(productosMap.values());
    }

    public synchronized List<Producto> obtenerProductosActivos() {
        List<Producto> lista = new ArrayList<>();
        for (Producto p : productosMap.values()) {
            if ("Activo".equalsIgnoreCase(p.getEstado())) {
                lista.add(p);
            }
        }
        return lista;
    }

    public synchronized List<Producto> obtenerProductosCriticos() {
        List<Producto> lista = new ArrayList<>();
        for (Producto p : productosMap.values()) {
            if (p.isBajoStock() && "Activo".equalsIgnoreCase(p.getEstado())) {
                lista.add(p);
            }
        }
        return lista;
    }

    public synchronized boolean actualizarProducto(Producto p) {
        if (productosMap.containsKey(p.getCodigo())) {
            productosMap.put(p.getCodigo(), p);
            return true;
        }
        return false;
    }

    public synchronized boolean bajaLogicaProducto(String codigo) {
        Producto p = productosMap.get(codigo);
        if (p != null) {
            p.setEstado("Inactivo");
            return true;
        }
        return false;
    }

    public synchronized boolean actualizarStockProducto(String codigo, int nuevoStock) {
        Producto p = productosMap.get(codigo);
        if (p != null) {
            p.setStock(nuevoStock);
            return true;
        }
        return false;
    }

    // Ventas
    public synchronized String siguienteFolioVenta() {
        return "TKT-" + (contadorFolioVenta++);
    }

    public synchronized void registrarVenta(Venta v) {
        ventasMap.put(v.getFolio(), v);
        // Descontar inventario
        for (ItemVenta item : v.getItems()) {
            Producto p = productosMap.get(item.getCodigo());
            if (p != null) {
                int anterior = p.getStock();
                int nuevo = Math.max(0, anterior - item.getCantidad());
                p.setStock(nuevo);
                movimientosList.add(new MovimientoInventario(
                    p.getCodigo(), p.getNombre(), p.getCategoria(), nuevo, p.getStockMinimo(), "Salida por Venta (" + v.getFolio() + ")"
                ));
            }
        }
    }

    public synchronized Venta obtenerVenta(String folio) {
        return ventasMap.get(folio);
    }

    public synchronized List<Venta> obtenerTodasLasVentas() {
        return new ArrayList<>(ventasMap.values());
    }

    // Devoluciones
    public synchronized String siguienteFolioDevolucion() {
        return "DEV-" + (contadorFolioDevolucion++);
    }

    public synchronized void registrarDevolucion(Devolucion dev) {
        devolucionesMap.put(dev.getFolioDevolucion(), dev);
        if (dev.isReintegrarInventario()) {
            for (ItemDevolucion item : dev.getItems()) {
                Producto p = productosMap.get(item.getCodigo());
                if (p != null) {
                    int anterior = p.getStock();
                    int nuevo = anterior + item.getCantDevolver();
                    p.setStock(nuevo);
                    movimientosList.add(new MovimientoInventario(
                        p.getCodigo(), p.getNombre(), p.getCategoria(), nuevo, p.getStockMinimo(), "Entrada por Devolución (" + dev.getFolioDevolucion() + ")"
                    ));
                }
            }
        }
    }

    public synchronized Devolucion obtenerDevolucion(String folio) {
        return devolucionesMap.get(folio);
    }

    public synchronized List<Devolucion> obtenerTodasLasDevoluciones() {
        return new ArrayList<>(devolucionesMap.values());
    }

    // Categorias
    public synchronized List<Categoria> obtenerCategorias() {
        return new ArrayList<>(categoriasList);
    }

    // Movimientos
    public synchronized void registrarMovimiento(MovimientoInventario m) {
        movimientosList.add(m);
    }

    public synchronized List<MovimientoInventario> obtenerMovimientos() {
        return new ArrayList<>(movimientosList);
    }
}
