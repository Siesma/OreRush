package game.gui;

import game.Main;
import game.client.Client;
import game.client.LobbyInClient;
import game.server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller Class for the start menu GUI
 * handles On Action methods
 */
public class StartMenuController {


  /**
   * reference to the client that created this Controller
   */
  private Client client;

  /**
   * A button that can be clicked to create a lobby
   */
  @FXML
  private Button createLobbyButton;

  /**
   * A button that can be clicked to join a lobby
   */
  @FXML
  private Button joinLobbyButton;

  /**
   * A label that displays your current nickname
   */
  @FXML
  private Label nickname;

  /**
   * A textField where you can type a new nickname
   */
  @FXML
  private TextField newNickname;

  /**
   * A textField where you can type a new lobby name
   */
  @FXML
  private TextField newLobbyName;

  /**
   * A textField where you can type a new message
   */
  @FXML
  private TextField newMessageTextField;

  /**
   * A textFlow which stores the current and past chat
   */
  @FXML
  private TextFlow chatTextFlow;

  /**
   * A listview that stores a list of all the clients connected to the server
   */
  @FXML
  private ListView<String> clientListView;

  /**
   * A listview that stores a list of all the lobbies on the server
   */
  @FXML
  private TableView<LobbyInClient> lobbyTableView;

  /**
   * A TableColumn that stores the lobbies and the name of the lobby
   */
  @FXML
  private TableColumn<LobbyInClient, String> lobbyNameColumn;

  /**
   * A TableColumn that stores the lobbies and the status of said lobby.
   */
  @FXML
  private TableColumn<LobbyInClient, String> statusColumn;

  /**
   * A TableColumn that stores the lobbies and the players inside the lobby
   */
  @FXML
  private TableColumn<LobbyInClient, String> playerColumn;

  public static final Logger logger = LogManager.getLogger(Server.class);

  /**
   * Initializes all the mandatory components of the startMenuController
   */
  public void initialize() {

    //get model
    this.client = new Client(Main.hostAddress, Integer.parseInt(Main.port), Main.name);

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
    Platform.runLater(() -> nickname.getScene().getWindow().setOnCloseRequest(e -> {
      client.sendClosePacket();
      System.exit(0);
    }));

  }


  /**
   * tests if the textfield is empty  sends the message to the CLients and clears the Textfield
   */
  @FXML
  private void handleSendMessage(ActionEvent actionEvent) {
    if (!newMessageTextField.getText().equals("")) {
      client.sendChatMessage(newMessageTextField.getText());
      newMessageTextField.setText("");
    }
    actionEvent.consume();
  }

  /**
   * checks if the textfield is empty changes the nickname and clears the textfield
   */
  @FXML
  private void handleChangeNickname(ActionEvent actionEvent) {
    if (!newNickname.getText().equals("")) {
      client.changeNickname(newNickname.getText());
      newNickname.setText("");
    }
    actionEvent.consume();
  }

  /**
   * checks if the Textfield is empty creates a Lobby joins the Lobby and clear the textfield
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void handleCreateLobby(ActionEvent actionEvent) {
    if (!newLobbyName.getText().equals("")) {
      createLobbyButton.setDisable(true);
      joinLobbyButton.setDisable(true);
      client.createLobby(newLobbyName.getText());
      client.joinLobby(newLobbyName.getText());
      newLobbyName.setText("");
      changeToLobbyScene();
    }
    actionEvent.consume();
  }

  /**
   * Joins the Lobby
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void handleJoinLobby(ActionEvent actionEvent) {
    if(lobbyTableView.getItems().size() == 0 || lobbyTableView.getSelectionModel() == null) {
      return;
    }
    try {
      client.joinLobby(lobbyNameColumn.getCellObservableValue(lobbyTableView.getItems().get(lobbyTableView.getSelectionModel().getSelectedCells().get(0).getRow())).getValue());
      changeToLobbyScene();
    } catch (Exception e) {
      logger.fatal("There was an error while trying to connect to the lobby", e);
    }
    actionEvent.consume();
  }

  /**
   * starts the Gui of the Lobby with the lobby fxml file
   */
  public void changeToLobbyScene() {
    try {
      while (client.getLobbyInClient() == null) {
        Thread.sleep(1000);
        logger.info("Waiting for the lobby to be created.");
      }

      Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/lobby.fxml")));
      Stage stage = new Stage();
      stage.setScene(new Scene(parent));
      stage.setFullScreen(true);
      stage.setOnCloseRequest(event -> {
        client.leaveLobby(client.getLobbyInClient().getLobbyName());
        client.setLobbyInClient(null);
        createLobbyButton.setDisable(false);
        joinLobbyButton.setDisable(false);
      });
      stage.show();
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * checks if the whisper message can be sent and send it
   *
   * @param actionEvent unused actionEvent that triggers this function
   */
  public void handleWhisperMessage(ActionEvent actionEvent) {
    if (clientListView.getSelectionModel().getSelectedItem() != null &&
      !newMessageTextField.getText().equals("")) {
      client.sendWhisper(clientListView.getSelectionModel().getSelectedItem(), newMessageTextField.getText());
      newMessageTextField.setText("");
    }
    actionEvent.consume();
  }

  /**
   * checks if the textfield is empty and send the message as broadcast
   *
   * @param actionEvent unused actionEvent that triggers this function
   */
  public void handleBroadcastMessage(ActionEvent actionEvent) {
    if (!newMessageTextField.getText().equals("")) {
      client.sendBroadcast(newMessageTextField.getText());
      newMessageTextField.setText("");
    }
    actionEvent.consume();
  }

  public void handleHighScore(ActionEvent actionEvent) {
    client.sendHighScore();
  }
}
