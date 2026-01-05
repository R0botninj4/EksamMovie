package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        stage.setScene(new Scene(loader.load(), 1000, 600));
        stage.setTitle("Movie Collection");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}