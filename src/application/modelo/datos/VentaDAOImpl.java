package application.modelo.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import application.modelo.ItemVenta;
import application.modelo.Venta;

/**
 * Implementación de IVentaDAO:
 * Registra y consulta las transacciones comerciales de venta.
 */
public class VentaDAOImpl implements IVentaDAO {

    private final RepositorioDatos repo = RepositorioDatos.getInstancia();

    @Override
    public boolean registrarVenta(Venta v) {
        repo.registrarVenta(v);
        Connection conn = ConexionBD.obtenerConexion();
        if (conn != null) {
            String sqlVenta = "INSERT INTO ventas (folio, fecha_hora, cajero, subtotal, descuento, iva, total, metodo_pago, paga_con, cambio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlDetalle = "INSERT INTO detalle_ventas (folio_venta, numero_item, codigo_producto, descripcion, precio_unitario, cantidad, descuento, importe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta)) {
                    psVenta.setString(1, v.getFolio());
                    psVenta.setObject(2, v.getFechaHora());
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

                try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
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
                    }
                    psDetalle.executeBatch();
                }
                conn.commit();
                conn.setAutoCommit(true);
                return true;
            } catch (SQLException e) {
                try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
                System.err.println("[VentaDAO] Error registrando venta: " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    public Venta buscarPorFolio(String folio) {
        if (folio == null) return null;
        return repo.obtenerVenta(folio.trim());
    }

    @Override
    public List<Venta> listarVentas() {
        return repo.obtenerTodasLasVentas();
    }

    @Override
    public List<Venta> listarVentasPorFecha(LocalDate inicio, LocalDate fin) {
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

    @Override
    public String generarSiguienteFolio() {
        return repo.siguienteFolioVenta();
    }
}
