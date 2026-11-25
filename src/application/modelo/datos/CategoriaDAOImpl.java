package application.modelo.datos;

import java.util.List;
import application.modelo.Categoria;

/**
 * Implementación de ICategoriaDAO:
 * Administración de categorías del catálogo.
 */
public class CategoriaDAOImpl implements ICategoriaDAO {

    private final RepositorioDatos repo = RepositorioDatos.getInstancia();

    @Override
    public List<Categoria> listarCategorias() {
        return repo.obtenerCategorias();
    }

    @Override
    public Categoria buscarPorNombre(String nombre) {
        if (nombre == null) return null;
        for (Categoria c : listarCategorias()) {
            if (c.getNombre().equalsIgnoreCase(nombre.trim())) {
                return c;
            }
        }
        return null;
    }

    @Override
    public boolean agregarCategoria(Categoria categoria) {
        return true;
    }
}
