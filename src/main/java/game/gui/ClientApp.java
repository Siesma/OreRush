package game.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

/**
 * This class loads the Javafx fxml file and starts the GUI application
 */
public class ClientApp extends Application {
    /**
     * This method loads the Javafx fxml file and shows the GUI application
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception IOException – if an error occurs during loading
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/startMenu.fxml")));
        primaryStage.setScene(new Scene(parent));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Logo.png"))));
        primaryStage.show();
    }
}
