package application.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase BaseDatos (misma estructura que ApartadosLabFIT):
 * consultar, existe, insertar, eliminar y modificar registros sobre una conexión activa.
 */
public class BaseDatos {
    private Connection conexion;

    public BaseDatos(Connection conexion) {
        this.conexion = conexion;
    }

    public ArrayList<String[]> consultar(String tabla, String campos, String condicion) {
        ArrayList<String[]> resultados = new ArrayList<>();
        if (conexion == null) return resultados;
        String sql = (campos == null || campos.equals("*")) ? "SELECT * FROM " + tabla : "SELECT " + campos + " FROM " + tabla;
        if (condicion != null && !condicion.isEmpty()) {
            sql += " WHERE " + condicion;
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumnas = rsmd.getColumnCount();

            while (rs.next()) {
                String[] fila = new String[numColumnas];
                for (int i = 1; i <= numColumnas; i++) {
                    fila[i - 1] = rs.getString(i);
                }
                resultados.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al consultar registros: " + e.getMessage());
        }
        return resultados;
    }

    public ArrayList<String[]> consultar(String tabla, String campos, String condicion, Object parametro) {
        ArrayList<String[]> resultados = new ArrayList<>();
        if (conexion == null) return resultados;
        String sql = (campos == null || campos.equals("*")) ? "SELECT * FROM " + tabla : "SELECT " + campos + " FROM " + tabla;
        if (condicion != null && !condicion.isEmpty()) {
            sql += " WHERE " + condicion;
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setObject(1, parametro);
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
            System.err.println("[ERROR] Error al consultar registros con parámetro: " + e.getMessage());
        }
        return resultados;
    }

    public boolean existe(String tabla, String condicion) {
        boolean enc = false;
        if (conexion == null) return false;
        String sql = "SELECT 1 FROM " + tabla + " WHERE " + condicion;

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            enc = rs.next();
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al verificar existencia: " + e.getMessage());
        }
        return enc;
    }

    public void insertar(String tabla, String campos, String[] valores) {
        if (conexion == null) return;
        String sql = "INSERT INTO " + tabla + " (" + campos + ") VALUES (" + "?,".repeat(valores.length - 1) + "?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setString(i + 1, valores[i]);
            }
            ps.executeUpdate();
            System.out.println("[OK] Registro insertado correctamente en " + tabla + ".");
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al insertar registro en " + tabla + ": " + e.getMessage());
        }
    }

    public void eliminar(String tabla, String campo, String valor) {
        if (conexion == null) return;
        String sql = "DELETE FROM " + tabla + " WHERE " + campo + " = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, valor);
            int resultado = pstmt.executeUpdate();
            System.out.println(resultado > 0 ? "[OK] Registros eliminados correctamente." : "[AVISO] No se encontraron registros para eliminar.");
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al eliminar registros: " + e.getMessage());
        }
    }

    public void modificar(String tabla, String campoActualizar, String nuevoValor, String condicion) {
        if (conexion == null) return;
        String sql = "UPDATE " + tabla + " SET " + campoActualizar + " = ? WHERE " + condicion;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoValor);
            ps.executeUpdate();
            System.out.println("[OK] Registro(s) actualizado(s) correctamente en " + tabla + ".");
        } catch (SQLException e) {
            System.err.println("[ERROR] Error al actualizar registros: " + e.getMessage());
        }
    }
}
