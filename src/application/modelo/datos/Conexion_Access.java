package application.modelo.datos;

// Conexion a Microsoft Access (.accdb) usando UCanAccess
public class Conexion_Access extends ConexionBD {

    public Conexion_Access(String baseDatos) {
        super("", baseDatos, "", "");
    }

    public Conexion_Access(String servidor, String baseDatos, String usuario, String password) {
        super(servidor, baseDatos, usuario, password);
    }

    @Override
    protected String construirURL(String servidor, String baseDatos) {
        return "jdbc:ucanaccess://" + baseDatos;
    }
}
