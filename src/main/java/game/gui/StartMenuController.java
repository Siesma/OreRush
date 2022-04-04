package game.gui;

import game.client.Client;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StartMenuController {

    Client client;

    @FXML private Label nickname = new Label();

    @FXML private TextField newNickname;
    @FXML private TextField newMessageTextField;

    public StartMenuController(Client client) {
        //get model
        this.client = client;

        //link model with view
        nickname.textProperty().bind(client.nicknameProperty());
        // TODO


    }



    @FXML private void handleSendMessage(ActionEvent actionEvent) {


    }

    @FXML private void handleChangeNickname(ActionEvent actionEvent) throws Exception {
        System.out.println("yayaa");
        System.out.println("text: "+ newNickname.getText());
        if (newNickname.getText() != null) {
            client.changeNickname(newNickname.getText());
        }
        actionEvent.consume();
    }
}
