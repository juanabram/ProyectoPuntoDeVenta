package application.modelo.datos;

import java.util.List;
import application.modelo.Categoria;

/**
 * Interfaz ICategoriaDAO:
 * Catálogo de categorías y subcategorías.
 */
public interface ICategoriaDAO {
    List<Categoria> listarCategorias();
    Categoria buscarPorNombre(String nombre);
    boolean agregarCategoria(Categoria categoria);
}
