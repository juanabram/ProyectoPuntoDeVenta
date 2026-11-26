package application.modelo.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Clase base para conexiones JDBC; cada motor define su propia URL
public abstract class ConexionBD {
    private Connection conexion;
    private final String url;
    private final String usuario;
    private final String password;

    public ConexionBD(String servidor, String baseDatos, String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
        this.url = this.construirURL(servidor, baseDatos);
        conectar();
    }

    protected abstract String construirURL(String servidor, String baseDatos);

    private void conectar() {
        try {
            conexion = DriverManager.getConnection(url, usuario, password);
            System.out.println("[OK] Conexión establecida correctamente con " + url);
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al conectar a la BD: " + e.getMessage());
        }
    }

    public Connection getConexion() {
        return conexion;
    }

    public boolean estaActiva() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("[OK] Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al cerrar la conexión: " + e.getMessage());
        } finally {
            conexion = null;
        }
    }
}
