package application.modelo.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import application.modelo.Categoria;
import application.modelo.Devolucion;
import application.modelo.ItemDevolucion;
import application.modelo.ItemVenta;
import application.modelo.MovimientoInventario;
import application.modelo.Producto;
import application.modelo.Venta;

// Acceso centralizado a datos (JDBC con respaldo en memoria)
public class RepositorioDatos {

    private static final String SQL_PROD_BASE =
        "SELECT p.codigo, p.nombre, COALESCE(c.nombre, 'General') AS categoria, " +
        "p.subcategoria, p.precio, p.stock, p.stock_minimo, p.estado " +
        "FROM productos p LEFT JOIN categorias c ON p.id_categoria = c.id_categoria";
    private static final String SQL_VENTAS_BASE =
        "SELECT folio, fecha_hora, cajero, subtotal, descuento, iva, total, metodo_pago, paga_con, cambio FROM ventas";
    private static final String SQL_DEV_BASE =
        "SELECT folio_devolucion, folio_venta_original, fecha_hora, motivo, reintegrar_stock, total_reembolso FROM devoluciones";

    private static RepositorioDatos instancia;

    private final Map<String, Producto> productosMap = new LinkedHashMap<>();
    private final Map<String, Venta> ventasMap = new LinkedHashMap<>();
    private final Map<String, Devolucion> devolucionesMap = new LinkedHashMap<>();
    private final List<Categoria> categoriasList = new ArrayList<>();
    private final List<MovimientoInventario> movimientosList = new ArrayList<>();
    private int contadorFolioVenta = 1001;
    private int contadorFolioDevolucion = 501;

    @FunctionalInterface
    private interface RowMapper<T> {
        T map(Connection conn, ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    private interface SqlTransaction {
        void run(Connection conn) throws SQLException;
    }

    public static synchronized RepositorioDatos getInstancia() {
        if (instancia == null) instancia = new RepositorioDatos();
        return instancia;
    }

    private RepositorioDatos() {
        inicializarDatosSemilla();
    }

    private void inicializarDatosSemilla() {
        String[] cats = {
            "Bebidas", "Lácteos", "Botanas", "Panadería", "Enlatados", "Limpieza", "Higiene Personal",
            "Farmacia", "Dulcería", "Carnes Frías", "Frutas y Verduras", "Mascotas", "Papelería",
            "Abarrotes", "Cuidado del Hogar", "Licores y Cervezas", "Congelados", "Cuidado de Bebé",
            "Despensa General", "Snacks y Confitería"
        };
        for (int i = 0; i < cats.length; i++) categoriasList.add(new Categoria(i + 1, cats[i]));

        Producto[] semilla = {
            new Producto("7501055310884", "Coca Cola Original 600ml", "Bebidas", "Refrescos", 19.50, 48, 10, "Activo"),
            new Producto("7501055300076", "Agua Ciel Purificada 1L", "Bebidas", "Aguas", 14.00, 35, 8, "Activo"),
            new Producto("7501000111207", "Leche Lala Entera 1L", "Lácteos", "Leches", 28.50, 24, 6, "Activo"),
            new Producto("7501000141679", "Yogurt Yoplait Fresa 1kg", "Lácteos", "Yogurt", 38.00, 15, 5, "Activo"),
            new Producto("7501011115683", "Papas Sabritas Sal 45g", "Botanas", "Papas Fritas", 22.00, 30, 8, "Activo"),
            new Producto("7501011131072", "Doritos Nacho 58g", "Botanas", "Tortillas Fritas", 22.00, 25, 6, "Activo"),
            new Producto("7501000153108", "Pan Blanco Bimbo Grande 680g", "Panadería", "Pan de Caja", 45.00, 18, 5, "Activo"),
            new Producto("7501030467022", "Atún Dolores en Agua 140g", "Enlatados", "Pescados", 21.50, 40, 10, "Activo"),
            new Producto("7501025401345", "Cloro Cloralex El Rendidor 950ml", "Limpieza", "Desinfectantes", 18.00, 20, 5, "Activo"),
            new Producto("7501035911476", "Jabón Zote Rosa 400g", "Limpieza", "Jabones", 24.00, 16, 4, "Activo"),
            new Producto("7501008041018", "Pasta Dental Colgate Total 12 100ml", "Higiene Personal", "Cuidado Bucal", 36.50, 12, 4, "Activo"),
            new Producto("7501125134501", "Paracetamol 500mg 20 Tabletas", "Farmacia", "Analgésicos", 25.00, 3, 5, "Activo"),
            new Producto("7501000128908", "Galletas Emperador Chocolate 101g", "Dulcería", "Galletas", 18.50, 22, 6, "Activo"),
            new Producto("7501040001209", "Salchicha FUD Viena 500g", "Carnes Frías", "Salchichas", 42.00, 14, 4, "Activo"),
            new Producto("7501050012345", "Manzana Red Delicious kg", "Frutas y Verduras", "Frutas", 39.90, 2, 5, "Activo"),
            new Producto("7501060012346", "Alimento para Perro Pedigree 2kg", "Mascotas", "Caninos", 115.00, 8, 3, "Activo"),
            new Producto("7501070012347", "Cuaderno Profesional Scribe Rayas", "Papelería", "Cuadernos", 32.00, 15, 5, "Activo"),
            new Producto("7501080012348", "Arroz Verde Valle 1kg", "Abarrotes", "Granos", 34.50, 20, 5, "Activo"),
            new Producto("7501090012349", "Frijol Negro Verde Valle 1kg", "Abarrotes", "Legumbres", 38.00, 18, 5, "Activo"),
            new Producto("7501100012350", "Aceite 1-2-3 Mixto 1L", "Abarrotes", "Aceites", 41.00, 25, 6, "Activo")
        };
        for (Producto p : semilla) productosMap.put(p.getCodigo(), p);
    }

    // Productos

    public synchronized List<Producto> listarTodosProductos() {
        return consultarConRespaldo(SQL_PROD_BASE, this::mapearProducto, () -> new ArrayList<>(productosMap.values()));
    }

    public synchronized List<Producto> listarProductosActivos() {
        return consultarConRespaldo(SQL_PROD_BASE + " WHERE p.estado = 'Activo'", this::mapearProducto,
            () -> productosMap.values().stream()
                .filter(p -> "Activo".equalsIgnoreCase(p.getEstado()))
                .collect(Collectors.toList()));
    }

    public synchronized List<Producto> listarProductosCriticos() {
        return consultarConRespaldo(SQL_PROD_BASE + " WHERE p.stock <= p.stock_minimo AND p.estado = 'Activo'", this::mapearProducto,
            () -> productosMap.values().stream()
                .filter(p -> p.isBajoStock() && "Activo".equalsIgnoreCase(p.getEstado()))
                .collect(Collectors.toList()));
    }

    public synchronized Producto buscarProductoPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return null;
        String clave = codigo.trim();
        List<Producto> bd = consultarLista(SQL_PROD_BASE + " WHERE p.codigo = ?", this::mapearProducto, clave);
        return (bd != null && !bd.isEmpty()) ? bd.get(0) : productosMap.get(clave);
    }

    public synchronized List<Producto> buscarProductosPorCriterio(String texto, String categoria) {
        String txt = (texto != null) ? texto.trim().toLowerCase() : "";
        boolean filtrarCat = categoria != null && !categoria.isEmpty() && !categoria.equalsIgnoreCase("Todas las categorías");
        return listarTodosProductos().stream()
            .filter(p -> txt.isEmpty() || p.getCodigo().toLowerCase().contains(txt) || p.getNombre().toLowerCase().contains(txt))
            .filter(p -> !filtrarCat || p.getCategoria().equalsIgnoreCase(categoria))
            .collect(Collectors.toList());
    }

    public synchronized boolean insertarProducto(Producto p) {
        if (p == null) return false;
        productosMap.put(p.getCodigo(), p);
        Boolean res = ejecutarConConexion(conn -> {
            Integer idCat = resolverIdCategoria(conn, p.getCategoria());
            String sql = "INSERT INTO productos (codigo, nombre, id_categoria, subcategoria, precio, stock, stock_minimo, estado, imagen) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            return ejecutarUpdate(conn, sql, p.getCodigo(), p.getNombre(), idCat, p.getSubcategoria(),
                p.getPrecio(), p.getStock(), p.getStockMinimo(), p.getEstado(), p.getImagen());
        });
        return res != null ? res : true;
    }

    public synchronized boolean actualizarProducto(Producto p) {
        if (p == null) return false;
        boolean enMemoria = productosMap.containsKey(p.getCodigo());
        if (enMemoria) productosMap.put(p.getCodigo(), p);
        Boolean res = ejecutarConConexion(conn -> {
            Integer idCat = resolverIdCategoria(conn, p.getCategoria());
            String sql = "UPDATE productos SET nombre = ?, id_categoria = ?, subcategoria = ?, precio = ?, stock = ?, stock_minimo = ?, estado = ? WHERE codigo = ?";
            return ejecutarUpdate(conn, sql, p.getNombre(), idCat, p.getSubcategoria(),
                p.getPrecio(), p.getStock(), p.getStockMinimo(), p.getEstado(), p.getCodigo());
        });
        return res != null ? res : enMemoria;
    }

    public synchronized boolean actualizarStock(String codigo, int nuevoStock) {
        if (codigo == null) return false;
        String clave = codigo.trim();
        Producto p = productosMap.get(clave);
        if (p != null) p.setStock(nuevoStock);
        Boolean res = ejecutarConConexion(conn -> ejecutarUpdate(conn, "UPDATE productos SET stock = ? WHERE codigo = ?", nuevoStock, clave));
        return res != null ? res : (p != null);
    }

    public synchronized boolean bajaLogicaProducto(String codigo) {
        if (codigo == null) return false;
        String clave = codigo.trim();
        Producto p = productosMap.get(clave);
        if (p != null) p.setEstado("Inactivo");
        Boolean res = ejecutarConConexion(conn -> ejecutarUpdate(conn, "UPDATE productos SET estado = 'Inactivo' WHERE codigo = ?", clave));
        return res != null ? res : (p != null);
    }

    // Ventas

    public synchronized String siguienteFolioVenta() {
        return "TKT-" + (contadorFolioVenta++);
    }

    public synchronized boolean registrarVenta(Venta v) {
        if (v == null) return false;
        ventasMap.put(v.getFolio(), v);
        for (ItemVenta item : v.getItems()) {
            Producto p = productosMap.get(item.getCodigo());
            if (p != null) {
                int nuevo = Math.max(0, p.getStock() - item.getCantidad());
                p.setStock(nuevo);
                movimientosList.add(new MovimientoInventario(p.getCodigo(), p.getNombre(), p.getCategoria(),
                    nuevo, p.getStockMinimo(), "Salida por Venta (" + v.getFolio() + ")"));
            }
        }
        ejecutarTransaccion(conn -> {
            ejecutarUpdate(conn,
                "INSERT INTO ventas (folio, fecha_hora, cajero, subtotal, descuento, iva, total, metodo_pago, paga_con, cambio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                v.getFolio(), v.getFechaHora(), v.getCajero(), v.getSubtotal(), v.getDescuentoTotal(),
                v.getIva(), v.getTotal(), v.getMetodoPago(), v.getPagaCon(), v.getCambio());
            for (ItemVenta item : v.getItems()) {
                ejecutarUpdate(conn,
                    "INSERT INTO detalle_ventas (folio_venta, numero_item, codigo_producto, descripcion, precio_unitario, cantidad, descuento, importe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    v.getFolio(), item.getNumero(), item.getCodigo(), item.getDescripcion(),
                    item.getPrecio(), item.getCantidad(), item.getDescuento(), item.getImporte());
                ejecutarUpdate(conn,
                    "UPDATE productos SET stock = CASE WHEN stock - ? < 0 THEN 0 ELSE stock - ? END WHERE codigo = ?",
                    item.getCantidad(), item.getCantidad(), item.getCodigo());
            }
        });
        return true;
    }

    public synchronized Venta buscarVentaPorFolio(String folio) {
        if (folio == null || folio.trim().isEmpty()) return null;
        String clave = folio.trim();
        List<Venta> bd = consultarLista(SQL_VENTAS_BASE + " WHERE folio = ?", this::mapearVenta, clave);
        return (bd != null && !bd.isEmpty()) ? bd.get(0) : ventasMap.get(clave);
    }

    public synchronized List<Venta> listarVentas() {
        return consultarConRespaldo(SQL_VENTAS_BASE + " ORDER BY fecha_hora DESC", this::mapearVenta,
            () -> new ArrayList<>(ventasMap.values()));
    }

    public synchronized List<Venta> listarVentasPorRango(LocalDate inicio, LocalDate fin) {
        return listarVentas().stream()
            .filter(v -> (inicio == null || !v.getFechaHora().toLocalDate().isBefore(inicio))
                      && (fin == null || !v.getFechaHora().toLocalDate().isAfter(fin)))
            .collect(Collectors.toList());
    }

    // Inventario

    public synchronized boolean registrarMovimiento(MovimientoInventario mov) {
        if (mov == null) return false;
        movimientosList.add(mov);
        String detalle = mov.getUltimoMovimiento() != null ? mov.getUltimoMovimiento() : "Ajuste";
        String tipo = detalle.length() > 50 ? detalle.substring(0, 50) : detalle;
        LocalDateTime fecha = mov.getFechaHora() != null ? mov.getFechaHora() : LocalDateTime.now();
        Boolean res = ejecutarConConexion(conn -> ejecutarUpdate(conn,
            "INSERT INTO movimientos_inventario (codigo_producto, tipo, cantidad, existencias_previas, existencias_resultantes, motivo, fecha_hora) VALUES (?, ?, ?, ?, ?, ?, ?)",
            mov.getCodigo(), tipo, mov.getExistencias(), mov.getExistencias(), mov.getExistencias(), detalle, fecha));
        return res != null ? res : true;
    }

    public synchronized List<MovimientoInventario> listarExistencias() {
        return listarTodosProductos().stream()
            .map(p -> new MovimientoInventario(p.getCodigo(), p.getNombre(), p.getCategoria(),
                p.getStock(), p.getStockMinimo(), "Consulta Existencias"))
            .collect(Collectors.toList());
    }

    public synchronized List<MovimientoInventario> listarBajoStock() {
        return listarExistencias().stream()
            .filter(m -> m.getExistencias() <= m.getStockMinimo())
            .collect(Collectors.toList());
    }

    public synchronized List<MovimientoInventario> obtenerMovimientos() {
        return new ArrayList<>(movimientosList);
    }

    // Devoluciones y categorias

    public synchronized String siguienteFolioDevolucion() {
        return "DEV-" + (contadorFolioDevolucion++);
    }

    public synchronized boolean registrarDevolucion(Devolucion dev) {
        if (dev == null) return false;
        devolucionesMap.put(dev.getFolioDevolucion(), dev);
        if (dev.isReintegrarInventario()) {
            for (ItemDevolucion item : dev.getItems()) {
                Producto p = productosMap.get(item.getCodigo());
                if (p != null) {
                    int nuevo = p.getStock() + item.getCantDevolver();
                    p.setStock(nuevo);
                    movimientosList.add(new MovimientoInventario(p.getCodigo(), p.getNombre(), p.getCategoria(),
                        nuevo, p.getStockMinimo(), "Entrada por Devolución (" + dev.getFolioDevolucion() + ")"));
                }
            }
        }
        ejecutarTransaccion(conn -> {
            ejecutarUpdate(conn,
                "INSERT INTO devoluciones (folio_devolucion, folio_venta_original, fecha_hora, motivo, reintegrar_stock, total_reembolso) VALUES (?, ?, ?, ?, ?, ?)",
                dev.getFolioDevolucion(), dev.getFolioVentaOriginal(), dev.getFechaHora(),
                dev.getMotivo(), dev.isReintegrarInventario(), dev.getTotalReembolso());
            for (ItemDevolucion item : dev.getItems()) {
                ejecutarUpdate(conn,
                    "INSERT INTO detalle_devoluciones (folio_devolucion, codigo_producto, descripcion, precio, cantidad_comprada, cantidad_devuelta, subtotal_reembolso) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    dev.getFolioDevolucion(), item.getCodigo(), item.getDescripcion(), item.getPrecio(),
                    item.getCantComprada(), item.getCantDevolver(), item.getSubtotalReembolso());
                if (dev.isReintegrarInventario()) {
                    ejecutarUpdate(conn, "UPDATE productos SET stock = stock + ? WHERE codigo = ?",
                        item.getCantDevolver(), item.getCodigo());
                }
            }
        });
        return true;
    }

    public synchronized Devolucion buscarDevolucionPorFolio(String folio) {
        if (folio == null || folio.trim().isEmpty()) return null;
        String clave = folio.trim();
        List<Devolucion> bd = consultarLista(SQL_DEV_BASE + " WHERE folio_devolucion = ?", this::mapearDevolucion, clave);
        return (bd != null && !bd.isEmpty()) ? bd.get(0) : devolucionesMap.get(clave);
    }

    public synchronized List<Devolucion> listarDevoluciones() {
        return consultarConRespaldo(SQL_DEV_BASE + " ORDER BY fecha_hora DESC", this::mapearDevolucion,
            () -> new ArrayList<>(devolucionesMap.values()));
    }

    public synchronized List<Categoria> obtenerCategorias() {
        List<Categoria> bd = consultarLista("SELECT id_categoria, nombre FROM categorias ORDER BY id_categoria",
            (conn, rs) -> new Categoria(rs.getInt("id_categoria"), rs.getString("nombre")));
        return (bd != null && !bd.isEmpty()) ? bd : new ArrayList<>(categoriasList);
    }

    // Helpers JDBC y mapeadores

    private <T> List<T> consultarConRespaldo(String sql, RowMapper<T> mapper, Supplier<List<T>> respaldo, Object... params) {
        List<T> bd = consultarLista(sql, mapper, params);
        return bd != null ? bd : respaldo.get();
    }

    private <T> List<T> consultarLista(String sql, RowMapper<T> mapper, Object... params) {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn == null) return null;
        return consultarLista(conn, sql, mapper, params);
    }

    private <T> List<T> consultarLista(Connection conn, String sql, RowMapper<T> mapper, Object... params) {
        List<T> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            aplicarParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapper.map(conn, rs));
            }
            return lista;
        } catch (SQLException e) {
            System.err.println("[RepositorioDatos] Error SQL: " + e.getMessage());
            return null;
        }
    }

    private boolean ejecutarUpdate(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            aplicarParametros(ps, params);
            return ps.executeUpdate() > 0;
        }
    }

    @FunctionalInterface
    private interface SqlBoolAction {
        boolean run(Connection conn) throws SQLException;
    }

    private Boolean ejecutarConConexion(SqlBoolAction accion) {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn == null) return null;
        try {
            return accion.run(conn);
        } catch (SQLException e) {
            System.err.println("[RepositorioDatos] Error SQL: " + e.getMessage());
            return null;
        }
    }

    private void ejecutarTransaccion(SqlTransaction tx) {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn == null) return;
        try {
            conn.setAutoCommit(false);
            tx.run(conn);
            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.err.println("[RepositorioDatos] Error en transacción SQL: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    private void aplicarParametros(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object val = params[i];
            if (val == null) ps.setNull(i + 1, Types.NULL);
            else if (val instanceof LocalDateTime) ps.setTimestamp(i + 1, Timestamp.valueOf((LocalDateTime) val));
            else ps.setObject(i + 1, val);
        }
    }

    private Integer resolverIdCategoria(Connection conn, String nombreCategoria) throws SQLException {
        if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) return null;
        String nombre = nombreCategoria.trim();
        List<Integer> ids = consultarLista(conn, "SELECT id_categoria FROM categorias WHERE LOWER(nombre) = LOWER(?)",
            (c, rs) -> rs.getInt("id_categoria"), nombre);
        if (ids != null && !ids.isEmpty()) return ids.get(0);

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO categorias (nombre) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : null;
            }
        }
    }

    private Producto mapearProducto(Connection conn, ResultSet rs) throws SQLException {
        return new Producto(rs.getString("codigo"), rs.getString("nombre"), rs.getString("categoria"),
            rs.getString("subcategoria"), rs.getDouble("precio"), rs.getInt("stock"),
            rs.getInt("stock_minimo"), rs.getString("estado"));
    }

    private Venta mapearVenta(Connection conn, ResultSet rs) throws SQLException {
        String folio = rs.getString("folio");
        List<ItemVenta> items = consultarLista(conn,
            "SELECT numero_item, codigo_producto, descripcion, precio_unitario, cantidad, descuento FROM detalle_ventas WHERE folio_venta = ? ORDER BY numero_item",
            (c, r) -> new ItemVenta(r.getInt("numero_item"), r.getString("codigo_producto"), r.getString("descripcion"),
                r.getDouble("precio_unitario"), r.getInt("cantidad"), r.getDouble("descuento")), folio);
        Venta v = new Venta(folio, rs.getString("cajero"), items != null ? items : new ArrayList<>(),
            rs.getDouble("subtotal"), rs.getDouble("descuento"), rs.getDouble("iva"),
            rs.getDouble("total"), rs.getString("metodo_pago"), rs.getDouble("paga_con"), rs.getDouble("cambio"));
        Timestamp ts = rs.getTimestamp("fecha_hora");
        if (ts != null) v.setFechaHora(ts.toLocalDateTime());
        return v;
    }

    private Devolucion mapearDevolucion(Connection conn, ResultSet rs) throws SQLException {
        String folio = rs.getString("folio_devolucion");
        List<ItemDevolucion> items = consultarLista(conn,
            "SELECT codigo_producto, descripcion, precio, cantidad_comprada, cantidad_devuelta FROM detalle_devoluciones WHERE folio_devolucion = ?",
            (c, r) -> new ItemDevolucion(r.getString("codigo_producto"), r.getString("descripcion"),
                r.getDouble("precio"), r.getInt("cantidad_comprada"), r.getInt("cantidad_devuelta")), folio);
        Devolucion d = new Devolucion(folio, rs.getString("folio_venta_original"), rs.getString("motivo"),
            rs.getBoolean("reintegrar_stock"), rs.getDouble("total_reembolso"), items != null ? items : new ArrayList<>());
        Timestamp ts = rs.getTimestamp("fecha_hora");
        if (ts != null) d.setFechaHora(ts.toLocalDateTime());
        return d;
    }
}
