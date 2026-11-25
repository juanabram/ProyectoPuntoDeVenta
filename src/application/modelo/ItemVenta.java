package application.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Fila de producto en el carrito de venta
public class ItemVenta {

    private final IntegerProperty numero = new SimpleIntegerProperty();
    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty descripcion = new SimpleStringProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();
    private final DoubleProperty descuento = new SimpleDoubleProperty(0.0);
    private final DoubleProperty importe = new SimpleDoubleProperty();

    public ItemVenta(int numero, String codigo, String descripcion, double precio, int cantidad, double descuento) {
        this.numero.set(numero);
        this.codigo.set(codigo);
        this.descripcion.set(descripcion);
        this.precio.set(precio);
        this.cantidad.set(cantidad);
        this.descuento.set(descuento);
        recalcularImporte();
    }

    public void recalcularImporte() {
        double sub = getPrecio() * getCantidad();
        double desc = getDescuento();
        double total = Math.max(0.0, sub - desc);
        this.importe.set(total);
    }

    // Property getters
    public IntegerProperty numeroProperty() { return numero; }
    public StringProperty codigoProperty() { return codigo; }
    public StringProperty descripcionProperty() { return descripcion; }
    public DoubleProperty precioProperty() { return precio; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public DoubleProperty descuentoProperty() { return descuento; }
    public DoubleProperty importeProperty() { return importe; }

    // Standard getters y setters
    public int getNumero() { return numero.get(); }
    public void setNumero(int valor) { this.numero.set(valor); }

    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String valor) { this.codigo.set(valor); }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String valor) { this.descripcion.set(valor); }

    public double getPrecio() { return precio.get(); }
    public void setPrecio(double valor) { 
        this.precio.set(valor); 
        recalcularImporte();
    }

    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int valor) { 
        this.cantidad.set(valor); 
        recalcularImporte();
    }

    public double getDescuento() { return descuento.get(); }
    public void setDescuento(double valor) { 
        this.descuento.set(valor); 
        recalcularImporte();
    }

    public double getImporte() { return importe.get(); }
}
