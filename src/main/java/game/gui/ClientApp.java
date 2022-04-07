package game.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class ClientApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> parameters = getParameters().getRaw();
        // client = new Client(parameters.get(0),Integer.parseInt(parameters.get(1)) ,parameters.get(2));

        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/startMenu.fxml")));

        primaryStage.setScene(new Scene(parent));
        primaryStage.show();

    }
}
