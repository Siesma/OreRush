package game.gui;

import game.client.Client;
import game.client.LobbyInClient;
import game.datastructures.Cell;
import game.datastructures.GameMap;
import game.datastructures.Robot;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class is the controller for the lobby GUI, it connects view to the lobby and client models.
 */
public class LobbyController {

  public ChoiceBox<String> playerRobotActionList;
  public ListView<String> playerRobotList;
  public ListView<String> currentRobotMovesList;
  public Button addPossibleMovesList;
  public Button buttonMakeMove;
  Client client;
  LobbyInClient lobby;
  @FXML
  public VBox vBoxServerSettings;
  @FXML
  private Button startGameButton;
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


  @FXML
  private Slider sliderNumberOfRobots;

  @FXML
  private Label labelNumberOfRobots;

  @FXML
  private Slider sliderMapWidth;

  @FXML
  private Label labelMapWidth;

  @FXML
  private Slider sliderMapHeight;

  @FXML
  private Label labelMapHeight;

  @FXML
  private Slider sliderTurnsPerPlayer;

  @FXML
  private Label labelTurnsPerPlayer;

  @FXML
  private Slider sliderOreDensity;

  @FXML
  private Label labelOreDensity;

  @FXML
  private Slider sliderMaxAllowedMoves;

  @FXML
  private Label labelMaxAllowedMoves;

  @FXML
  private Slider sliderRadarDistance;

  @FXML
  private Label labelRadarDistance;

  @FXML
  private Slider sliderMaxClusterSize;

  @FXML
  private Label labelMaxClusterSize;

  @FXML
  private Slider sliderOreThreshold;

  @FXML
  private Label labelOreThreshold;

  public static final Logger logger = LogManager.getLogger(LobbyController.class);
  @FXML
  private Pane mapPane;
  @FXML
  private Label turnInfoLabel;
  @FXML
  private Label playerTurnLabel;
  private int xClicked = -1;
  private int yClicked = -1;

  private Robot selectedRobot;

  private GameMap currentGameMap;

  private Popup cellPreview;

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
    changeAllRobots(this.client.getLobbyInClient().getServerSettings());
    // Adds all the possible robot actions to the combobox.
    for (RobotAction robotAction : RobotAction.values()) {
      this.playerRobotActionList.getItems().add(robotAction.name());
    }
    playerRobotActionList.setValue("Move");
    lobby.gameMapPropertyProperty().addListener((obs, oldVal, newVal) -> {
      updateMap();
    });
    lobby.statusProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.equals("in game")) {
        startGameButton.setVisible(false);
      };
    });
    lobby.playerOnPlayProperty().addListener((obs, oldVal, newVal) -> {
      playerTurnLabel.setText("Turn of: " + newVal);
    });
    lobby.turnCounterProperty().addListener((obs, oldVal, newVal) -> {
      turnInfoLabel.setText("Turn number: " + newVal);
    });
    this.labelTurnsPerPlayer.setText("Turns Per Player: " + this.sliderTurnsPerPlayer.getValue());
    this.labelRadarDistance.setText("Radar Distance: " + this.sliderRadarDistance.getValue());
    this.labelOreThreshold.setText("Ore Threshold: " + this.sliderOreThreshold.getValue());
    this.labelOreDensity.setText("Ore Density: " + this.sliderOreDensity.getValue());
    this.labelNumberOfRobots.setText("Number Of Robots: " + this.sliderNumberOfRobots.getValue());
    this.labelMaxClusterSize.setText("Max Cluster Size: " + this.sliderMaxClusterSize.getValue());
    this.labelMaxAllowedMoves.setText("Max Allowed Moves: " + this.sliderMaxAllowedMoves.getValue());
    this.labelMapWidth.setText("Map Width: " + this.sliderMapWidth.getValue());
    this.labelMapHeight.setText("Map Height: " + this.sliderMapHeight.getValue());

  }

  private void changeAllRobots(ServerSettings serverSettings) {
    this.playerRobotList.getItems().clear();
    this.currentRobotMovesList.getItems().clear();
    // Initialize the list of the robots of the player.
    // Also initializes the default Move of the robots, to wait.
    for (int i = 0; i < serverSettings.getNumberOfRobots(); i++) {
      this.playerRobotList.getItems().add("Robot " + i);
      this.currentRobotMovesList.getItems().add(i + ":Wait:0:0");
    }
  }

  public void updateMap() {
    Platform.runLater(
      () -> {
        mapGridPane.getChildren().clear();
        currentGameMap = lobby.getGameMap().getIndividualGameMapForPlayer(client.getNickname());
//        GameMap.printMapToConsole(currentGameMap);
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
            Image image = null;
            Cell currentCell = currentGameMap.getCellArray()[x][y];
            if (currentCell.robotsOnCell() != null) { //TODO: differentiate robot owner
              try {
                if (currentCell.robotsOnCell().get(0).getOwner().equals(this.client.getNickname())) {
                  image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Robot.png")));
                } else {
                  image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/RobotEnemy.png")));
                }
              } catch (Exception e) {
                logger.error("The file \"Robot.png\" does not exist.");
              }
            } else if (currentCell.oreOnCell() != null) {
              String oreType = currentGameMap.getCellArray()[x][y].oreOnCell().get(0).getOreType().name();
              try {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + oreType + " Ore.png")));
              } catch (Exception e) {
                logger.error("The file \"" + oreType + " Ore.png\" does not exist.");
              }
            } else if (currentCell.radarOnCell() != null) {
              try {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Radar.png")));
              } catch (Exception e) {
                logger.error("The file \"Radar.png\" does not exist.");
              }
            } else if (currentCell.trapOnCell() != null) {
              try {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/TrapEmpty.png")));
              } catch (Exception e) {
                logger.error("The file \"TrapEmpty.png\" does not exist.");
              }
            }
            if (image != null) {
              imageView.setImage(image);
              button.setGraphic(imageView);
            }
            button.setOnMouseClicked((MouseEvent event) -> {
              Node node = (Node) event.getTarget();
              int row = GridPane.getRowIndex(node);
              int column = GridPane.getColumnIndex(node);
              selectedAndMakeActionForRobot(button, column, row);
            });

            mapGridPane.add(button, x, y, 1, 1);
          }
        }
      }
    );

  }

  /**
   * This function sets the action of the associated robot.
   * It will grab the current game maps cells to see if the selected x, y contain any robots.
   * If so it will select this robot and once a robot is selected the next positional update will set the robots upcoming move to the selected cell with the respective action.
   *
   * @param button the button that is being pressed
   * @param x      the x coordinate of this button in terms of the gameMap's cellArray
   * @param y      the y coordinate of this button in terms of the gameMap's cellArray
   */
  public void selectedAndMakeActionForRobot(Button button, int x, int y) {
    if (currentGameMap == null) {
      // how? Just how?
      return;
    }
    this.xClicked = x;
    this.yClicked = y;

    if (selectedRobot == null) {
      ArrayList<Robot> robotsOnCell = currentGameMap.getCellArray()[x][y].robotsOnCell();
      if (robotsOnCell == null) {
        return;
      }
      for (Robot r : robotsOnCell) {
        if (r.getOwner().equals(this.client.getNickname())) {
          selectedRobot = r;
        }
      }
      if (selectedRobot == null) {
        return;
      }
      logger.info(selectedRobot.getId());
    } else {
      if (playerRobotActionList.getSelectionModel().getSelectedItem() == null) {
        return;
      }
      String addition = "";
      if (playerRobotActionList.getSelectionModel().getSelectedItem().matches("^Request.*$")) {
        addition = ":" + playerRobotActionList.getSelectionModel().getSelectedItem().split("Request")[1];
      }
      int index = selectedRobot.getId();
      String moveType = playerRobotActionList.getSelectionModel().getSelectedItem();
      try {
        this.currentRobotMovesList.getItems().set(index, index + ":" + moveType + ":" + x + ":" + y + addition);
      } catch (Exception e) {
        logger.info(selectedRobot.encodeToString() + " " + index + " " + moveType);
      }

      selectedRobot = null;
    }

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

  /**
   * This method changes the status lable of a lobby, once it's game has started
   *
   * @param actionEvent UI Action that triggers this method
   */
  public void handleStartGame(ActionEvent actionEvent) {
    changeAllRobots(this.client.getLobbyInClient().getServerSettings());
    client.sendStartGame();
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


  @FXML
  void onMouseReleasedMapHeight(MouseEvent event) {
    this.labelMapHeight.setText("Map Height: " + this.sliderMapHeight.getValue());
    this.client.sendServerSettings("mapHeight:" + (int) this.sliderMapHeight.getValue());
  }

  @FXML
  void onMouseReleasedMapWidth(MouseEvent event) {
    this.labelMapWidth.setText("Map Width: " + this.sliderMapWidth.getValue());
    this.client.sendServerSettings("mapWidth:" + (int) this.sliderMapWidth.getValue());
  }

  @FXML
  void onMouseReleasedMaxAllowedMoves(MouseEvent event) {
    this.labelMaxAllowedMoves.setText("Max Allowed Moves: " + this.sliderMaxAllowedMoves.getValue());
    this.client.sendServerSettings("maxAllowedMoves:" + (int) this.sliderMaxAllowedMoves.getValue());
  }

  @FXML
  void onMouseReleasedMaxClusterSize(MouseEvent event) {
    this.labelMaxClusterSize.setText("Max Cluster Size: " + this.sliderMaxClusterSize.getValue());
    this.client.sendServerSettings("maxClusterSize:" + (int) this.sliderMaxClusterSize.getValue());
  }

  @FXML
  void onMouseReleasedNumberOfRobots(MouseEvent event) {
    this.labelNumberOfRobots.setText("Number Of Robots: " + this.sliderNumberOfRobots.getValue());
    this.client.sendServerSettings("numberOfRobots:" + (int) this.sliderNumberOfRobots.getValue());
  }

  @FXML
  void onMouseReleasedOreDensity(MouseEvent event) {
    this.labelOreDensity.setText("Ore Density: " + this.sliderOreDensity.getValue());
    this.client.sendServerSettings("oreDensity:" + this.sliderOreDensity.getValue());
  }

  @FXML
  void onMouseReleasedOreThreshold(MouseEvent event) {
    this.labelOreThreshold.setText("Ore Threshold: " + this.sliderOreThreshold.getValue());
    this.client.sendServerSettings("oreThreshold:" + this.sliderOreThreshold.getValue());
  }

  @FXML
  void onMouseReleasedRadarDistance(MouseEvent event) {
    this.labelRadarDistance.setText("Radar Distance: " + this.sliderRadarDistance.getValue());
    this.client.sendServerSettings("radarDistance:" + (int) this.sliderRadarDistance.getValue());
  }

  @FXML
  void onMouseReleasedTurnsPerPlayer(MouseEvent event) {
    this.labelTurnsPerPlayer.setText("Turns Per Player: " + this.sliderTurnsPerPlayer.getValue());
    this.client.sendServerSettings("numberOfRounds:" + (int) this.sliderTurnsPerPlayer.getValue());
  }
}
