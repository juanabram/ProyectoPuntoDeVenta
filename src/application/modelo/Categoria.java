package application.modelo;

import java.util.ArrayList;
import java.util.List;

// Categoria de productos del catalogo
public class Categoria {

    private int id;
    private String nombre;
    private List<String> subcategorias;

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.subcategorias = new ArrayList<>();
    }

    public Categoria(int id, String nombre, List<String> subcategorias) {
        this.id = id;
        this.nombre = nombre;
        this.subcategorias = new ArrayList<>(subcategorias);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getSubcategorias() { return subcategorias; }
    public void setSubcategorias(List<String> subcategorias) { this.subcategorias = subcategorias; }

    @Override
    public String toString() {
        return nombre;
    }
}
