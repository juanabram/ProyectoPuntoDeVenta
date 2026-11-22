package application.Application;

import application.Controlador.Controlador;
import application.Controlador.VistaControlador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase Principal:
 * Punto de entrada de la aplicación JavaFX del Punto de Venta.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML desde application.vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/vista/PuntoDeVenta.fxml"));
            Parent root = loader.load();

            // Obtener la instancia del VistaControlador generada por el FXMLLoader
            VistaControlador vistaControlador = loader.getController();

            // Instanciar la clase controladora para enlazar las interacciones y lógica
            new Controlador(vistaControlador);

            // Configurar y desplegar la ventana principal
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

    /**
     * Método main para iniciar la aplicación JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
