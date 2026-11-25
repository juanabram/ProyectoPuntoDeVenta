package application.modelo.datos;

import java.util.List;
import application.modelo.Devolucion;

/**
 * Implementación de IDevolucionDAO:
 * Registra y gestiona las notas de devolución y reembolsos al cliente.
 */
public class DevolucionDAOImpl implements IDevolucionDAO {

    private final RepositorioDatos repo = RepositorioDatos.getInstancia();

    @Override
    public boolean registrarDevolucion(Devolucion dev) {
        repo.registrarDevolucion(dev);
        return true;
    }

    @Override
    public Devolucion buscarPorFolio(String folio) {
        if (folio == null) return null;
        return repo.obtenerDevolucion(folio.trim());
    }

    @Override
    public List<Devolucion> listarDevoluciones() {
        return repo.obtenerTodasLasDevoluciones();
    }
}
