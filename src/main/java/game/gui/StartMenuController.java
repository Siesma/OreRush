package game.gui;

import game.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class StartMenuController{

    Client client;
    String hostname;
    String port;
    String name;

    @FXML private Label nickname;

    @FXML private TextField newNickname;
    @FXML private TextField newMessageTextField;

    @FXML private TextFlow chatTextFlow;


    public void initialize() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "clientInfo.txt"));
            hostname = reader.readLine();
            port = reader.readLine();
            name = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get model
        this.client = new Client(hostname, Integer.parseInt(port),name);

        //link model with view
        nickname.textProperty().bind(client.nicknameProperty());

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
