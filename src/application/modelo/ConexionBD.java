package application.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase abstracta ConexionBD (misma estructura que ApartadosLabFIT):
 * abre la conexión JDBC al crearse y la cierra con cerrarConexion().
 * Cada motor de base de datos construye su propia URL en una subclase.
 */
public abstract class ConexionBD {
    private Connection conexion;
    private String url;
    private String usuario;
    private String password;

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

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("[OK] Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
