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
import application.modelo.Categoria;
import application.modelo.Devolucion;
import application.modelo.ItemDevolucion;
import application.modelo.ItemVenta;
import application.modelo.MovimientoInventario;
import application.modelo.Producto;
import application.modelo.Venta;

/**
 * Repositorio de Datos Central:
 * Concentra las operaciones CRUD con patrón resiliente: utiliza JDBC a través
 * de {@link ConexionBD} cuando hay conexión disponible y opera en memoria
 * como respaldo automático con datos semilla realistas.
 */
public class RepositorioDatos {

    private static final String SQL_SELECT_PRODUCTOS_BASE =
        "SELECT p.codigo, p.nombre, COALESCE(c.nombre, 'General') AS categoria, " +
        "p.subcategoria, p.precio, p.stock, p.stock_minimo, p.estado " +
        "FROM productos p LEFT JOIN categorias c ON p.id_categoria = c.id_categoria";

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
        // Catálogo inicial con códigos de barras comerciales estándar
        productosMap.put("7501055310884", new Producto("7501055310884", "Coca Cola Original 600ml", "Bebidas", "Refrescos", 19.50, 48, 10, "Activo"));
        productosMap.put("7501055300076", new Producto("7501055300076", "Agua Ciel Purificada 1L", "Bebidas", "Aguas", 14.00, 35, 8, "Activo"));
        productosMap.put("7501000111207", new Producto("7501000111207", "Leche Lala Entera 1L", "Lácteos", "Leches", 28.50, 24, 6, "Activo"));
        productosMap.put("7501000141679", new Producto("7501000141679", "Yogurt Yoplait Fresa 1kg", "Lácteos", "Yogurt", 38.00, 15, 5, "Activo"));
        productosMap.put("7501011115683", new Producto("7501011115683", "Papas Sabritas Sal 45g", "Botanas", "Papas Fritas", 22.00, 30, 8, "Activo"));
        productosMap.put("7501011131072", new Producto("7501011131072", "Doritos Nacho 58g", "Botanas", "Tortillas Fritas", 22.00, 25, 6, "Activo"));
        productosMap.put("7501000153108", new Producto("7501000153108", "Pan Blanco Bimbo Grande 680g", "Panadería", "Pan de Caja", 45.00, 18, 5, "Activo"));
        productosMap.put("7501030467022", new Producto("7501030467022", "Atún Dolores en Agua 140g", "Enlatados", "Pescados", 21.50, 40, 10, "Activo"));
        productosMap.put("7501025401345", new Producto("7501025401345", "Cloro Cloralex El Rendidor 950ml", "Limpieza", "Desinfectantes", 18.00, 20, 5, "Activo"));
        productosMap.put("7501035911476", new Producto("7501035911476", "Jabón Zote Rosa 400g", "Limpieza", "Jabones", 24.00, 16, 4, "Activo"));
        productosMap.put("7501008041018", new Producto("7501008041018", "Pasta Dental Colgate Total 12 100ml", "Higiene Personal", "Cuidado Bucal", 36.50, 12, 4, "Activo"));
        productosMap.put("7501125134501", new Producto("7501125134501", "Paracetamol 500mg 20 Tabletas", "Farmacia", "Analgésicos", 25.00, 3, 5, "Activo")); // Stock crítico
        productosMap.put("7501000128908", new Producto("7501000128908", "Galletas Emperador Chocolate 101g", "Dulcería", "Galletas", 18.50, 22, 6, "Activo"));
        productosMap.put("7501040001209", new Producto("7501040001209", "Salchicha FUD Viena 500g", "Carnes Frías", "Salchichas", 42.00, 14, 4, "Activo"));
        productosMap.put("7501050012345", new Producto("7501050012345", "Manzana Red Delicious kg", "Frutas y Verduras", "Frutas", 39.90, 2, 5, "Activo")); // Stock crítico
        productosMap.put("7501060012346", new Producto("7501060012346", "Alimento para Perro Pedigree 2kg", "Mascotas", "Caninos", 115.00, 8, 3, "Activo"));
        productosMap.put("7501070012347", new Producto("7501070012347", "Cuaderno Profesional Scribe Rayas", "Papelería", "Cuadernos", 32.00, 15, 5, "Activo"));
        productosMap.put("7501080012348", new Producto("7501080012348", "Arroz Verde Valle 1kg", "Abarrotes", "Granos", 34.50, 20, 5, "Activo"));
        productosMap.put("7501090012349", new Producto("7501090012349", "Frijol Negro Verde Valle 1kg", "Abarrotes", "Legumbres", 38.00, 18, 5, "Activo"));
        productosMap.put("7501100012350", new Producto("7501100012350", "Aceite 1-2-3 Mixto 1L", "Abarrotes", "Aceites", 41.00, 25, 6, "Activo"));
    }

    // =========================================================================
    // PRODUCTOS
    // =========================================================================

    public synchronized List<Producto> listarTodosProductos() {
        List<Producto> desdeBD = consultarProductosSQL(SQL_SELECT_PRODUCTOS_BASE);
        if (desdeBD != null) {
            return desdeBD;
        }
        return new ArrayList<>(productosMap.values());
    }

    public synchronized List<Producto> listarProductosActivos() {
        List<Producto> desdeBD = consultarProductosSQL(SQL_SELECT_PRODUCTOS_BASE + " WHERE p.estado = 'Activo'");
        if (desdeBD != null) {
            return desdeBD;
        }
        List<Producto> lista = new ArrayList<>();
        for (Producto p : productosMap.values()) {
            if ("Activo".equalsIgnoreCase(p.getEstado())) {
                lista.add(p);
            }
        }
        return lista;
    }

    public synchronized List<Producto> listarProductosCriticos() {
        List<Producto> desdeBD = consultarProductosSQL(
            SQL_SELECT_PRODUCTOS_BASE + " WHERE p.stock <= p.stock_minimo AND p.estado = 'Activo'"
        );
        if (desdeBD != null) {
            return desdeBD;
        }
        List<Producto> lista = new ArrayList<>();
        for (Producto p : productosMap.values()) {
            if (p.isBajoStock() && "Activo".equalsIgnoreCase(p.getEstado())) {
                lista.add(p);
            }
        }
        return lista;
    }

    public synchronized Producto buscarProductoPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        String clave = codigo.trim();
        List<Producto> desdeBD = consultarProductosSQL(SQL_SELECT_PRODUCTOS_BASE + " WHERE p.codigo = ?", clave);
        if (desdeBD != null && !desdeBD.isEmpty()) {
            return desdeBD.get(0);
        }
        return productosMap.get(clave);
    }

    public synchronized List<Producto> buscarProductosPorCriterio(String texto, String categoria) {
        List<Producto> todos = listarTodosProductos();
        List<Producto> filtrados = new ArrayList<>();
        String txt = (texto != null) ? texto.trim().toLowerCase() : "";
        boolean filtrarCat = (categoria != null && !categoria.isEmpty()
            && !categoria.equalsIgnoreCase("Todas las categorías"));

        for (Producto p : todos) {
            boolean coincideTexto = txt.isEmpty()
                || p.getCodigo().toLowerCase().contains(txt)
                || p.getNombre().toLowerCase().contains(txt);
            boolean coincideCat = !filtrarCat || p.getCategoria().equalsIgnoreCase(categoria);
            if (coincideTexto && coincideCat) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    public synchronized boolean insertarProducto(Producto p) {
        if (p == null) return false;
        productosMap.put(p.getCodigo(), p);

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "INSERT INTO productos (codigo, nombre, id_categoria, subcategoria, precio, stock, stock_minimo, estado, imagen) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                Integer idCategoria = resolverIdCategoria(conn, p.getCategoria());
                ps.setString(1, p.getCodigo());
                ps.setString(2, p.getNombre());
                if (idCategoria != null) {
                    ps.setInt(3, idCategoria);
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setString(4, p.getSubcategoria());
                ps.setDouble(5, p.getPrecio());
                ps.setInt(6, p.getStock());
                ps.setInt(7, p.getStockMinimo());
                ps.setString(8, p.getEstado());
                ps.setString(9, p.getImagen());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error al insertar producto en BD: " + e.getMessage());
            }
        }
        return true;
    }

    public synchronized boolean actualizarProducto(Producto p) {
        if (p == null) return false;
        boolean actualizadoEnMemoria = false;
        if (productosMap.containsKey(p.getCodigo())) {
            productosMap.put(p.getCodigo(), p);
            actualizadoEnMemoria = true;
        }

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "UPDATE productos SET nombre = ?, id_categoria = ?, subcategoria = ?, precio = ?, stock = ?, stock_minimo = ?, estado = ? WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                Integer idCategoria = resolverIdCategoria(conn, p.getCategoria());
                ps.setString(1, p.getNombre());
                if (idCategoria != null) {
                    ps.setInt(2, idCategoria);
                } else {
                    ps.setNull(2, Types.INTEGER);
                }
                ps.setString(3, p.getSubcategoria());
                ps.setDouble(4, p.getPrecio());
                ps.setInt(5, p.getStock());
                ps.setInt(6, p.getStockMinimo());
                ps.setString(7, p.getEstado());
                ps.setString(8, p.getCodigo());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error al actualizar producto en BD: " + e.getMessage());
            }
        }
        return actualizadoEnMemoria;
    }

    public synchronized boolean actualizarStock(String codigo, int nuevoStock) {
        if (codigo == null) return false;
        boolean actualizadoEnMemoria = false;
        Producto p = productosMap.get(codigo.trim());
        if (p != null) {
            p.setStock(nuevoStock);
            actualizadoEnMemoria = true;
        }

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "UPDATE productos SET stock = ? WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, nuevoStock);
                ps.setString(2, codigo.trim());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error actualizando stock en BD: " + e.getMessage());
            }
        }
        return actualizadoEnMemoria;
    }

    public synchronized boolean bajaLogicaProducto(String codigo) {
        if (codigo == null) return false;
        boolean dadoDeBajaEnMemoria = false;
        Producto p = productosMap.get(codigo.trim());
        if (p != null) {
            p.setEstado("Inactivo");
            dadoDeBajaEnMemoria = true;
        }

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "UPDATE productos SET estado = 'Inactivo' WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigo.trim());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error en baja lógica en BD: " + e.getMessage());
            }
        }
        return dadoDeBajaEnMemoria;
    }

    // =========================================================================
    // VENTAS
    // =========================================================================

    public synchronized String siguienteFolioVenta() {
        return "TKT-" + (contadorFolioVenta++);
    }

    public synchronized boolean registrarVenta(Venta v) {
        if (v == null) return false;
        ventasMap.put(v.getFolio(), v);

        // Descontar inventario en memoria y registrar kardex
        for (ItemVenta item : v.getItems()) {
            Producto p = productosMap.get(item.getCodigo());
            if (p != null) {
                int nuevo = Math.max(0, p.getStock() - item.getCantidad());
                p.setStock(nuevo);
                movimientosList.add(new MovimientoInventario(
                    p.getCodigo(), p.getNombre(), p.getCategoria(),
                    nuevo, p.getStockMinimo(), "Salida por Venta (" + v.getFolio() + ")"
                ));
            }
        }

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sqlVenta = "INSERT INTO ventas (folio, fecha_hora, cajero, subtotal, descuento, iva, total, metodo_pago, paga_con, cambio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlDetalle = "INSERT INTO detalle_ventas (folio_venta, numero_item, codigo_producto, descripcion, precio_unitario, cantidad, descuento, importe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE productos SET stock = CASE WHEN stock - ? < 0 THEN 0 ELSE stock - ? END WHERE codigo = ?";
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta)) {
                    psVenta.setString(1, v.getFolio());
                    psVenta.setTimestamp(2, Timestamp.valueOf(v.getFechaHora()));
                    psVenta.setString(3, v.getCajero());
                    psVenta.setDouble(4, v.getSubtotal());
                    psVenta.setDouble(5, v.getDescuentoTotal());
                    psVenta.setDouble(6, v.getIva());
                    psVenta.setDouble(7, v.getTotal());
                    psVenta.setString(8, v.getMetodoPago());
                    psVenta.setDouble(9, v.getPagaCon());
                    psVenta.setDouble(10, v.getCambio());
                    psVenta.executeUpdate();
                }

                try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                     PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                    for (ItemVenta item : v.getItems()) {
                        psDetalle.setString(1, v.getFolio());
                        psDetalle.setInt(2, item.getNumero());
                        psDetalle.setString(3, item.getCodigo());
                        psDetalle.setString(4, item.getDescripcion());
                        psDetalle.setDouble(5, item.getPrecio());
                        psDetalle.setInt(6, item.getCantidad());
                        psDetalle.setDouble(7, item.getDescuento());
                        psDetalle.setDouble(8, item.getImporte());
                        psDetalle.addBatch();

                        psStock.setInt(1, item.getCantidad());
                        psStock.setInt(2, item.getCantidad());
                        psStock.setString(3, item.getCodigo());
                        psStock.addBatch();
                    }
                    psDetalle.executeBatch();
                    psStock.executeBatch();
                }

                conn.commit();
                conn.setAutoCommit(true);
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    // Ignorar error de rollback
                }
                System.err.println("[RepositorioDatos] Error registrando venta en BD: " + e.getMessage());
            }
        }
        return true;
    }

    public synchronized Venta buscarVentaPorFolio(String folio) {
        if (folio == null || folio.trim().isEmpty()) return null;
        String clave = folio.trim();

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT folio, fecha_hora, cajero, subtotal, descuento, iva, total, metodo_pago, paga_con, cambio FROM ventas WHERE folio = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clave);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearVentaConDetalles(conn, rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error buscando venta en BD: " + e.getMessage());
            }
        }
        return ventasMap.get(clave);
    }

    public synchronized List<Venta> listarVentas() {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT folio, fecha_hora, cajero, subtotal, descuento, iva, total, metodo_pago, paga_con, cambio FROM ventas ORDER BY fecha_hora DESC";
            List<Venta> lista = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearVentaConDetalles(conn, rs));
                }
                return lista;
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error listando ventas en BD: " + e.getMessage());
            }
        }
        return new ArrayList<>(ventasMap.values());
    }

    public synchronized List<Venta> listarVentasPorRango(LocalDate inicio, LocalDate fin) {
        List<Venta> todas = listarVentas();
        List<Venta> filtradas = new ArrayList<>();
        for (Venta v : todas) {
            LocalDate f = v.getFechaHora().toLocalDate();
            boolean despuesOIgual = (inicio == null) || !f.isBefore(inicio);
            boolean antesOIgual = (fin == null) || !f.isAfter(fin);
            if (despuesOIgual && antesOIgual) {
                filtradas.add(v);
            }
        }
        return filtradas;
    }

    // =========================================================================
    // INVENTARIO
    // =========================================================================

    public synchronized boolean registrarMovimiento(MovimientoInventario mov) {
        if (mov == null) return false;
        movimientosList.add(mov);

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "INSERT INTO movimientos_inventario (codigo_producto, tipo, cantidad, existencias_previas, existencias_resultantes, motivo, fecha_hora) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String detalle = mov.getUltimoMovimiento() != null ? mov.getUltimoMovimiento() : "Ajuste";
                String tipo = detalle.length() > 50 ? detalle.substring(0, 50) : detalle;
                ps.setString(1, mov.getCodigo());
                ps.setString(2, tipo);
                ps.setInt(3, mov.getExistencias());
                ps.setInt(4, mov.getExistencias());
                ps.setInt(5, mov.getExistencias());
                ps.setString(6, detalle);
                ps.setTimestamp(7, Timestamp.valueOf(mov.getFechaHora() != null ? mov.getFechaHora() : LocalDateTime.now()));
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error registrando movimiento en BD: " + e.getMessage());
            }
        }
        return true;
    }

    public synchronized List<MovimientoInventario> listarExistencias() {
        List<Producto> productos = listarTodosProductos();
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

    public synchronized List<MovimientoInventario> listarBajoStock() {
        List<MovimientoInventario> todos = listarExistencias();
        List<MovimientoInventario> bajoStock = new ArrayList<>();
        for (MovimientoInventario m : todos) {
            if (m.getExistencias() <= m.getStockMinimo()) {
                bajoStock.add(m);
            }
        }
        return bajoStock;
    }

    public synchronized List<MovimientoInventario> obtenerMovimientos() {
        return new ArrayList<>(movimientosList);
    }

    // =========================================================================
    // DEVOLUCIONES
    // =========================================================================

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
                    movimientosList.add(new MovimientoInventario(
                        p.getCodigo(), p.getNombre(), p.getCategoria(),
                        nuevo, p.getStockMinimo(), "Entrada por Devolución (" + dev.getFolioDevolucion() + ")"
                    ));
                }
            }
        }

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sqlDev = "INSERT INTO devoluciones (folio_devolucion, folio_venta_original, fecha_hora, motivo, reintegrar_stock, total_reembolso) VALUES (?, ?, ?, ?, ?, ?)";
            String sqlDetalle = "INSERT INTO detalle_devoluciones (folio_devolucion, codigo_producto, descripcion, precio, cantidad_comprada, cantidad_devuelta, subtotal_reembolso) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE productos SET stock = stock + ? WHERE codigo = ?";
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement psDev = conn.prepareStatement(sqlDev)) {
                    psDev.setString(1, dev.getFolioDevolucion());
                    psDev.setString(2, dev.getFolioVentaOriginal());
                    psDev.setTimestamp(3, Timestamp.valueOf(dev.getFechaHora()));
                    psDev.setString(4, dev.getMotivo());
                    psDev.setBoolean(5, dev.isReintegrarInventario());
                    psDev.setDouble(6, dev.getTotalReembolso());
                    psDev.executeUpdate();
                }

                try (PreparedStatement psDet = conn.prepareStatement(sqlDetalle);
                     PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                    for (ItemDevolucion item : dev.getItems()) {
                        psDet.setString(1, dev.getFolioDevolucion());
                        psDet.setString(2, item.getCodigo());
                        psDet.setString(3, item.getDescripcion());
                        psDet.setDouble(4, item.getPrecio());
                        psDet.setInt(5, item.getCantComprada());
                        psDet.setInt(6, item.getCantDevolver());
                        psDet.setDouble(7, item.getSubtotalReembolso());
                        psDet.addBatch();

                        if (dev.isReintegrarInventario()) {
                            psStock.setInt(1, item.getCantDevolver());
                            psStock.setString(2, item.getCodigo());
                            psStock.addBatch();
                        }
                    }
                    psDet.executeBatch();
                    if (dev.isReintegrarInventario()) {
                        psStock.executeBatch();
                    }
                }

                conn.commit();
                conn.setAutoCommit(true);
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    // Ignorar error de rollback
                }
                System.err.println("[RepositorioDatos] Error registrando devolución en BD: " + e.getMessage());
            }
        }
        return true;
    }

    public synchronized Devolucion buscarDevolucionPorFolio(String folio) {
        if (folio == null || folio.trim().isEmpty()) return null;
        String clave = folio.trim();

        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT folio_devolucion, folio_venta_original, fecha_hora, motivo, reintegrar_stock, total_reembolso FROM devoluciones WHERE folio_devolucion = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clave);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearDevolucionConDetalles(conn, rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error buscando devolución en BD: " + e.getMessage());
            }
        }
        return devolucionesMap.get(clave);
    }

    public synchronized List<Devolucion> listarDevoluciones() {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT folio_devolucion, folio_venta_original, fecha_hora, motivo, reintegrar_stock, total_reembolso FROM devoluciones ORDER BY fecha_hora DESC";
            List<Devolucion> lista = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDevolucionConDetalles(conn, rs));
                }
                return lista;
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error listando devoluciones en BD: " + e.getMessage());
            }
        }
        return new ArrayList<>(devolucionesMap.values());
    }

    // =========================================================================
    // CATEGORÍAS
    // =========================================================================

    public synchronized List<Categoria> obtenerCategorias() {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT id_categoria, nombre FROM categorias ORDER BY id_categoria";
            List<Categoria> lista = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre")));
                }
                if (!lista.isEmpty()) {
                    return lista;
                }
            } catch (SQLException e) {
                System.err.println("[RepositorioDatos] Error listando categorías en BD: " + e.getMessage());
            }
        }
        return new ArrayList<>(categoriasList);
    }

    // =========================================================================
    // HELPERS PRIVADOS JDBC
    // =========================================================================

    private List<Producto> consultarProductosSQL(String sql, String... params) {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn == null) {
            return null;
        }
        List<Producto> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProducto(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            System.err.println("[RepositorioDatos] Error en consulta SQL de productos: " + e.getMessage());
            return null;
        }
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getString("categoria"),
            rs.getString("subcategoria"),
            rs.getDouble("precio"),
            rs.getInt("stock"),
            rs.getInt("stock_minimo"),
            rs.getString("estado")
        );
    }

    private Integer resolverIdCategoria(Connection conn, String nombreCategoria) throws SQLException {
        if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) {
            return null;
        }
        String nombre = nombreCategoria.trim();
        String sqlBuscar = "SELECT id_categoria FROM categorias WHERE LOWER(nombre) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlBuscar)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_categoria");
                }
            }
        }

        String sqlInsertar = "INSERT INTO categorias (nombre) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return null;
    }

    private Venta mapearVentaConDetalles(Connection conn, ResultSet rs) throws SQLException {
        String folio = rs.getString("folio");
        List<ItemVenta> items = new ArrayList<>();
        String sqlDetalles = "SELECT numero_item, codigo_producto, descripcion, precio_unitario, cantidad, descuento FROM detalle_ventas WHERE folio_venta = ? ORDER BY numero_item";
        try (PreparedStatement psDet = conn.prepareStatement(sqlDetalles)) {
            psDet.setString(1, folio);
            try (ResultSet rsDet = psDet.executeQuery()) {
                while (rsDet.next()) {
                    items.add(new ItemVenta(
                        rsDet.getInt("numero_item"),
                        rsDet.getString("codigo_producto"),
                        rsDet.getString("descripcion"),
                        rsDet.getDouble("precio_unitario"),
                        rsDet.getInt("cantidad"),
                        rsDet.getDouble("descuento")
                    ));
                }
            }
        }

        Venta v = new Venta(
            folio,
            rs.getString("cajero"),
            items,
            rs.getDouble("subtotal"),
            rs.getDouble("descuento"),
            rs.getDouble("iva"),
            rs.getDouble("total"),
            rs.getString("metodo_pago"),
            rs.getDouble("paga_con"),
            rs.getDouble("cambio")
        );
        Timestamp ts = rs.getTimestamp("fecha_hora");
        if (ts != null) {
            v.setFechaHora(ts.toLocalDateTime());
        }
        return v;
    }

    private Devolucion mapearDevolucionConDetalles(Connection conn, ResultSet rs) throws SQLException {
        String folioDev = rs.getString("folio_devolucion");
        List<ItemDevolucion> items = new ArrayList<>();
        String sqlDetalles = "SELECT codigo_producto, descripcion, precio, cantidad_comprada, cantidad_devuelta FROM detalle_devoluciones WHERE folio_devolucion = ?";
        try (PreparedStatement psDet = conn.prepareStatement(sqlDetalles)) {
            psDet.setString(1, folioDev);
            try (ResultSet rsDet = psDet.executeQuery()) {
                while (rsDet.next()) {
                    items.add(new ItemDevolucion(
                        rsDet.getString("codigo_producto"),
                        rsDet.getString("descripcion"),
                        rsDet.getDouble("precio"),
                        rsDet.getInt("cantidad_comprada"),
                        rsDet.getInt("cantidad_devuelta")
                    ));
                }
            }
        }

        Devolucion dev = new Devolucion(
            folioDev,
            rs.getString("folio_venta_original"),
            rs.getString("motivo"),
            rs.getBoolean("reintegrar_stock"),
            rs.getDouble("total_reembolso"),
            items
        );
        Timestamp ts = rs.getTimestamp("fecha_hora");
        if (ts != null) {
            dev.setFechaHora(ts.toLocalDateTime());
        }
        return dev;
    }
}
