package application.modelo.datos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import application.modelo.Categoria;
import application.modelo.Devolucion;
import application.modelo.ItemDevolucion;
import application.modelo.ItemVenta;
import application.modelo.MovimientoInventario;
import application.modelo.Producto;
import application.modelo.Venta;

// Acceso centralizado a datos sobre PuntoDeVenta.accdb con cache en memoria
public class RepositorioDatos {

    public static final String ARCHIVO_BD = "PuntoDeVenta.accdb";
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATO_MOVIMIENTO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static RepositorioDatos instancia;

    private Conexion_Access conexionActiva;
    private BaseDatos bdActiva;

    private final Map<String, Producto> productosMap = new LinkedHashMap<>();
    private final Map<String, Venta> ventasMap = new LinkedHashMap<>();
    private final Map<String, Devolucion> devolucionesMap = new LinkedHashMap<>();
    private final List<Categoria> categoriasList = new ArrayList<>();
    private final List<MovimientoInventario> movimientosList = new ArrayList<>();
    private int contadorFolioVenta = 1001;
    private int contadorFolioDevolucion = 501;

    public static synchronized RepositorioDatos getInstancia() {
        if (instancia == null) instancia = new RepositorioDatos();
        return instancia;
    }

    private RepositorioDatos() {
        cargarDatos();
    }

    // Reutiliza una sola conexion abierta a Access durante la sesion
    private synchronized BaseDatos obtenerBaseDatos() {
        if (conexionActiva == null || !conexionActiva.estaActiva()) {
            conexionActiva = new Conexion_Access(ARCHIVO_BD);
            bdActiva = new BaseDatos(conexionActiva.getConexion());
        }
        return bdActiva;
    }

    public synchronized void cerrarConexion() {
        if (conexionActiva != null) {
            conexionActiva.cerrarConexion();
            conexionActiva = null;
            bdActiva = null;
        }
    }

    public static String texto(String valor) {
        return "'" + (valor == null ? "" : valor.replace("'", "''")) + "'";
    }

    public static String importe(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    public static String fecha(LocalDateTime fechaHora) {
        return (fechaHora != null ? fechaHora : LocalDateTime.now()).format(FORMATO_FECHA);
    }

    // Carga inicial desde PuntoDeVenta.accdb hacia memoria
    private void cargarDatos() {
        BaseDatos bd = obtenerBaseDatos();

        ArrayList<String[]> datosCategorias = bd.consultar("Categorias", "Id, Nombre", "");
        ArrayList<String[]> datosProductos = bd.consultar("Productos",
            "Codigo, Nombre, Categoria, Subcategoria, Precio, Stock, StockMinimo, Estado, Imagen", "");
        ArrayList<String[]> datosVentas = bd.consultar("Ventas",
            "Folio, FechaHora, Cajero, Subtotal, Descuento, Iva, Total, MetodoPago, PagaCon, Cambio", "");
        ArrayList<String[]> datosDetalleVentas = bd.consultar("DetalleVentas ORDER BY Folio, NumeroItem",
            "Folio, NumeroItem, Codigo, Descripcion, Precio, Cantidad, Descuento", "");
        ArrayList<String[]> datosDevoluciones = bd.consultar("Devoluciones",
            "FolioDevolucion, FolioVenta, FechaHora, Motivo, ReintegrarStock, TotalReembolso", "");
        ArrayList<String[]> datosDetalleDevoluciones = bd.consultar("DetalleDevoluciones ORDER BY Id",
            "FolioDevolucion, Codigo, Descripcion, Precio, CantidadComprada, CantidadDevuelta", "");
        ArrayList<String[]> datosMovimientos = bd.consultar("MovimientosInventario ORDER BY Id",
            "Codigo, Producto, ExistenciasResultantes, Motivo, FechaHora", "");

        try {
            for (String[] d : datosCategorias) {
                categoriasList.add(new Categoria(entero(d[0]), d[1]));
            }

            for (String[] d : datosProductos) {
                Producto p = new Producto(d[0], d[1], d[2], d[3], decimal(d[4]), entero(d[5]), entero(d[6]), d[7]);
                if (d[8] != null && !d[8].isEmpty()) p.setImagen(d[8]);
                productosMap.put(p.getCodigo(), p);
            }

            Map<String, List<ItemVenta>> itemsPorVenta = new HashMap<>();
            for (String[] d : datosDetalleVentas) {
                itemsPorVenta.computeIfAbsent(d[0], k -> new ArrayList<>())
                    .add(new ItemVenta(entero(d[1]), d[2], d[3], decimal(d[4]), entero(d[5]), decimal(d[6])));
            }
            for (String[] d : datosVentas) {
                Venta v = new Venta();
                v.setFolio(d[0]);
                v.setFechaHora(leerFecha(d[1]));
                v.setCajero(d[2]);
                v.setSubtotal(decimal(d[3]));
                v.setDescuentoTotal(decimal(d[4]));
                v.setIva(decimal(d[5]));
                v.setTotal(decimal(d[6]));
                v.setMetodoPago(d[7]);
                v.setPagaCon(decimal(d[8]));
                v.setCambio(decimal(d[9]));
                v.setItems(itemsPorVenta.getOrDefault(d[0], new ArrayList<>()));
                ventasMap.put(v.getFolio(), v);
                contadorFolioVenta = Math.max(contadorFolioVenta, numeroFolio(d[0], "TKT-") + 1);
            }

            Map<String, List<ItemDevolucion>> itemsPorDevolucion = new HashMap<>();
            for (String[] d : datosDetalleDevoluciones) {
                itemsPorDevolucion.computeIfAbsent(d[0], k -> new ArrayList<>())
                    .add(new ItemDevolucion(d[1], d[2], decimal(d[3]), entero(d[4]), entero(d[5])));
            }
            for (String[] d : datosDevoluciones) {
                Devolucion dev = new Devolucion();
                dev.setFolioDevolucion(d[0]);
                dev.setFolioVentaOriginal(d[1]);
                dev.setFechaHora(leerFecha(d[2]));
                dev.setMotivo(d[3]);
                dev.setReintegrarInventario("Si".equalsIgnoreCase(d[4]) || "true".equalsIgnoreCase(d[4]));
                dev.setTotalReembolso(decimal(d[5]));
                dev.setItems(itemsPorDevolucion.getOrDefault(d[0], new ArrayList<>()));
                devolucionesMap.put(dev.getFolioDevolucion(), dev);
                contadorFolioDevolucion = Math.max(contadorFolioDevolucion, numeroFolio(d[0], "DEV-") + 1);
            }

            for (String[] d : datosMovimientos) {
                Producto p = productosMap.get(d[0]);
                MovimientoInventario m = new MovimientoInventario(
                    d[0], d[1],
                    p != null ? p.getCategoria() : "",
                    entero(d[2]),
                    p != null ? p.getStockMinimo() : 0,
                    d[3]);
                m.setUltimoMovimiento(d[3] + " (" + leerFecha(d[4]).format(FORMATO_MOVIMIENTO) + ")");
                movimientosList.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (categoriasList.isEmpty()) inicializarCategorias();
        if (productosMap.isEmpty()) inicializarProductosSemilla();
    }

    private void inicializarCategorias() {
        String[] cats = {
            "Bebidas", "Lácteos", "Botanas", "Panadería", "Enlatados", "Limpieza", "Higiene Personal",
            "Farmacia", "Dulcería", "Carnes Frías", "Frutas y Verduras", "Mascotas", "Papelería",
            "Abarrotes", "Cuidado del Hogar", "Licores y Cervezas", "Congelados", "Cuidado de Bebé",
            "Despensa General", "Snacks y Confitería"
        };
        for (int i = 0; i < cats.length; i++) categoriasList.add(new Categoria(i + 1, cats[i]));
    }

    private void inicializarProductosSemilla() {
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
        return new ArrayList<>(productosMap.values());
    }

    public synchronized List<Producto> listarProductosActivos() {
        return productosMap.values().stream()
            .filter(p -> "Activo".equalsIgnoreCase(p.getEstado()))
            .collect(Collectors.toList());
    }

    public synchronized List<Producto> listarProductosCriticos() {
        return productosMap.values().stream()
            .filter(p -> p.isBajoStock() && "Activo".equalsIgnoreCase(p.getEstado()))
            .collect(Collectors.toList());
    }

    public synchronized Producto buscarProductoPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return null;
        return productosMap.get(codigo.trim());
    }

    public synchronized List<Producto> buscarProductosPorCriterio(String texto, String categoria) {
        String txt = (texto != null) ? texto.trim().toLowerCase() : "";
        boolean filtrarCat = categoria != null && !categoria.isEmpty() && !categoria.equalsIgnoreCase("Todas las categorías");
        return listarTodosProductos().stream()
            .filter(p -> txt.isEmpty() || p.getCodigo().toLowerCase().contains(txt) || p.getNombre().toLowerCase().contains(txt))
            .filter(p -> !filtrarCat || (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria)))
            .collect(Collectors.toList());
    }

    public synchronized boolean insertarProducto(Producto p) {
        if (p == null) return false;
        productosMap.put(p.getCodigo(), p);

        BaseDatos bd = obtenerBaseDatos();
        if (bd.existe("Productos", "Codigo = " + texto(p.getCodigo()))) {
            return actualizarProducto(p);
        }
        bd.insertar("Productos",
            "Codigo, Nombre, Categoria, Subcategoria, Precio, Stock, StockMinimo, Estado, Imagen, FechaRegistro",
            new String[]{
                p.getCodigo(), p.getNombre(), p.getCategoria(), p.getSubcategoria(),
                importe(p.getPrecio()), String.valueOf(p.getStock()), String.valueOf(p.getStockMinimo()),
                p.getEstado(), p.getImagen() != null ? p.getImagen() : "default.png", fecha(LocalDateTime.now())
            });
        return true;
    }

    public synchronized boolean actualizarProducto(Producto p) {
        if (p == null) return false;
        boolean enMemoria = productosMap.containsKey(p.getCodigo());
        if (enMemoria) productosMap.put(p.getCodigo(), p);

        obtenerBaseDatos().modificarVarios("Productos",
            new String[]{ "Nombre", "Categoria", "Subcategoria", "Precio", "Stock", "StockMinimo", "Estado" },
            new String[]{
                p.getNombre(), p.getCategoria(), p.getSubcategoria(), importe(p.getPrecio()),
                String.valueOf(p.getStock()), String.valueOf(p.getStockMinimo()), p.getEstado()
            },
            "Codigo = " + texto(p.getCodigo()));
        return enMemoria;
    }

    public synchronized boolean actualizarStock(String codigo, int nuevoStock) {
        if (codigo == null) return false;
        String clave = codigo.trim();
        Producto p = productosMap.get(clave);
        if (p != null) p.setStock(nuevoStock);

        obtenerBaseDatos().modificar("Productos", "Stock", String.valueOf(nuevoStock), "Codigo = " + texto(clave));
        return p != null;
    }

    public synchronized boolean bajaLogicaProducto(String codigo) {
        if (codigo == null) return false;
        String clave = codigo.trim();
        Producto p = productosMap.get(clave);
        if (p != null) p.setEstado("Inactivo");

        obtenerBaseDatos().modificar("Productos", "Estado", "Inactivo", "Codigo = " + texto(clave));
        return p != null;
    }

    // Ventas

    public synchronized String siguienteFolioVenta() {
        return "TKT-" + (contadorFolioVenta++);
    }

    public synchronized boolean registrarVenta(Venta v) {
        if (v == null) return false;
        Map<String, Integer> stockPrevio = new HashMap<>();
        for (ItemVenta item : v.getItems()) {
            Producto p = productosMap.get(item.getCodigo());
            if (p != null && !stockPrevio.containsKey(p.getCodigo())) {
                stockPrevio.put(p.getCodigo(), p.getStock());
            }
        }

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

        String fechaVenta = fecha(v.getFechaHora());
        BaseDatos bd = obtenerBaseDatos();
        bd.iniciarTransaccion();
        try {
            bd.insertar("Ventas", "Folio, FechaHora, Cajero, Subtotal, Descuento, Iva, Total, MetodoPago, PagaCon, Cambio",
                new String[]{
                    v.getFolio(), fechaVenta, v.getCajero(),
                    importe(v.getSubtotal()), importe(v.getDescuentoTotal()), importe(v.getIva()),
                    importe(v.getTotal()), v.getMetodoPago(), importe(v.getPagaCon()), importe(v.getCambio())
                });

            for (ItemVenta item : v.getItems()) {
                bd.insertar("DetalleVentas", "Folio, NumeroItem, Codigo, Descripcion, Precio, Cantidad, Descuento, Importe",
                    new String[]{
                        v.getFolio(), String.valueOf(item.getNumero()), item.getCodigo(), item.getDescripcion(),
                        importe(item.getPrecio()), String.valueOf(item.getCantidad()),
                        importe(item.getDescuento()), importe(item.getImporte())
                    });

                Producto p = productosMap.get(item.getCodigo());
                if (p != null) {
                    int previo = stockPrevio.getOrDefault(p.getCodigo(), p.getStock() + item.getCantidad());
                    int resultante = Math.max(0, previo - item.getCantidad());
                    stockPrevio.put(p.getCodigo(), resultante);

                    bd.modificar("Productos", "Stock", String.valueOf(p.getStock()), "Codigo = " + texto(p.getCodigo()));
                    bd.insertar("MovimientosInventario",
                        "Codigo, Producto, Tipo, Cantidad, ExistenciasPrevias, ExistenciasResultantes, Motivo, FechaHora",
                        new String[]{
                            p.getCodigo(), p.getNombre(), "Salida", String.valueOf(item.getCantidad()),
                            String.valueOf(previo), String.valueOf(resultante),
                            "Salida por Venta (" + v.getFolio() + ")", fechaVenta
                        });
                }
            }
            bd.confirmarTransaccion();
        } catch (Exception e) {
            bd.revertirTransaccion();
            System.err.println("[ERROR] Error en transacción de venta: " + e.getMessage());
        }
        return true;
    }

    public synchronized Venta buscarVentaPorFolio(String folio) {
        if (folio == null || folio.trim().isEmpty()) return null;
        return ventasMap.get(folio.trim());
    }

    public synchronized List<Venta> listarVentas() {
        return new ArrayList<>(ventasMap.values());
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
        String tipo = "Ajuste";
        if (detalle.toLowerCase().startsWith("entrada")) tipo = "Entrada";
        else if (detalle.toLowerCase().startsWith("salida")) tipo = "Salida";

        obtenerBaseDatos().insertar("MovimientosInventario",
            "Codigo, Producto, Tipo, Cantidad, ExistenciasPrevias, ExistenciasResultantes, Motivo, FechaHora",
            new String[]{
                mov.getCodigo(), mov.getProducto(), tipo,
                String.valueOf(mov.getExistencias()), String.valueOf(mov.getExistencias()),
                String.valueOf(mov.getExistencias()), detalle, fecha(mov.getFechaHora())
            });
        return true;
    }

    public synchronized List<MovimientoInventario> listarExistencias() {
        Map<String, String> ultimos = new HashMap<>();
        for (MovimientoInventario m : movimientosList) {
            ultimos.put(m.getCodigo(), m.getUltimoMovimiento());
        }
        return listarTodosProductos().stream()
            .map(p -> {
                String ultimo = ultimos.getOrDefault(p.getCodigo(), "Consulta Existencias");
                MovimientoInventario item = new MovimientoInventario(p.getCodigo(), p.getNombre(), p.getCategoria(),
                    p.getStock(), p.getStockMinimo(), ultimo);
                if (ultimos.containsKey(p.getCodigo())) item.setUltimoMovimiento(ultimo);
                return item;
            })
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
        Map<String, Integer> stockPrevio = new HashMap<>();
        for (ItemDevolucion item : dev.getItems()) {
            Producto p = productosMap.get(item.getCodigo());
            if (p != null && !stockPrevio.containsKey(p.getCodigo())) {
                stockPrevio.put(p.getCodigo(), p.getStock());
            }
        }

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

        String fechaDev = fecha(dev.getFechaHora());
        BaseDatos bd = obtenerBaseDatos();
        bd.iniciarTransaccion();
        try {
            bd.insertar("Devoluciones", "FolioDevolucion, FolioVenta, FechaHora, Motivo, ReintegrarStock, TotalReembolso",
                new String[]{
                    dev.getFolioDevolucion(), dev.getFolioVentaOriginal(), fechaDev,
                    dev.getMotivo(), dev.isReintegrarInventario() ? "Si" : "No", importe(dev.getTotalReembolso())
                });

            for (ItemDevolucion item : dev.getItems()) {
                bd.insertar("DetalleDevoluciones",
                    "FolioDevolucion, Codigo, Descripcion, Precio, CantidadComprada, CantidadDevuelta, SubtotalReembolso",
                    new String[]{
                        dev.getFolioDevolucion(), item.getCodigo(), item.getDescripcion(), importe(item.getPrecio()),
                        String.valueOf(item.getCantComprada()), String.valueOf(item.getCantDevolver()),
                        importe(item.getSubtotalReembolso())
                    });

                Producto p = productosMap.get(item.getCodigo());
                if (dev.isReintegrarInventario() && p != null && item.getCantDevolver() > 0) {
                    int previo = stockPrevio.getOrDefault(p.getCodigo(), p.getStock() - item.getCantDevolver());
                    int resultante = previo + item.getCantDevolver();
                    stockPrevio.put(p.getCodigo(), resultante);

                    bd.modificar("Productos", "Stock", String.valueOf(p.getStock()), "Codigo = " + texto(p.getCodigo()));
                    bd.insertar("MovimientosInventario",
                        "Codigo, Producto, Tipo, Cantidad, ExistenciasPrevias, ExistenciasResultantes, Motivo, FechaHora",
                        new String[]{
                            p.getCodigo(), p.getNombre(), "Entrada", String.valueOf(item.getCantDevolver()),
                            String.valueOf(previo), String.valueOf(resultante),
                            "Entrada por Devolución (" + dev.getFolioDevolucion() + ")", fechaDev
                        });
                }
            }
            bd.confirmarTransaccion();
        } catch (Exception e) {
            bd.revertirTransaccion();
            System.err.println("[ERROR] Error en transacción de devolución: " + e.getMessage());
        }
        return true;
    }

    public synchronized Devolucion buscarDevolucionPorFolio(String folio) {
        if (folio == null || folio.trim().isEmpty()) return null;
        return devolucionesMap.get(folio.trim());
    }

    public synchronized List<Devolucion> listarDevoluciones() {
        return new ArrayList<>(devolucionesMap.values());
    }

    public synchronized List<Categoria> obtenerCategorias() {
        return new ArrayList<>(categoriasList);
    }

    // Conversiones seguras

    private static int entero(String valor) {
        if (valor == null || valor.trim().isEmpty()) return 0;
        try { return (int) Math.round(Double.parseDouble(valor.trim())); }
        catch (NumberFormatException e) { return 0; }
    }

    private static double decimal(String valor) {
        if (valor == null || valor.trim().isEmpty()) return 0.0;
        try { return Double.parseDouble(valor.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private static LocalDateTime leerFecha(String valor) {
        if (valor == null || valor.trim().isEmpty()) return LocalDateTime.now();
        try {
            String v = valor.trim();
            if (v.length() > 19) v = v.substring(0, 19);
            return LocalDateTime.parse(v, FORMATO_FECHA);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private static int numeroFolio(String folio, String prefijo) {
        if (folio == null || !folio.startsWith(prefijo)) return 0;
        try { return Integer.parseInt(folio.substring(prefijo.length()).trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
