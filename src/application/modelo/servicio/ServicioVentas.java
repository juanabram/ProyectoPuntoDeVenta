package application.modelo.servicio;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import application.modelo.ItemVenta;
import application.modelo.Producto;
import application.modelo.Venta;
import application.modelo.datos.RepositorioDatos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Manejo del carrito, calculo de totales y cobro de ventas
public class ServicioVentas {

    private final RepositorioDatos repo = RepositorioDatos.getInstancia();

    private final ObservableList<ItemVenta> carrito = FXCollections.observableArrayList();
    private static final double TASA_IVA = 0.16;

    public ObservableList<ItemVenta> getCarrito() {
        return carrito;
    }

    public synchronized String agregarArticulo(String codigo, int cantidad) throws IllegalArgumentException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar un código de barras válido.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        Producto p = repo.buscarProductoPorCodigo(codigo.trim());
        if (p == null) {
            throw new IllegalArgumentException("El producto con código '" + codigo + "' no existe en el catálogo.");
        }
        if (!"Activo".equalsIgnoreCase(p.getEstado())) {
            throw new IllegalArgumentException("El producto '" + p.getNombre() + "' se encuentra dado de baja (Inactivo).");
        }

        // Verificar si ya existe en el carrito
        ItemVenta existente = null;
        for (ItemVenta item : carrito) {
            if (item.getCodigo().equalsIgnoreCase(p.getCodigo())) {
                existente = item;
                break;
            }
        }

        int cantidadTotal = cantidad;
        if (existente != null) {
            cantidadTotal += existente.getCantidad();
        }

        // Validación de existencias en inventario
        if (cantidadTotal > p.getStock()) {
            throw new IllegalArgumentException("Stock insuficiente para '" + p.getNombre() + "'. Existencias disponibles: " + p.getStock());
        }

        if (existente != null) {
            existente.setCantidad(cantidadTotal);
        } else {
            int num = carrito.size() + 1;
            ItemVenta nuevo = new ItemVenta(num, p.getCodigo(), p.getNombre(), p.getPrecio(), cantidad, 0.0);
            carrito.add(nuevo);
        }
        renumerarCarrito();
        return p.getNombre();
    }

    public synchronized void quitarArticulo(ItemVenta item) {
        if (item != null) {
            carrito.remove(item);
            renumerarCarrito();
        }
    }

    public synchronized void modificarCantidad(ItemVenta item, int nuevaCantidad) throws IllegalArgumentException {
        if (item == null) return;
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser de al menos 1 unidad.");
        }
        Producto p = repo.buscarProductoPorCodigo(item.getCodigo());
        if (p != null && nuevaCantidad > p.getStock()) {
            throw new IllegalArgumentException("Stock insuficiente para '" + p.getNombre() + "'. Existencias disponibles: " + p.getStock());
        }
        item.setCantidad(nuevaCantidad);
    }

    public synchronized void aplicarDescuento(ItemVenta item, double montoDescuento) throws IllegalArgumentException {
        if (item == null) return;
        if (montoDescuento < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo.");
        }
        double subtotalBruto = item.getPrecio() * item.getCantidad();
        if (montoDescuento > subtotalBruto) {
            throw new IllegalArgumentException("El descuento no puede exceder el importe total del producto ($" + subtotalBruto + ").");
        }
        item.setDescuento(montoDescuento);
    }

    public synchronized void vaciarCarrito() {
        carrito.clear();
    }

    private void renumerarCarrito() {
        for (int i = 0; i < carrito.size(); i++) {
            carrito.get(i).setNumero(i + 1);
        }
    }

    // Cálculos de Totales
    public int calcularTotalArticulos() {
        int total = 0;
        for (ItemVenta item : carrito) {
            total += item.getCantidad();
        }
        return total;
    }

    public double calcularSubtotal() {
        double sub = 0.0;
        for (ItemVenta item : carrito) {
            sub += (item.getPrecio() * item.getCantidad());
        }
        return sub;
    }

    public double calcularDescuentos() {
        double desc = 0.0;
        for (ItemVenta item : carrito) {
            desc += item.getDescuento();
        }
        return desc;
    }

    public double calcularIVA() {
        double baseGravable = Math.max(0.0, calcularSubtotal() - calcularDescuentos());
        return baseGravable * TASA_IVA;
    }

    public double calcularTotal() {
        double base = Math.max(0.0, calcularSubtotal() - calcularDescuentos());
        return base + calcularIVA();
    }

    // Registra la venta y limpia el carrito
    public synchronized Venta liquidarVenta(String metodoPago, double pagaCon, boolean imprimirTicket) throws IllegalArgumentException {
        if (carrito.isEmpty()) {
            throw new IllegalArgumentException("El carrito de venta está vacío.");
        }
        double total = calcularTotal();
        if ("Efectivo".equalsIgnoreCase(metodoPago) && pagaCon < total) {
            throw new IllegalArgumentException("El monto recibido ($" + pagaCon + ") es menor al total a pagar ($" + total + ").");
        }

        double cambio = "Efectivo".equalsIgnoreCase(metodoPago) ? (pagaCon - total) : 0.0;
        String folio = repo.siguienteFolioVenta();

        List<ItemVenta> copiaItems = new ArrayList<>(carrito);
        Venta venta = new Venta(
            folio, "Cajero Activo", copiaItems,
            calcularSubtotal(), calcularDescuentos(), calcularIVA(), total,
            metodoPago != null ? metodoPago : "Efectivo",
            pagaCon, cambio
        );

        repo.registrarVenta(venta);
        vaciarCarrito();
        return venta;
    }

    // Arma el ticket en texto plano
    public String generarTextoTicket(Venta v) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        sb.append("========================================\n");
        sb.append("         TIENDA DE CONVENIENCIA         \n");
        sb.append("          PUNTO DE VENTA UAT           \n");
        sb.append("        TAMPICO, TAMAULIPAS, MÉXICO     \n");
        sb.append("========================================\n");
        sb.append("Folio Ticket: ").append(v.getFolio()).append("\n");
        sb.append("Fecha/Hora  : ").append(v.getFechaHora().format(fmt)).append("\n");
        sb.append("Atendido por: ").append(v.getCajero()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-18s %4s %7s %8s\n", "PRODUCTO", "CANT", "PRECIO", "IMPORTE"));
        sb.append("----------------------------------------\n");
        for (ItemVenta item : v.getItems()) {
            String desc = item.getDescripcion();
            if (desc.length() > 18) desc = desc.substring(0, 18);
            sb.append(String.format("%-18s %4d %7.2f %8.2f\n",
                desc, item.getCantidad(), item.getPrecio(), item.getImporte()));
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("SUBTOTAL      : $%10.2f\n", v.getSubtotal()));
        sb.append(String.format("DESCUENTO     : $%10.2f\n", v.getDescuentoTotal()));
        sb.append(String.format("IVA (16%%)     : $%10.2f\n", v.getIva()));
        sb.append(String.format("TOTAL A PAGAR : $%10.2f\n", v.getTotal()));
        sb.append(String.format("MÉTODO DE PAGO: %s\n", v.getMetodoPago()));
        if ("Efectivo".equalsIgnoreCase(v.getMetodoPago())) {
            sb.append(String.format("PAGA CON      : $%10.2f\n", v.getPagaCon()));
            sb.append(String.format("SU CAMBIO     : $%10.2f\n", v.getCambio()));
        }
        sb.append("========================================\n");
        sb.append("    ¡GRACIAS POR SU COMPRA! VUELVA PRONTO \n");
        sb.append("========================================\n");
        return sb.toString();
    }
}
