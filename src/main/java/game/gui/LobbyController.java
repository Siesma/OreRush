package game.gui;

import game.client.Client;
import game.client.LobbyInClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LobbyController {

    Client client;
    LobbyInClient lobby;

    @FXML
    private TableView<Player> playerTableView;
    @FXML
    private TableColumn<Player, String> nicknameColumn;
    @FXML
    private TableColumn<Player, String> scoreColumn;

    @FXML private TextFlow chatLobbyTextFlow;
    @FXML private TextField newLobbyMessageTextField;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        client = Client.getClient();
        lobby = client.getLobbyInClient();
        playerTableView.setItems(lobby.getPlayerData());
        nicknameColumn.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
        lobby.lastChatMessageProperty().addListener((observable, oldValue, newValue) -> {
            Text newMessage = new Text(newValue);
            chatLobbyTextFlow.getChildren().add(newMessage);
        });
    }



    public void handleStartGame(ActionEvent actionEvent) {
        lobby.setStatus("in game");
    }

    public void handleWinGame(ActionEvent actionEvent) {
        client.sendChatMessage(client.getNickname() + "won in lobby " + lobby.getLobbyName());
        lobby.setStatus("finished");
    }

    public void handleSendMessage(ActionEvent actionEvent) {
        if (!newLobbyMessageTextField.getText().equals("")) {
            client.sendChatMessageToLobby(lobby.getLobbyName(), newLobbyMessageTextField.getText());
            newLobbyMessageTextField.setText("");
        }
        actionEvent.consume();
    }

    // TODO fix whisper only works on first person
    public void handleWhisperMessage(ActionEvent actionEvent) {
        try {
            if (!newLobbyMessageTextField.getText().equals("")) {
                client.sendWhisper(nicknameColumn.getCellObservableValue(playerTableView.getItems().get(playerTableView.getSelectionModel().getSelectedCells().get(0).getRow())).toString(), newLobbyMessageTextField.getText());
                newLobbyMessageTextField.setText("");
            }
        } catch (Exception ignored) {}

        actionEvent.consume();
    }


    public void handleBroadcastMessage(ActionEvent actionEvent) {
        if (!newLobbyMessageTextField.getText().equals("")) {
            client.sendBroadcast(newLobbyMessageTextField.getText());
            newLobbyMessageTextField.setText("");
        }
        actionEvent.consume();
    }
}