package application.modelo.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import application.modelo.datos.IInventarioDAO;
import application.modelo.datos.IProductoDAO;
import application.modelo.datos.IVentaDAO;
import application.modelo.datos.InventarioDAOImpl;
import application.modelo.datos.ProductoDAOImpl;
import application.modelo.datos.VentaDAOImpl;
import application.modelo.ItemVenta;
import application.modelo.MovimientoInventario;
import application.modelo.Producto;
import application.modelo.Venta;

/**
 * Servicio de Lógica de Negocio: Generación de Reportes del Sistema
 * Preparado para emitir datos formateados y exportación.
 */
public class ServicioReportes {

    private final IProductoDAO productoDAO = new ProductoDAOImpl();
    private final IVentaDAO ventaDAO = new VentaDAOImpl();
    private final IInventarioDAO inventarioDAO = new InventarioDAOImpl();

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String generarVistaPrevia(String tipoReporte, LocalDate fechaInicio, LocalDate fechaFin, String categoria) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================\n");
        sb.append("                  REPORTE OFICIAL DEL SISTEMA POS                       \n");
        sb.append("                     TIENDA DE CONVENIENCIA                              \n");
        sb.append("=========================================================================\n");
        sb.append("Tipo de Reporte  : ").append(tipoReporte).append("\n");
        sb.append("Fecha de Emisión : ").append(LocalDateTime.now().format(FMT_FECHA)).append("\n");
        sb.append("Filtro Periodo   : ").append(fechaInicio != null ? fechaInicio : "Histórico").append(" al ").append(fechaFin != null ? fechaFin : "Actual").append("\n");
        sb.append("Filtro Categoría : ").append(categoria != null ? categoria : "Todas").append("\n");
        sb.append("Conexión Datos   : Capa DAO Activa (Preparada para Base de Datos Relacional)\n");
        sb.append("-------------------------------------------------------------------------\n\n");

        if (tipoReporte.contains("1. Inventario General")) {
            List<Producto> lista = productoDAO.listarTodos();
            sb.append(String.format("%-15s %-30s %-15s %8s %8s %10s\n", "CÓDIGO", "NOMBRE", "CATEGORÍA", "PRECIO", "STOCK", "ESTADO"));
            sb.append("-----------------------------------------------------------------------------------------\n");
            for (Producto p : lista) {
                String nom = p.getNombre().length() > 30 ? p.getNombre().substring(0, 30) : p.getNombre();
                sb.append(String.format("%-15s %-30s %-15s %8.2f %8d %10s\n",
                    p.getCodigo(), nom, p.getCategoria(), p.getPrecio(), p.getStock(), p.getEstado()));
            }
            sb.append("-----------------------------------------------------------------------------------------\n");
            sb.append("Total de Productos en Catálogo: ").append(lista.size()).append("\n");

        } else if (tipoReporte.contains("3. Productos con Bajo Stock")) {
            List<Producto> criticos = productoDAO.listarCriticos();
            sb.append(String.format("%-15s %-32s %10s %10s %12s\n", "CÓDIGO", "PRODUCTO", "EXISTENCIAS", "MÍNIMO", "DÉFICIT"));
            sb.append("-----------------------------------------------------------------------------------------\n");
            for (Producto p : criticos) {
                int def = Math.max(0, p.getStockMinimo() - p.getStock());
                String nom = p.getNombre().length() > 32 ? p.getNombre().substring(0, 32) : p.getNombre();
                sb.append(String.format("%-15s %-32s %10d %10d %12d\n",
                    p.getCodigo(), nom, p.getStock(), p.getStockMinimo(), def));
            }
            sb.append("-----------------------------------------------------------------------------------------\n");
            sb.append("Total de Productos en Alerta Crítica: ").append(criticos.size()).append("\n");

        } else if (tipoReporte.contains("4. Ventas Diarias") || tipoReporte.contains("5. Ventas por Periodo")) {
            List<Venta> ventas = ventaDAO.listarVentasPorFecha(fechaInicio, fechaFin);
            sb.append(String.format("%-12s %-18s %-15s %10s %10s %12s\n", "FOLIO", "FECHA/HORA", "CAJERO", "SUBTOTAL", "IVA", "TOTAL"));
            sb.append("-----------------------------------------------------------------------------------------\n");
            double totalRecaudado = 0.0;
            for (Venta v : ventas) {
                sb.append(String.format("%-12s %-18s %-15s %10.2f %10.2f %12.2f\n",
                    v.getFolio(), v.getFechaHora().format(FMT_FECHA), v.getCajero(), v.getSubtotal(), v.getIva(), v.getTotal()));
                totalRecaudado += v.getTotal();
            }
            sb.append("-----------------------------------------------------------------------------------------\n");
            sb.append(String.format("Número de Ventas: %d | Total Recaudado: $%.2f\n", ventas.size(), totalRecaudado));

        } else {
            sb.append("Reporte estructurado y listo para compilación con JasperReports (.jrxml).\n");
            sb.append("Parámetros JDBC configurados en ConexionBD.\n");
            sb.append("Los datos se extraerán automáticamente de las tablas relacionales del sistema.\n");
        }

        sb.append("\n=========================================================================\n");
        sb.append("                       FIN DEL REPORTE                                   \n");
        sb.append("=========================================================================\n");
        return sb.toString();
    }
}
