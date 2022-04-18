package game.gui;

import game.Main;
import game.client.Client;
import game.client.LobbyInClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    @FXML
    private TableView<LobbyInClient> lobbyTableView;
    @FXML
    private TableColumn<LobbyInClient, String> lobbyNameColumn;
    @FXML
    private TableColumn<LobbyInClient, String> statusColumn;
    @FXML
    private TableColumn<LobbyInClient, String> playerColumn;


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
        lobbyTableView.setItems(
                client.getLobbyData());
        lobbyNameColumn.setCellValueFactory(cellData -> cellData.getValue().lobbyNameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        playerColumn.setCellValueFactory(cellData -> cellData.getValue().playersProperty());
        clientListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        });

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
            client.joinLobby(newLobbyName.getText());
            newLobbyName.setText("");
            changeToLobbyScene();
        }
        actionEvent.consume();
    }

    public void handleJoinLobby(ActionEvent actionEvent) {
        try {
            client.joinLobby(lobbyNameColumn.getCellObservableValue(lobbyTableView.getItems().get(lobbyTableView.getSelectionModel().getSelectedCells().get(0).getRow())).getValue());
            changeToLobbyScene();
        } catch (Exception ignored) {

        }
        actionEvent.consume();
    }

    public void changeToLobbyScene() {
        try {
            while (client.getLobbyInClient() == null) {
                Thread.sleep(1000);
                System.out.println("Waiting for the lobby to be created.");
            }

            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/lobby.fxml")));
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setOnCloseRequest(event -> {
                client.leaveLobby(client.getLobbyInClient().getLobbyName());
                client.setLobbyInClient(null);
            });
            stage.show();
        }catch (IOException io) {
            io.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void handleWhisperMessage(ActionEvent actionEvent) {
        if (clientListView.getSelectionModel().getSelectedItem() != null &&
                !newMessageTextField.getText().equals("")) {
            client.sendWhisper(clientListView.getSelectionModel().getSelectedItem(), newMessageTextField.getText());
            newMessageTextField.setText("");
        }
        actionEvent.consume();
    }


    public void handleBroadcastMessage(ActionEvent actionEvent) {
        if (!newMessageTextField.getText().equals("")) {
            client.sendBroadcast(newMessageTextField.getText());
            newMessageTextField.setText("");
        }
        actionEvent.consume();
    }
}
