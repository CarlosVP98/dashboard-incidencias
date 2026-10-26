package es.dam.di.incidencias;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal (versión FXML).
 *
 * Ya no construye la interfaz: la carga desde dashboard.fxml con FXMLLoader.
 * La estructura está en el FXML, el aspecto en styles.css y el comportamiento
 * en DashboardController.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
        Parent raiz = loader.load();

        Scene scene = new Scene(raiz, 1100, 750);

        stage.setTitle("Gestión de incidencias");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
