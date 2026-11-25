package application.modelo.servicio;

import java.util.ArrayList;
import java.util.List;
import application.modelo.datos.IDevolucionDAO;
import application.modelo.datos.IVentaDAO;
import application.modelo.datos.DevolucionDAOImpl;
import application.modelo.datos.VentaDAOImpl;
import application.modelo.Devolucion;
import application.modelo.ItemDevolucion;
import application.modelo.ItemVenta;
import application.modelo.Venta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Servicio de Lógica de Negocio: Gestión de Devoluciones y Reembolsos
 */
public class ServicioDevoluciones {

    private final IDevolucionDAO devolucionDAO = new DevolucionDAOImpl();
    private final IVentaDAO ventaDAO = new VentaDAOImpl();
    private final ObservableList<ItemDevolucion> itemsDevolucion = FXCollections.observableArrayList();

    public ObservableList<ItemDevolucion> getItemsDevolucion() {
        return itemsDevolucion;
    }

    public Venta buscarVenta(String folio) {
        itemsDevolucion.clear();
        if (folio == null || folio.trim().isEmpty()) return null;
        Venta v = ventaDAO.buscarPorFolio(folio.trim());
        if (v != null) {
            for (ItemVenta item : v.getItems()) {
                itemsDevolucion.add(new ItemDevolucion(
                    item.getCodigo(),
                    item.getDescripcion(),
                    item.getPrecio(),
                    item.getCantidad(),
                    0 // Cantidad a devolver inicial
                ));
            }
        }
        return v;
    }

    public double calcularTotalReembolso() {
        double total = 0.0;
        for (ItemDevolucion item : itemsDevolucion) {
            total += item.getSubtotalReembolso();
        }
        return total;
    }

    public Devolucion procesarDevolucion(String folioVenta, String motivo, boolean reintegrarStock) throws IllegalArgumentException {
        if (itemsDevolucion.isEmpty()) {
            throw new IllegalArgumentException("No hay ticket de venta cargado para procesar.");
        }

        List<ItemDevolucion> itemsEfectivos = new ArrayList<>();
        for (ItemDevolucion item : itemsDevolucion) {
            if (item.getCantDevolver() > 0) {
                if (item.getCantDevolver() > item.getCantComprada()) {
                    throw new IllegalArgumentException("La cantidad a devolver de '" + item.getDescripcion() + "' supera la comprada.");
                }
                itemsEfectivos.add(item);
            }
        }

        if (itemsEfectivos.isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos una unidad a devolver en algún producto.");
        }

        double total = calcularTotalReembolso();
        String folioDev = "DEV-" + (devolucionDAO.listarDevoluciones().size() + 101);

        Devolucion dev = new Devolucion(
            folioDev, folioVenta,
            motivo != null ? motivo : "Devolución en ventanilla",
            reintegrarStock, total, itemsEfectivos
        );

        devolucionDAO.registrarDevolucion(dev);
        itemsDevolucion.clear();
        return dev;
    }
}
