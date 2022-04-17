package game.gui;

import game.client.Client;
import game.client.LobbyInClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class LobbyController {

    Client client = Client.getClient();



    public void handleStartGame(ActionEvent actionEvent) {
    }

    public void handleWinGame(ActionEvent actionEvent) {
        client.sendChatMessage("i won lul");
    }

    public void handleSendMessage(ActionEvent actionEvent) {
    }
}
