package application.Application;

import application.Controlador.Controlador;
import application.Controlador.VistaControlador;
import application.modelo.datos.RepositorioDatos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Punto de entrada de la aplicacion JavaFX
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/vista/PuntoDeVenta.fxml"));
            Parent root = loader.load();

            VistaControlador vistaControlador = loader.getController();
            new Controlador(vistaControlador);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/application/vista/estilos.css").toExternalForm());
            primaryStage.setTitle("Punto de Venta - Tienda de Conveniencia");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(950);
            primaryStage.setMinHeight(650);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        RepositorioDatos.getInstancia().cerrarConexion();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
