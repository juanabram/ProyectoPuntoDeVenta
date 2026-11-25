package application.modelo.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import application.modelo.Producto;

/**
 * Implementación de IProductoDAO:
 * Equipada con sentencias JDBC preparadas para conectarse a la Base de Datos Relacional,
 * y con sincronización con el Repositorio de Datos.
 */
public class ProductoDAOImpl implements IProductoDAO {

    private final RepositorioDatos repo = RepositorioDatos.getInstancia();

    @Override
    public List<Producto> listarTodos() {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT codigo, nombre, categoria, subcategoria, precio, stock, stock_minimo, estado FROM productos";
            List<Producto> lista = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Producto(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getString("subcategoria"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getInt("stock_minimo"),
                        rs.getString("estado")
                    ));
                }
                return lista;
            } catch (SQLException e) {
                System.err.println("[ProductoDAO] Error en consulta SQL: " + e.getMessage());
            }
        }
        return repo.obtenerTodosLosProductos();
    }

    @Override
    public List<Producto> listarActivos() {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT codigo, nombre, categoria, subcategoria, precio, stock, stock_minimo, estado FROM productos WHERE estado = 'Activo'";
            List<Producto> lista = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Producto(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getString("subcategoria"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getInt("stock_minimo"),
                        rs.getString("estado")
                    ));
                }
                return lista;
            } catch (SQLException e) {
                System.err.println("[ProductoDAO] Error en consulta SQL: " + e.getMessage());
            }
        }
        return repo.obtenerProductosActivos();
    }

    @Override
    public List<Producto> listarCriticos() {
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT codigo, nombre, categoria, subcategoria, precio, stock, stock_minimo, estado FROM productos WHERE stock <= stock_minimo AND estado = 'Activo'";
            List<Producto> lista = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Producto(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getString("subcategoria"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getInt("stock_minimo"),
                        rs.getString("estado")
                    ));
                }
                return lista;
            } catch (SQLException e) {
                System.err.println("[ProductoDAO] Error en consulta SQL: " + e.getMessage());
            }
        }
        return repo.obtenerProductosCriticos();
    }

    @Override
    public Producto buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return null;
        codigo = codigo.trim();
        
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "SELECT codigo, nombre, categoria, subcategoria, precio, stock, stock_minimo, estado FROM productos WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
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
                }
            } catch (SQLException e) {
                System.err.println("[ProductoDAO] Error buscando código: " + e.getMessage());
            }
        }
        return repo.obtenerProducto(codigo);
    }

    @Override
    public List<Producto> buscarPorCriterio(String texto, String categoria) {
        List<Producto> todos = listarTodos();
        List<Producto> filtrados = new ArrayList<>();
        String txt = (texto != null) ? texto.trim().toLowerCase() : "";
        boolean filtrarCat = (categoria != null && !categoria.isEmpty() && !categoria.equalsIgnoreCase("Todas las categorías"));

        for (Producto p : todos) {
            boolean coincideTexto = txt.isEmpty() ||
                p.getCodigo().toLowerCase().contains(txt) ||
                p.getNombre().toLowerCase().contains(txt);
            boolean coincideCat = !filtrarCat || p.getCategoria().equalsIgnoreCase(categoria);

            if (coincideTexto && coincideCat) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    @Override
    public boolean insertar(Producto p) {
        repo.agregarProducto(p);
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "INSERT INTO productos (codigo, nombre, categoria, subcategoria, precio, stock, stock_minimo, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getCodigo());
                ps.setString(2, p.getNombre());
                ps.setString(3, p.getCategoria());
                ps.setString(4, p.getSubcategoria());
                ps.setDouble(5, p.getPrecio());
                ps.setInt(6, p.getStock());
                ps.setInt(7, p.getStockMinimo());
                ps.setString(8, p.getEstado());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[ProductoDAO] Error al insertar en BD: " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean actualizar(Producto p) {
        repo.actualizarProducto(p);
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "UPDATE productos SET nombre = ?, categoria = ?, subcategoria = ?, precio = ?, stock = ?, stock_minimo = ?, estado = ? WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getCategoria());
                ps.setString(3, p.getSubcategoria());
                ps.setDouble(4, p.getPrecio());
                ps.setInt(5, p.getStock());
                ps.setInt(6, p.getStockMinimo());
                ps.setString(7, p.getEstado());
                ps.setString(8, p.getCodigo());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[ProductoDAO] Error al actualizar en BD: " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean bajaLogica(String codigo) {
        repo.bajaLogicaProducto(codigo);
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "UPDATE productos SET estado = 'Inactivo' WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigo);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[ProductoDAO] Error baja lógica: " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean actualizarStock(String codigo, int nuevoStock) {
        repo.actualizarStockProducto(codigo, nuevoStock);
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sql = "UPDATE productos SET stock = ? WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, nuevoStock);
                ps.setString(2, codigo);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("[ProductoDAO] Error actualizando stock: " + e.getMessage());
            }
        }
        return true;
    }
}
