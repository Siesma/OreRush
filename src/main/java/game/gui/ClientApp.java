package game.gui;

import game.client.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class ClientApp extends Application {
    private Client client;

    @Override
    public void start(Stage primaryStage) throws Exception{
        List<String> parameters= getParameters().getRaw();
        client = new Client(parameters.get(0),Integer.parseInt(parameters.get(1)) ,parameters.get(2));

        Parent parent;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/startMenu.fxml"));

        StartMenuController startMenuController = new StartMenuController(client);
        loader.setController(startMenuController);

        parent = loader.load();

        primaryStage.setScene(new Scene(parent));
        primaryStage.show();

    }

}
