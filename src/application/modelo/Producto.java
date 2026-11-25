package application.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Articulo del catalogo del punto de venta
public class Producto {

    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty categoria = new SimpleStringProperty();
    private final StringProperty subcategoria = new SimpleStringProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();
    private final IntegerProperty stock = new SimpleIntegerProperty();
    private final IntegerProperty stockMinimo = new SimpleIntegerProperty();
    private final StringProperty estado = new SimpleStringProperty("Activo");
    private final StringProperty imagen = new SimpleStringProperty("default.png");

    public Producto() {
    }

    public Producto(String codigo, String nombre, String categoria, String subcategoria,
                    double precio, int stock, int stockMinimo, String estado) {
        this.codigo.set(codigo);
        this.nombre.set(nombre);
        this.categoria.set(categoria);
        this.subcategoria.set(subcategoria);
        this.precio.set(precio);
        this.stock.set(stock);
        this.stockMinimo.set(stockMinimo);
        this.estado.set(estado != null ? estado : "Activo");
    }

    // Property getters
    public StringProperty codigoProperty() { return codigo; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty categoriaProperty() { return categoria; }
    public StringProperty subcategoriaProperty() { return subcategoria; }
    public DoubleProperty precioProperty() { return precio; }
    public IntegerProperty stockProperty() { return stock; }
    public IntegerProperty stockMinimoProperty() { return stockMinimo; }
    public StringProperty estadoProperty() { return estado; }
    public StringProperty imagenProperty() { return imagen; }

    // Standard getters y setters
    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String valor) { this.codigo.set(valor); }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String valor) { this.nombre.set(valor); }

    public String getCategoria() { return categoria.get(); }
    public void setCategoria(String valor) { this.categoria.set(valor); }

    public String getSubcategoria() { return subcategoria.get(); }
    public void setSubcategoria(String valor) { this.subcategoria.set(valor); }

    public double getPrecio() { return precio.get(); }
    public void setPrecio(double valor) { this.precio.set(valor); }

    public int getStock() { return stock.get(); }
    public void setStock(int valor) { this.stock.set(valor); }

    public int getStockMinimo() { return stockMinimo.get(); }
    public void setStockMinimo(int valor) { this.stockMinimo.set(valor); }

    public String getEstado() { return estado.get(); }
    public void setEstado(String valor) { this.estado.set(valor); }

    public String getImagen() { return imagen.get(); }
    public void setImagen(String valor) { this.imagen.set(valor); }

    public boolean isBajoStock() {
        return getStock() <= getStockMinimo();
    }

    @Override
    public String toString() {
        return getCodigo() + " - " + getNombre() + " (" + getCategoria() + ")";
    }
}
