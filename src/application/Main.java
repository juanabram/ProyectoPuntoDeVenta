package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Vista/prueba6.fxml")
        );
        Parent root = loader.load();
        
/*        CVmenuprincipal vista = loader.getController();
        
        // Inicialización del modelo (delega la carga a RepositorioEscolar y Access)
        Mprincipal modelo = new Mprincipal();
        
        // Inicialización del controlador vinculando la vista y el modelo
        Cprincipal controlador = new Cprincipal(vista, modelo); */
        
        stage.setScene(new Scene(root));
        stage.setTitle("Control de Alumnos y Materias - Sistema Escolar");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
