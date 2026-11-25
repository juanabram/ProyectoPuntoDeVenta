package application.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Fila de articulo en la tabla de devoluciones
public class ItemDevolucion {

    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty descripcion = new SimpleStringProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();
    private final IntegerProperty cantComprada = new SimpleIntegerProperty();
    private final IntegerProperty cantDevolver = new SimpleIntegerProperty();
    private final DoubleProperty subtotalReembolso = new SimpleDoubleProperty();

    public ItemDevolucion(String codigo, String descripcion, double precio, int cantComprada, int cantDevolver) {
        this.codigo.set(codigo);
        this.descripcion.set(descripcion);
        this.precio.set(precio);
        this.cantComprada.set(cantComprada);
        this.cantDevolver.set(cantDevolver);
        recalcularReembolso();
    }

    public void recalcularReembolso() {
        this.subtotalReembolso.set(getPrecio() * getCantDevolver());
    }

    public StringProperty codigoProperty() { return codigo; }
    public StringProperty descripcionProperty() { return descripcion; }
    public DoubleProperty precioProperty() { return precio; }
    public IntegerProperty cantCompradaProperty() { return cantComprada; }
    public IntegerProperty cantDevolverProperty() { return cantDevolver; }
    public DoubleProperty subtotalReembolsoProperty() { return subtotalReembolso; }

    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String val) { this.codigo.set(val); }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String val) { this.descripcion.set(val); }

    public double getPrecio() { return precio.get(); }
    public void setPrecio(double val) { 
        this.precio.set(val); 
        recalcularReembolso();
    }

    public int getCantComprada() { return cantComprada.get(); }
    public void setCantComprada(int val) { this.cantComprada.set(val); }

    public int getCantDevolver() { return cantDevolver.get(); }
    public void setCantDevolver(int val) { 
        this.cantDevolver.set(val); 
        recalcularReembolso();
    }

    public double getSubtotalReembolso() { return subtotalReembolso.get(); }
}
