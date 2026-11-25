package application.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de Entidad: Venta
 * Representa una transacción de venta finalizada o registrada en el sistema.
 */
public class Venta {

    private String folio;
    private LocalDateTime fechaHora;
    private String cajero;
    private List<ItemVenta> items;
    private double subtotal;
    private double descuentoTotal;
    private double iva;
    private double total;
    private String metodoPago;
    private double pagaCon;
    private double cambio;

    public Venta() {
        this.items = new ArrayList<>();
        this.fechaHora = LocalDateTime.now();
    }

    public Venta(String folio, String cajero, List<ItemVenta> items,
                 double subtotal, double descuentoTotal, double iva, double total,
                 String metodoPago, double pagaCon, double cambio) {
        this.folio = folio;
        this.fechaHora = LocalDateTime.now();
        this.cajero = cajero;
        this.items = new ArrayList<>(items);
        this.subtotal = subtotal;
        this.descuentoTotal = descuentoTotal;
        this.iva = iva;
        this.total = total;
        this.metodoPago = metodoPago;
        this.pagaCon = pagaCon;
        this.cambio = cambio;
    }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getCajero() { return cajero; }
    public void setCajero(String cajero) { this.cajero = cajero; }

    public List<ItemVenta> getItems() { return items; }
    public void setItems(List<ItemVenta> items) { this.items = items; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDescuentoTotal() { return descuentoTotal; }
    public void setDescuentoTotal(double descuentoTotal) { this.descuentoTotal = descuentoTotal; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public double getPagaCon() { return pagaCon; }
    public void setPagaCon(double pagaCon) { this.pagaCon = pagaCon; }

    public double getCambio() { return cambio; }
    public void setCambio(double cambio) { this.cambio = cambio; }
}
