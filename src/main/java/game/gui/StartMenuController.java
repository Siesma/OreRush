package game.gui;

import game.Main;
import game.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StartMenuController{

    private Client client;

    @FXML private Label nickname;

    @FXML private TextField newNickname;

    @FXML private TextField newLobbyName;

    @FXML private TextField newMessageTextField;
    @FXML private TextFlow chatTextFlow;

    @FXML private ListView<String> clientListView;

    @FXML private ListView<String> lobbyListView;

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
        clientListView.itemsProperty().bind(client.clientListProperty());
        lobbyListView.itemsProperty().bind(client.lobbyListProperty());

    }



    @FXML private void handleSendMessage(ActionEvent actionEvent) throws Exception {
        if (!newMessageTextField.getText().equals("")) {
            client.sendChatMessage(newMessageTextField.getText());
            newMessageTextField.setText("");
        }
        actionEvent.consume();
    }

    @FXML private void handleChangeNickname(ActionEvent actionEvent) throws Exception {
        if (!newNickname.getText().equals("")) {
            client.changeNickname(newNickname.getText());
            newNickname.setText("");
        }
        actionEvent.consume();
    }

    public void handleCreateLobby(ActionEvent actionEvent) {
        if (!newLobbyName.getText().equals("")){
            client.createLobby(newLobbyName.getText());
            newLobbyName.setText("");
        }
        actionEvent.consume();
    }

    public void handleJoinLobby(ActionEvent actionEvent) {
        changeToLobbyScene();
    }

    public void changeToLobbyScene() {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/lobby.fxml")));
            Stage stage = (Stage) nickname.getScene().getWindow();
            stage.setScene(new Scene(parent));
        }catch (IOException io){
            io.printStackTrace();
        }
    }
}
