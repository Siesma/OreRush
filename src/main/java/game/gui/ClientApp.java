package game.gui;

import game.client.Client;
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
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/startMenu.fxml")));

        primaryStage.setScene(new Scene(parent));
        primaryStage.show();

    }
}
