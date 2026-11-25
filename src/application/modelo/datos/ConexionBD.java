package application.modelo.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Maneja la conexion JDBC con la base de datos.
// Si no hay conexion disponible devuelve null para usar el respaldo en memoria.
public class ConexionBD {

    // Configuracion por motor
    private static final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";
    private static final String URL_MYSQL = "jdbc:mysql://localhost:3306/punto_de_venta?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO_MYSQL = "root";
    private static final String PASSWORD_MYSQL = "";

    private static final String DRIVER_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL_MSSQL = "jdbc:sqlserver://localhost:1433;databaseName=punto_de_venta;encrypt=false";
    private static final String USUARIO_MSSQL = "sa";
    private static final String PASSWORD_MSSQL = "tu_password";

    private static final String DRIVER_ACCESS = "net.ucanaccess.jdbc.UcanaccessDriver";
    private static final String URL_ACCESS = "jdbc:ucanaccess://datos/punto_de_venta.accdb";

    // Motor activo
    private static String driverActivo = DRIVER_MYSQL;
    private static String urlActiva = URL_MYSQL;
    private static String usuarioActivo = USUARIO_MYSQL;
    private static String passwordActivo = PASSWORD_MYSQL;

    private static Connection conexion = null;

    // Devuelve una conexion activa o null si no se pudo conectar
    public static synchronized Connection obtenerConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                return conexion;
            }
            Class.forName(driverActivo);
            conexion = DriverManager.getConnection(urlActiva, usuarioActivo, passwordActivo);
            System.out.println("[ConexionBD] Conexión establecida con éxito a: " + urlActiva);
            return conexion;
        } catch (ClassNotFoundException e) {
            // Driver no encontrado en el classpath
            return null;
        } catch (SQLException e) {
            // BD no disponible o credenciales pendientes
            return null;
        }
    }

    public static boolean probarConexion() {
        try (Connection c = obtenerConexion()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Permite cambiar driver y credenciales en tiempo de ejecucion
    public static void configurar(String driver, String url, String usuario, String password) {
        driverActivo = driver;
        urlActiva = url;
        usuarioActivo = usuario;
        passwordActivo = password;
        cerrarConexion();
    }

    public static synchronized void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conexion = null;
            }
        }
    }
}
