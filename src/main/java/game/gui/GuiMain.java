package game.gui;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This is an example JavaFX-Application.
 */
public class GuiMain extends Application {

    @Override
    public void start(Stage stage) {

        stage.setTitle("Ore Rush");

        // create a button
        Button nicknameButton = new Button("change Name");

        // create a stack pane
        StackPane stackPane = new StackPane();

        // add button
        stackPane.getChildren().add(nicknameButton);

        // create a scene
        Scene scene = new Scene(stackPane, 200, 200);
        stage.setScene(scene);
        stage.show();

    }

}


