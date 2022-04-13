package game.gui;

import game.Main;
import game.client.Client;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
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
    @FXML private TextFlow onlinePlayer;
    @FXML private ListView<String> clientListView;

    // TODO fix nicknames with underscore number

    public void initialize() {


        //get model
        this.client = new Client(Main.hostAddress, Integer.parseInt(Main.port),Main.name);

        //link model with view
        nickname.textProperty().bind(client.nicknameProperty());
        client.lastChatMessageProperty().addListener((observable, oldValue, newValue) -> {
            Text newMessage = new Text(newValue);
            chatTextFlow.getChildren().add(newMessage);
        });
        clientListView.itemsProperty().bind(client.connectedClientsProperty());

    }



    @FXML private void handleSendMessage(ActionEvent actionEvent) throws Exception {
        if (newMessageTextField.getText() != null) {
            client.sendChatMessage(newMessageTextField.getText());
            newMessageTextField.clear();
        }

    }

    @FXML private void handleChangeNickname(ActionEvent actionEvent) throws Exception {
        if (newNickname.getText() != null) {
            client.changeNickname(newNickname.getText());
            newNickname.clear();
        }
        actionEvent.consume();
    }
}
