package game.gui;

import game.client.Client;
import game.client.LobbyInClient;
import game.datastructures.Cell;
import game.datastructures.Robot;
import game.datastructures.*;
import game.helper.MathHelper;
import game.server.ServerSettings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class is the controller for the lobby GUI, it connects view to the lobby and client models.
 */
public class LobbyController {

  public ArrayList<String> playerRobotList = new ArrayList<>();
  public ArrayList<String> currentRobotMovesList = new ArrayList<>();
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
  private Pane mapPane;

  @FXML
  public AnchorPane anchorPane;
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
  private Label turnInfoLabel;
  @FXML
  private Label playerTurnLabel;
  private int xClicked = -1;
  private int yClicked = -1;

  Popup moveSelectionPopup;

  private Robot selectedRobot;

  private GameMap currentGameMap;

  private Color[] colours;


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
    playerTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
    });
    changeAllRobots(this.client.getLobbyInClient().getServerSettings());
    lobby.gameMapPropertyProperty().addListener((obs, oldVal, newVal) -> {
      updateMap();
    });
    lobby.statusProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.equals("in game")) {
        startGameButton.setVisible(false);
      }
    });
    lobby.playerOnPlayProperty().addListener((obs, oldVal, newVal) -> {
      playerTurnLabel.setText("Turn of: " + newVal);
    });
    lobby.turnCounterProperty().addListener((obs, oldVal, newVal) -> {
      turnInfoLabel.setText("Turn number: " + newVal);
    });
    mapPane.heightProperty().addListener((obs, oldVal, newVal) -> {
      if (!turnInfoLabel.getText().equals("Turn info")) {
        updateMap();
      }
    });
    mapPane.widthProperty().addListener((obs, oldVal, newVal) -> {
      if (!turnInfoLabel.getText().equals("Turn info")) {
        updateMap();
      }
    });

    moveSelectionPopup = new Popup();
    moveSelectionPopup.hide();
    moveSelectionPopup.getContent().clear();
    moveSelectionPopup.getContent().add(getPopupContent());

    this.labelTurnsPerPlayer.setText("Turns Per Player: " + this.sliderTurnsPerPlayer.getValue());
    this.labelRadarDistance.setText("Radar Distance: " + this.sliderRadarDistance.getValue());
    this.labelOreThreshold.setText("Ore Threshold: " + this.sliderOreThreshold.getValue());
    this.labelOreDensity.setText("Ore Density: " + this.sliderOreDensity.getValue());
    this.labelNumberOfRobots.setText("Number Of Robots: " + this.sliderNumberOfRobots.getValue());
    this.labelMaxClusterSize.setText("Max Cluster Size: " + this.sliderMaxClusterSize.getValue());
    this.labelMaxAllowedMoves.setText("Max Allowed Moves: " + this.sliderMaxAllowedMoves.getValue());
    this.labelMapWidth.setText("Map Width: " + this.sliderMapWidth.getValue());
    this.labelMapHeight.setText("Map Height: " + this.sliderMapHeight.getValue());
    this.colours = MathHelper.getRandomColours(this.client.getLobbyInClient().getServerSettings().numberOfRobots.getVal());
  }

  /**
   * @param action   the action that the selected Robot should perform
   * @param addition the additionally mandatory information about the inventory change that occurs for requests
   */
  public void setActionViaPopup(String action, String addition) {
    if (selectedRobot != null) {
      int index = selectedRobot.getId();
      this.currentRobotMovesList.set(index, index + ":" + action + ":" + xClicked + ":" + yClicked + addition);
      this.selectedRobot = null;
    }
    moveSelectionPopup.hide();
    updateMap();
  }

  /**
   * this function adds the preview of the robot list and their respective moves
   *
   * @param serverSettings the lobby server settings so that the correct amount of items is added
   */
  private void changeAllRobots(ServerSettings serverSettings) {
    this.playerRobotList.clear();
    this.currentRobotMovesList.clear();
    // Initialize the list of the robots of the player.
    // Also initializes the default Move of the robots, to wait.
    for (int i = 0; i < serverSettings.getNumberOfRobots(); i++) {
      this.playerRobotList.add("Robot " + i);
      this.currentRobotMovesList.add(i + ":Wait:0:0");
    }
  }

  /**
   * @return vBox containing all the components of the popup
   */
  public HBox getPopupContent() {
    HBox hBox = new HBox();
    VBox movePreview = new VBox();
    VBox cellPreview = new VBox();
    Button buttonMove = new Button("Move");
    Button buttonWait = new Button("Wait");
    Button buttonRequestRadar = new Button("RequestRadar");
    Button buttonRequestTrap = new Button("RequestTrap");
    Button buttonDig = new Button("Dig");
    buttonMove.setOnAction(e -> {
      setActionViaPopup("Move", "");
    });
    buttonWait.setOnAction(e -> {
      setActionViaPopup("Wait", "");
    });
    buttonRequestRadar.setOnAction(e -> {
      setActionViaPopup("RequestRadar", ":Radar");
    });
    buttonRequestTrap.setOnAction(e -> {
      setActionViaPopup("RequestTrap", ":Trap");
    });
    buttonDig.setOnAction(e -> {
      setActionViaPopup("Dig", "");
    });

    buttonMove.setPrefWidth(150);
    buttonWait.setPrefWidth(150);
    buttonRequestRadar.setPrefWidth(150);
    buttonRequestTrap.setPrefWidth(150);
    buttonDig.setPrefWidth(150);
    movePreview.getChildren().add(buttonMove);
    movePreview.getChildren().add(buttonDig);
    movePreview.getChildren().add(buttonRequestRadar);
    movePreview.getChildren().add(buttonRequestTrap);
    movePreview.getChildren().add(buttonWait);
    if (currentGameMap != null) {
      for (GameObject gameObject : currentGameMap.getCellArray()[xClicked][yClicked].getPlacedObjects()) {
        if (gameObject instanceof Nothing) {
          continue;
        }
        HBox box = new HBox();
        Label label = new Label(getObjectDisplayString(gameObject));
        Pane pane = getObjectPreviewPane(gameObject);
        label.setPrefHeight(pane.getHeight());
        label.setPadding(new Insets(pane.getHeight() / 4));
        box.getChildren().add(pane);
        box.getChildren().add(label);
//        label.setText(gameObject.encodeToString());
        cellPreview.getChildren().add(box);
      }
    }


    hBox.getChildren().addAll(movePreview, cellPreview);
    return hBox;
  }


  /**
   * @param gameObject the gameObject for which a pane is being created
   * @return a Pane object that contains the image of the GameObject in question
   */
  public Pane getObjectPreviewPane(GameObject gameObject) {
    Pane out = new Pane();
    if (gameObject instanceof Radar) {
      try {
        out.getChildren().add(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Radar.png")))));
      } catch (Exception e) {
        logger.error("The file \"Radar.png\" does not exist.");
      }
    } else if (gameObject instanceof Trap) {
      try {
        out.getChildren().add(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Trap.png")))));
      } catch (Exception e) {
        logger.error("The file \"Trap.png\" does not exist.");
      }
    } else if (gameObject instanceof Ore) {
      String oreType = currentGameMap.getCellArray()[xClicked][yClicked].oreOnCell().get(0).getOreType().name();
      try {
        out.getChildren().add(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + oreType + " Ore.png")))));
      } catch (Exception e) {
        logger.error("The file \"" + oreType + " Ore.png\" does not exist.");
      }
    } else if (gameObject instanceof Robot) {
      try {
        out.getChildren().add(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Robot.png")))));
      } catch (Exception e) {
        logger.error("The file \"Robot.png\" does not exist.");
      }
    }
    return out;
  }


  public String getObjectDisplayString(GameObject gameObject) {
    StringBuilder stringBuilder = new StringBuilder();

    if (gameObject instanceof Radar) {
      Radar radar = (Radar) gameObject;
      stringBuilder.append("Owner: ").append(radar.getOwner());
    } else if (gameObject instanceof Trap) {
      Trap trap = (Trap) gameObject;
      stringBuilder.append("Owner: ").append(trap.getOwner());
    } else if (gameObject instanceof Ore) {
      Ore ore = (Ore) gameObject;
      stringBuilder.append("Type: ").append(ore.getOreType().name()).append(", Value: ").append(ore.getOreType().getValue());
    } else if (gameObject instanceof Robot) {
      Robot robot = (Robot) gameObject;
      if (robot.getInventory() != null && !(robot.getInventory() instanceof Nothing)) {
        stringBuilder.append(" Carrying: ");
        if (robot.getInventory() instanceof Radar) {
          stringBuilder.append("Radar");
        } else if (robot.getInventory() instanceof Trap) {
          stringBuilder.append("Trap");
        } else if (robot.getInventory() instanceof Ore) {
          stringBuilder.append(((Ore) (robot.getInventory())).getOreType().name()).append("Ore");
        }
      }
    }
    return stringBuilder.toString();
  }

  /**
   * Updates the displayed map.
   */
  public void updateMap() {
    Platform.runLater(
      () -> {
        mapGridPane.getChildren().clear();
        currentGameMap = lobby.getGameMap().getIndividualGameMapForPlayer(client.getNickname());
        if (this.colours.length != this.client.getLobbyInClient().getServerSettings().numberOfRobots.getVal()) {
          this.colours = MathHelper.getRandomColours(this.client.getLobbyInClient().getServerSettings().numberOfRobots.getVal());
        }
        int xMax = currentGameMap.getGameMapSize()[0];
        int yMax = currentGameMap.getGameMapSize()[1];
        double xPixels = mapPane.getWidth();
        double yPixels = mapPane.getHeight();
        double cellSize = (int) Math.min(xPixels / xMax, yPixels / yMax);
        double xPad = (xPixels - xMax*cellSize)/2;
        double yPad = (yPixels - yMax*cellSize)/2;
        mapGridPane.setTranslateX(xPad);
        mapGridPane.setTranslateY(yPad);


        for (int x = 0; x < xMax; x++) {
          for (int y = 0; y < yMax; y++) {
            Button button = new Button();
            button.setPrefSize(cellSize, cellSize);
            button.setPadding(Insets.EMPTY);
            ImageView imageView = new ImageView();
            imageView.setFitHeight(cellSize);
            imageView.setFitWidth(cellSize);
            Image image = null;
            Cell currentCell = currentGameMap.getCellArray()[x][y];
            try {
//              image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Stone Floor.png")));
            } catch (Exception e) {
              logger.error("The file \"Stone Floor.png\" does not exist.");
            }
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
            for (int i = 0; i < this.client.getLobbyInClient().getServerSettings().numberOfRobots.getVal(); i++) {
              Robot curRob = getRobotObjectFromSelection(i);
              if (((curRob.getPosition()[0] == x && curRob.getPosition()[1] == y) || (!currentRobotMovesList.get(curRob.getId()).matches(".*Wait:[0-9]+:[0-9]+") && (Integer.parseInt(currentRobotMovesList.get(curRob.getId()).split(":")[2]) == x && Integer.parseInt(currentRobotMovesList.get(curRob.getId()).split(":")[3]) == y)))) {
                Color col = colours[curRob.getId()];
                float[] hsbValues = new float[3];
                Color.RGBtoHSB(col.getRed(), col.getBlue(), col.getGreen(), hsbValues);
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setContrast(0.1);
                colorAdjust.setHue(hsbValues[0]);
                colorAdjust.setSaturation(hsbValues[1]);
                colorAdjust.setBrightness(0);
                button.setEffect(colorAdjust);
              }
            }
            if (selectedRobot != null && selectedRobot.getPosition()[0] == x && selectedRobot.getPosition()[1] == y) {
              button.setEffect(new Bloom(0.4));
            }
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
    } else {
      try {
        Bounds boundsInScreen = button.localToScreen(button.getBoundsInLocal());
        int xPosition = (int) boundsInScreen.getMaxX();
        int yPosition = (int) boundsInScreen.getMaxY();

        moveSelectionPopup.setX(xPosition);
        moveSelectionPopup.setY(yPosition);
        moveSelectionPopup.getContent().clear();
        moveSelectionPopup.getContent().add(getPopupContent());
        moveSelectionPopup.show(mapGridPane.getScene().getWindow());
      } catch (Exception e) {
        logger.error("Error");
      }
    }

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
   * This method changes the status label of a lobby, once it's game has started
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
   * sends the message from the text field to the Lobby Chat
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

  /**
   * This function is pretty wasteful because it reiterates over everything
   *
   * @param id of the selected Robot
   * @return the robot object on the game map that has this id.
   */
  public Robot getRobotObjectFromSelection(int id) {
    for (int i = 0; i < currentGameMap.getGameMapSize()[0]; i++) {
      for (int j = 0; j < currentGameMap.getGameMapSize()[1]; j++) {
        if (currentGameMap.getCellArray()[i][j].robotsOnCell() == null || currentGameMap.getCellArray()[i][j].robotsOnCell().size() == 0) {
          continue;
        }
        for (Robot r : currentGameMap.getCellArray()[i][j].robotsOnCell()) {
          if (r.getOwner().equals(this.client.getNickname()) && r.getId() == id) {
            return r;
          }
        }
      }
    }
    return null;
  }

  @FXML
  public void onKeyPressedAnchorPane(KeyEvent keyEvent) {
    if (keyEvent.getText().length() != 1) {
      return;
    }
    if (keyEvent.getText().toCharArray()[0] > '0' && keyEvent.getText().toCharArray()[0] <= ('0' + this.client.getLobbyInClient().getServerSettings().getNumberOfRobots())) {
      int id = Integer.parseInt("" + keyEvent.getText().toCharArray()[0]) - 1;
      if (selectedRobot != null) {
        if (selectedRobot.getId() == id) {
          this.selectedRobot = null;
          updateMap();
          return;
        }
      }
      this.selectedRobot = getRobotObjectFromSelection(id);
      updateMap();
    }
  }
}
