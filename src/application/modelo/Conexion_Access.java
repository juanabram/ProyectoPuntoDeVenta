package application.modelo;

/**
 * Conexión a Microsoft Access (.accdb) mediante UCanAccess
 * (misma clase que en ApartadosLabFIT).
 *
 * Uso: new Conexion_Access("", "PuntoDeVenta.accdb", "", "")
 * La ruta es relativa a la carpeta del proyecto (directorio de trabajo de Eclipse).
 */
public class Conexion_Access extends ConexionBD {

    public Conexion_Access(String servidor, String baseDatos, String usuario, String password) {
        super(servidor, baseDatos, usuario, password);
    }

    @Override
    protected String construirURL(String servidor, String baseDatos) {
        return "jdbc:ucanaccess://" + baseDatos;
    }
}
