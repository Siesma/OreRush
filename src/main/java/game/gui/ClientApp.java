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
    /**
     * This class gets the Javafx fxml file and starts the GUI
     *
     */


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/startMenu.fxml")));

        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            // TODO: SEB send close packet when the application is closed.
            System.exit(1);
        });
    }
}
