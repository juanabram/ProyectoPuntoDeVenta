package application.modelo.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase ConexionBD:
 * Preparada para la conexión con el Sistema Gestor de Base de Datos Relacional.
 * 
 * INSTRUCCIONES PARA EL INTEGRANTE DEL EQUIPO ENCARGADO DE LA BASE DE DATOS:
 * 1. Seleccione el motor de base de datos deseado (MySQL, SQL Server, Microsoft Access o PostgreSQL).
 * 2. Configure los parámetros URL, USUARIO y PASSWORD en las constantes de abajo.
 * 3. Ejecute el script 'schema.sql' incluido en la raíz del proyecto para crear las tablas y datos iniciales.
 * 4. Al iniciar la aplicación, ConexionBD verificará automáticamente la disponibilidad del servidor.
 */
public class ConexionBD {

    // =========================================================================
    // CONFIGURACIÓN DE CONEXIÓN (AJUSTAR SEGÚN EL MOTOR SELECCIONADO)
    // =========================================================================

    // OPCIÓN A: MySQL / MariaDB (Recomendado para proyectos escolares y XAMPP)
    private static final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";
    private static final String URL_MYSQL = "jdbc:mysql://localhost:3306/punto_de_venta?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO_MYSQL = "root";
    private static final String PASSWORD_MYSQL = "";

    // OPCIÓN B: Microsoft SQL Server
    private static final String DRIVER_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL_MSSQL = "jdbc:sqlserver://localhost:1433;databaseName=punto_de_venta;encrypt=false";
    private static final String USUARIO_MSSQL = "sa";
    private static final String PASSWORD_MSSQL = "tu_password";

    // OPCIÓN C: Microsoft Access (.accdb vía UCanAccess)
    private static final String DRIVER_ACCESS = "net.ucanaccess.jdbc.UcanaccessDriver";
    private static final String URL_ACCESS = "jdbc:ucanaccess://datos/punto_de_venta.accdb";

    // Motor activo actualmente configurado
    private static String driverActivo = DRIVER_MYSQL;
    private static String urlActiva = URL_MYSQL;
    private static String usuarioActivo = USUARIO_MYSQL;
    private static String passwordActivo = PASSWORD_MYSQL;

    private static Connection conexion = null;

    /**
     * Obtiene una conexión activa a la Base de Datos.
     * Si no es posible conectar (por ejemplo, si el servidor aún no está encendido),
     * devuelve null de manera segura para que el backend opere con el repositorio en memoria.
     * 
     * @return Connection activa o null si no se pudo conectar.
     */
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
            // Driver no encontrado en el classpath de momento
            return null;
        } catch (SQLException e) {
            // Base de datos apagada o credenciales pendientes
            return null;
        }
    }

    /**
     * Prueba si la base de datos está actualmente accesible.
     * 
     * @return true si la conexión responde; false en caso contrario.
     */
    public static boolean probarConexion() {
        try (Connection c = obtenerConexion()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Permite al compañero de equipo cambiar la configuración en tiempo de ejecución.
     */
    public static void configurar(String driver, String url, String usuario, String password) {
        driverActivo = driver;
        urlActiva = url;
        usuarioActivo = usuario;
        passwordActivo = password;
        cerrarConexion();
    }

    /**
     * Cierra de manera segura la conexión si está abierta.
     */
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
