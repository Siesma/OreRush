package game.gui;

import game.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

public class StartMenuController {

    Client client;

    @FXML private Label nickname;

    @FXML private TextField newNickname;
    @FXML private TextField newMessageTextField;

    @FXML private TextFlow chatTextFlow;

    public void initialize() {
        //get model
        client = new Client();

        //link model with view // TODO fix
        //nickname.textProperty().bind(client.nicknameProperty());

    }



    @FXML private void handleSendMessage(ActionEvent actionEvent) throws Exception {
        if (newMessageTextField.getText() != null) {
            client.sendChatMessage(newMessageTextField.getText());
        }

    }

    @FXML private void handleChangeNickname(ActionEvent actionEvent) throws Exception {
        if (newNickname.getText() != null) {
            client.changeNickname(newNickname.getText());
        }
        actionEvent.consume();
    }
}
