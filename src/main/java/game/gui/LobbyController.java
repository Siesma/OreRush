package game.gui;

import game.client.Client;
import game.client.LobbyInClient;
import game.datastructures.GameMap;
import game.datastructures.RobotAction;
import game.server.ClientThread;
import game.server.ServerSettings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.Objects;

public class LobbyController {

  public ChoiceBox<String> playerRobotActionList;
  public ListView<String> playerRobotList;
  public ListView<String> currentRobotMovesList;
  public Button addPossibleMovesList;
  public TextField textFieldXCoord;
  public TextField textFieldYCoord;
  public Button buttonMakeMove;
  Client client;
  LobbyInClient lobby;
  @FXML
  private TableView<Player> playerTableView;
  @FXML
  private TableColumn<Player, String> nicknameColumn;
  @FXML
  private TableColumn<Player, String> scoreColumn;
  @FXML
  private TextFlow chatLobbyTextFlow;
  @FXML
  private TextField newLobbyMessageTextField;
  @FXML
  private GridPane mapGridPane;

  public static final Logger logger = LogManager.getLogger(ClientThread.class);
  @FXML
  private Pane mapPane;
  private int xClicked = -1;
  private int yClicked = -1;

  /**
   * Initializes the controller class. This method is automatically called
   * after the fxml file has been loaded.
   */
  @FXML
  private void initialize() throws FileNotFoundException {
    client = Client.getClient();
    lobby = client.getLobbyInClient();
    playerTableView.setItems(lobby.getPlayerData());
    nicknameColumn.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
    scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
    lobby.lastChatMessageProperty().addListener((observable, oldValue, newValue) -> {
      Text newMessage = new Text(newValue);
      chatLobbyTextFlow.getChildren().add(newMessage);
    });
    playerTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
    });
    // Initialize the list of the robots of the player.
    // Also initializes the default Move of the robots, to wait.

    for (int i = 0; i < (new ServerSettings("")).getNumberOfRobots(); i++) {
      this.playerRobotList.getItems().add("Robot " + i);
      this.currentRobotMovesList.getItems().add(i + ":Wait:0:0");
    }
    // Adds all the possible robot actions to the combobox.
    for (RobotAction robotAction : RobotAction.values()) {
      this.playerRobotActionList.getItems().add(robotAction.name());
    }
    lobby.gameMapPropertyProperty().addListener((obs, oldVal, newVal) -> {
      updateMap();
    });


  }

  public void updateMap() {
    Platform.runLater(
            () -> {
              mapGridPane.getChildren().clear();
              GameMap currentGameMap = lobby.getGameMap();
              int xMax = currentGameMap.getGameMapSize()[0];
              int yMax = currentGameMap.getGameMapSize()[1];
              int mapPixel = 500;
              int cellSize = Math.min(mapPixel / xMax, mapPixel / yMax);
              for (int x = 0; x < xMax; x++) {
                for (int y = 0; y < yMax; y++) {
                  Button button = new Button();
                  button.setPrefSize(cellSize, cellSize);
                  button.setPadding(Insets.EMPTY);
                  ImageView imageView = new ImageView();
                  imageView.setFitHeight(cellSize);
                  imageView.setFitWidth(cellSize);
                  String type;
                  Image image;
                  if (currentGameMap.getCellArray()[x][y].robotsOnCell() != null) { //TODO: differentiate robot owner
                    try {
                      image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Robot.png")));
                      imageView.setImage(image);
                      button.setGraphic(imageView);
                    } catch (Exception e) {
                      logger.error("The file \"Robot.png\" does not exist.");
                    }
                  } else if (currentGameMap.getCellArray()[x][y].oreOnCell() != null) {
                    String oreType = currentGameMap.getCellArray()[x][y].oreOnCell().get(0).getOreType().name();
                    try {
                      image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + oreType + " Ore.png")));
                      imageView.setImage(image);
                      button.setGraphic(imageView);
                    } catch (Exception e) {
                      logger.error("The file \"" + oreType + " Ore.png\" does not exist.");
                    }
                  } else if (currentGameMap.getCellArray()[x][y].radarOnCell() != null) {
                    try {
                      image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Radar.png")));
                      imageView.setImage(image);
                      button.setGraphic(imageView);
                    } catch (Exception e) {
                      logger.error("The file \"Radar.png\" does not exist.");
                    }
                  } else if (currentGameMap.getCellArray()[x][y].trapOnCell() != null) {
                    try {
                      image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/TrapEmpty.png")));
                      imageView.setImage(image);
                      button.setGraphic(imageView);
                    } catch (Exception e) {
                      logger.error("The file \"TrapEmpty.png\" does not exist.");
                    }
                  }
                  button.setOnMouseClicked((MouseEvent event) -> {
                    Node node = (Node) event.getTarget();
                    int row = GridPane.getRowIndex(node);
                    int column = GridPane.getColumnIndex(node);
                    saveClickedPosition(column, row);
                  });
                  mapGridPane.add(button, x, y, 1, 1);
                }
              }
            }
    );

  }

  public void saveClickedPosition(int x, int y) {
    this.xClicked = x;
    this.yClicked = y;
  }


  public void onActionRobotMoveTypes(ActionEvent actionEvent) {

  }

  public void onActionAddPossibleMovesList(ActionEvent actionEvent) {

  }

  /**
   * Once the player is finished with the move they can click the "Finish move" button that sends the given information to the server.
   * I sadly had to quickly modify the request structure to make this work.
   * TODO: Revert the request move structure to its original dynamic state.
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void onActionButtonMakeMove(ActionEvent actionEvent) {
    String defaultTextMessage = "";
    if (xClicked == -1 || yClicked == -1) {
      return;
    }
    if (playerRobotActionList.getSelectionModel().getSelectedItem() == null) {
      return;
    }
    if (playerRobotList.getSelectionModel().getSelectedItem() == null) {
      return;
    }
    int index = playerRobotList.getSelectionModel().getSelectedIndex();
    String addition = "";
    if (playerRobotActionList.getSelectionModel().getSelectedItem().matches("^Request.*$")) {
      addition = ":" + playerRobotActionList.getSelectionModel().getSelectedItem().split("Request")[1];
    }
    this.currentRobotMovesList.getItems().set(index,
            index + ":" + playerRobotActionList.getSelectionModel().getSelectedItem() + ":" + xClicked + ":" + yClicked + addition);

  }

  /**
   * This method executes the moves once the "EndTurnButton" is pressed
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void onActionEndTurnButton(ActionEvent actionEvent) {
    if (client == null) {
      return;
    }
    client.makeMove(this);
  }

  //TODO

  /**
   * This method changes the status lable of a lobby, once it's game has started
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void handleStartGame(ActionEvent actionEvent) {
    client.sendStartGame();


    lobby.setStatus("in game");
  }

  /**
   * get the winner and ends the Lobby
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void handleWinGame(ActionEvent actionEvent) {
    client.sendChatMessage(client.getNickname() + "won in lobby " + lobby.getLobbyName());
    lobby.setStatus("finished");
  }

  /**
   * sends the message from the textfield to the Lobby Chat
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void handleSendMessage(ActionEvent actionEvent) {
    if (!newLobbyMessageTextField.getText().equals("")) {
      client.sendChatMessageToLobby(lobby.getLobbyName(), newLobbyMessageTextField.getText());
      newLobbyMessageTextField.setText("");
    }
    actionEvent.consume();
  }

  // TODO fix whisper only works on first person

  /**
   * sends a Whisper Message
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void handleWhisperMessage(ActionEvent actionEvent) {
    try {
      if (!newLobbyMessageTextField.getText().equals("")) {
        client.sendWhisper(playerTableView.getSelectionModel().getSelectedItem().getNickname(), newLobbyMessageTextField.getText());
        newLobbyMessageTextField.setText("");
      }
    } catch (Exception ignored) {
    }

    actionEvent.consume();
  }

  /**
   * sends a Broadcast Message to all clients
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void handleBroadcastMessage(ActionEvent actionEvent) {
    if (!newLobbyMessageTextField.getText().equals("")) {
      client.sendBroadcast(newLobbyMessageTextField.getText());
      newLobbyMessageTextField.setText("");
    }
    actionEvent.consume();
  }
}
