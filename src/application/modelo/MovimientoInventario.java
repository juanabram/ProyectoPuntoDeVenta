package application.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modelo de Entidad: MovimientoInventario
 * Registra entradas, salidas y ajustes de existencias en el almacén.
 */
public class MovimientoInventario {

    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty producto = new SimpleStringProperty();
    private final StringProperty categoria = new SimpleStringProperty();
    private final IntegerProperty existencias = new SimpleIntegerProperty();
    private final IntegerProperty stockMinimo = new SimpleIntegerProperty();
    private final StringProperty estadoAlerta = new SimpleStringProperty();
    private final StringProperty ultimoMovimiento = new SimpleStringProperty();

    private LocalDateTime fechaHora;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MovimientoInventario(String codigo, String producto, String categoria,
                                int existencias, int stockMinimo, String tipoMovimiento) {
        this.codigo.set(codigo);
        this.producto.set(producto);
        this.categoria.set(categoria);
        this.existencias.set(existencias);
        this.stockMinimo.set(stockMinimo);
        this.fechaHora = LocalDateTime.now();
        actualizarAlerta();
        this.ultimoMovimiento.set(tipoMovimiento + " (" + fechaHora.format(FORMATO) + ")");
    }

    public void actualizarAlerta() {
        if (getExistencias() <= 0) {
            this.estadoAlerta.set("SIN STOCK");
        } else if (getExistencias() <= getStockMinimo()) {
            this.estadoAlerta.set("CRÍTICO (" + getExistencias() + ")");
        } else {
            this.estadoAlerta.set("NORMAL");
        }
    }

    // Property getters
    public StringProperty codigoProperty() { return codigo; }
    public StringProperty productoProperty() { return producto; }
    public StringProperty categoriaProperty() { return categoria; }
    public IntegerProperty existenciasProperty() { return existencias; }
    public IntegerProperty stockMinimoProperty() { return stockMinimo; }
    public StringProperty estadoAlertaProperty() { return estadoAlerta; }
    public StringProperty ultimoMovimientoProperty() { return ultimoMovimiento; }

    // Getters y setters
    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String val) { this.codigo.set(val); }

    public String getProducto() { return producto.get(); }
    public void setProducto(String val) { this.producto.set(val); }

    public String getCategoria() { return categoria.get(); }
    public void setCategoria(String val) { this.categoria.set(val); }

    public int getExistencias() { return existencias.get(); }
    public void setExistencias(int val) { 
        this.existencias.set(val); 
        actualizarAlerta();
    }

    public int getStockMinimo() { return stockMinimo.get(); }
    public void setStockMinimo(int val) { 
        this.stockMinimo.set(val); 
        actualizarAlerta();
    }

    public String getEstadoAlerta() { return estadoAlerta.get(); }
    public void setEstadoAlerta(String val) { this.estadoAlerta.set(val); }

    public String getUltimoMovimiento() { return ultimoMovimiento.get(); }
    public void setUltimoMovimiento(String val) { this.ultimoMovimiento.set(val); }

    public LocalDateTime getFechaHora() { return fechaHora; }
}
