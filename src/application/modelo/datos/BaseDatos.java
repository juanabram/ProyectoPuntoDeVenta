package application.modelo.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

// Operaciones basicas SQL sobre una conexion JDBC activa
public class BaseDatos {
    private final Connection conexion;

    public BaseDatos(Connection conexion) {
        this.conexion = conexion;
    }

    public ArrayList<String[]> consultar(String tabla, String campos, String condicion) {
        return consultar(tabla, campos, condicion, new Object[0]);
    }

    public ArrayList<String[]> consultar(String tabla, String campos, String condicion, Object... parametros) {
        ArrayList<String[]> resultados = new ArrayList<>();
        if (conexion == null) return resultados;

        String sql = (campos == null || campos.equals("*"))
            ? "SELECT * FROM " + tabla
            : "SELECT " + campos + " FROM " + tabla;
        if (condicion != null && !condicion.isEmpty()) {
            sql += " WHERE " + condicion;
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            if (parametros != null) {
                for (int i = 0; i < parametros.length; i++) {
                    if (parametros[i] != null) {
                        stmt.setObject(i + 1, parametros[i]);
                    }
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int numColumnas = rsmd.getColumnCount();
                while (rs.next()) {
                    String[] fila = new String[numColumnas];
                    for (int i = 1; i <= numColumnas; i++) {
                        fila[i - 1] = rs.getString(i);
                    }
                    resultados.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al consultar registros: " + e.getMessage());
        }
        return resultados;
    }

    public boolean existe(String tabla, String condicion) {
        if (conexion == null) return false;
        String sql = "SELECT 1 FROM " + tabla + " WHERE " + condicion;
        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al verificar existencia: " + e.getMessage());
            return false;
        }
    }

    public boolean insertar(String tabla, String campos, String[] valores) {
        if (conexion == null || valores == null || valores.length == 0) return false;
        String sql = "INSERT INTO " + tabla + " (" + campos + ") VALUES (" + "?,".repeat(valores.length - 1) + "?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setString(i + 1, valores[i]);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al insertar en " + tabla + ": " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String tabla, String campo, String valor) {
        if (conexion == null) return false;
        String sql = "DELETE FROM " + tabla + " WHERE " + campo + " = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, valor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al eliminar registros: " + e.getMessage());
            return false;
        }
    }

    public boolean modificar(String tabla, String campoActualizar, String nuevoValor, String condicion) {
        return modificarVarios(tabla, new String[]{ campoActualizar }, new String[]{ nuevoValor }, condicion);
    }

    // Actualiza multiples columnas en una sola sentencia UPDATE
    public boolean modificarVarios(String tabla, String[] campos, String[] valores, String condicion) {
        if (conexion == null || campos == null || valores == null || campos.length == 0 || campos.length != valores.length) {
            return false;
        }
        StringBuilder set = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) set.append(", ");
            set.append(campos[i]).append(" = ?");
        }
        String sql = "UPDATE " + tabla + " SET " + set + " WHERE " + condicion;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setString(i + 1, valores[i]);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al actualizar " + tabla + ": " + e.getMessage());
            return false;
        }
    }

    // Control de transacciones para operaciones compuestas (ventas y devoluciones)
    public void iniciarTransaccion() {
        if (conexion == null) return;
        try {
            conexion.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("[ERROR] No se pudo iniciar transacción: " + e.getMessage());
        }
    }

    public void confirmarTransaccion() {
        if (conexion == null) return;
        try {
            conexion.commit();
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al confirmar transacción: " + e.getMessage());
        } finally {
            restaurarAutoCommit();
        }
    }

    public void revertirTransaccion() {
        if (conexion == null) return;
        try {
            conexion.rollback();
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al revertir transacción: " + e.getMessage());
        } finally {
            restaurarAutoCommit();
        }
    }

    private void restaurarAutoCommit() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.setAutoCommit(true);
            }
        } catch (SQLException ignored) {
        }
    }
}
