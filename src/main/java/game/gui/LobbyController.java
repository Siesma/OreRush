package game.gui;

import game.client.Client;
import game.client.LobbyInClient;
import game.datastructures.Cell;
import game.datastructures.Robot;
import game.datastructures.*;
import game.helper.MathHelper;
import game.packet.PacketHandler;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
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

  @FXML
  private Button endTurnButton;

  public static final Logger logger = LogManager.getLogger(LobbyController.class);

  @FXML
  private Label turnInfoLabel;
  @FXML
  private Label playerTurnLabel;
  @FXML
  private VBox gameVBox;
  private int xClicked = -1;
  private int yClicked = -1;

  Popup moveSelectionPopup;

  private Robot selectedRobot;

  private GameMap currentGameMap;

  private Color[] colours;


  private Popup tutorialPopup = new Popup();

  private int tutorialRobotID = -1;

  private int tutorialCounter = 0;

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
    lobby.winnerProperty().addListener((obs, oldVal, newVal) -> {
      gameVBox.getChildren().clear();
      Label winnerLabel = new Label("The winner is: " + newVal);
      winnerLabel.setFont(new Font(32));
      gameVBox.getChildren().add(winnerLabel);
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
    if(tutorialPopup.isShowing()) {
      tutorialPopup.hide();
      updateMap();
    }
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
//    Button cheat = new Button("CHEAT");
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

//    cheat.setOnAction(e -> {
//      this.client.setScoreWithCheats(100 + (int) (Math.random() * 400));
//      cheat();
//    });

    buttonMove.setPrefWidth(150);
    buttonWait.setPrefWidth(150);
    buttonRequestRadar.setPrefWidth(150);
    buttonRequestTrap.setPrefWidth(150);
    buttonDig.setPrefWidth(150);
//    cheat.setPrefWidth(150);
    movePreview.getChildren().add(buttonMove);
    movePreview.getChildren().add(buttonDig);
    movePreview.getChildren().add(buttonRequestRadar);
    movePreview.getChildren().add(buttonRequestTrap);
    movePreview.getChildren().add(buttonWait);
//    movePreview.getChildren().add(cheat);
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

  /**
   * Returns the string containing object information
   *
   * @param gameObject
   * @return a string specifying the owner, type for radar, traps, additionnally the value for ores or what it is
   * carrying in case of a robot
   */
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
        mapGridPane.setTranslateX(getCellSizeAndPosition().get("xPad"));
        mapGridPane.setTranslateY(getCellSizeAndPosition().get("yPad"));


        // goes through all the rows and columns and sets the important background image.
        for (int x = 0; x < getCellSizeAndPosition().get("xMax"); x++) {
          for (int y = 0; y < getCellSizeAndPosition().get("yMax"); y++) {
            Button button = new Button();
            button.setPrefSize(getCellSizeAndPosition().get("cellSize"), getCellSizeAndPosition().get("cellSize"));
            button.setPadding(Insets.EMPTY);
            ImageView imageView = new ImageView();
            imageView.setFitHeight(getCellSizeAndPosition().get("cellSize"));
            imageView.setFitWidth(getCellSizeAndPosition().get("cellSize"));
            Image image = null;
            Cell currentCell = currentGameMap.getCellArray()[x][y];
            try {
              image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Stone Floor.png")));
            } catch (Exception e) {
              logger.error("The file \"Stone Floor.png\" does not exist.");
            }
            if (currentCell.robotsOnCell() != null) {
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
            // adds the listeners to the popup buttons to update the robots.
            button.setOnMouseClicked((MouseEvent event) -> {
              Node node = (Node) event.getTarget();
              int row = GridPane.getRowIndex(node);
              int column = GridPane.getColumnIndex(node);
              if (selectedRobot == null)
                return;
              selectedAndMakeActionForRobot(button, column, row);
            });
            button.setOnAction(e -> {
              Node node = (Node) e.getTarget();
              int row = GridPane.getRowIndex(node);
              int column = GridPane.getColumnIndex(node);
              setRobotMove(button, column, row);
            });
            for (int i = 0; i < this.client.getLobbyInClient().getServerSettings().numberOfRobots.getVal(); i++) {
              Robot curRob = getRobotObjectFromSelection(i);
              if (curRob == null) {
                continue;
              }
              if (((curRob.getPosition()[0] == x && curRob.getPosition()[1] == y) || (!currentRobotMovesList.get(curRob.getId()).matches(".*Wait:[0-9]+:[0-9]+") && (Integer.parseInt(currentRobotMovesList.get(curRob.getId()).split(":")[2]) == x && Integer.parseInt(currentRobotMovesList.get(curRob.getId()).split(":")[3]) == y)))) {
                Color col = colours[curRob.getId()];
                float[] hsbValues = new float[3];
                Color.RGBtoHSB(col.getRed(), col.getBlue(), col.getGreen(), hsbValues);
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setContrast(0);
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
   * @param x the x coordinate of this button in terms of the gameMap's cellArray
   * @param y the y coordinate of this button in terms of the gameMap's cellArray
   */
  private void setRobotMove(Button button, int x, int y) {
    this.selectedAndMakeActionForRobot(button, x, y);
  }

  /**
   * A function that displays a popup in case someone was cheating.
   */
  public void cheat() {
    Popup popup = new Popup();
    Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bsod.png")));
    ImageView iw = new ImageView(image);
    Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) size.getWidth();
    int height = (int) size.getHeight();
    double scaleX = width / image.getWidth();
    double scaleY = height / image.getHeight();
    popup.setX(0);
    iw.setScaleX(scaleX);
    iw.setScaleY(scaleY);
    popup.getContent().add(iw);
    popup.show(anchorPane.getScene().getWindow());
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

    // ignores this call if the position is out of bounds anyway.
    if (!MathHelper.isInBounds(x, y, this.client.getLobbyInClient().getServerSettings())) {
      return;
    }

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
        // displays the popup at the position of the mouse.
        Bounds boundsInScreen = button.localToScreen(button.getBoundsInLocal());
        int xPosition = (int) boundsInScreen.getMaxX();
        int yPosition = (int) boundsInScreen.getMaxY();

        moveSelectionPopup.setX(xPosition);
        moveSelectionPopup.setY(yPosition);
        moveSelectionPopup.getContent().clear();
        moveSelectionPopup.getContent().add(getPopupContent());
        moveSelectionPopup.show(mapGridPane.getScene().getWindow());
      } catch (Exception e) {
        logger.error("Failed to create a preview for the popup because of the x and y being out of bounds.");
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
    if(newLobbyMessageTextField.getText().matches("SET_SCORE [0-9]+")) {
      try {
        client.setScoreWithCheats(Integer.parseInt(newLobbyMessageTextField.getText().split(" ")[1]));
        cheat();
      } catch (Exception e) {
        logger.debug("The cheating image was missing");
        return;
      }
      return;
    } else if (newLobbyMessageTextField.getText().equals("END_GAME")) {
      try {
        client.closeGame();
        cheat();
      } catch (Exception e) {
        logger.debug("There was an error trying to prematurely ending the game");
        return;
      }
    }
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


  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
  @FXML
  void onMouseReleasedMapHeight(MouseEvent event) {
    this.labelMapHeight.setText("Map Height: " + this.sliderMapHeight.getValue());
    this.client.sendServerSettings("mapHeight:" + (int) this.sliderMapHeight.getValue());
  }

  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
  @FXML
  void onMouseReleasedMapWidth(MouseEvent event) {
    this.labelMapWidth.setText("Map Width: " + this.sliderMapWidth.getValue());
    this.client.sendServerSettings("mapWidth:" + (int) this.sliderMapWidth.getValue());
  }

  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
  @FXML
  void onMouseReleasedMaxAllowedMoves(MouseEvent event) {
    this.labelMaxAllowedMoves.setText("Max Allowed Moves: " + this.sliderMaxAllowedMoves.getValue());
    this.client.sendServerSettings("maxAllowedMoves:" + (int) this.sliderMaxAllowedMoves.getValue());
  }

  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
  @FXML
  void onMouseReleasedMaxClusterSize(MouseEvent event) {
    this.labelMaxClusterSize.setText("Max Cluster Size: " + this.sliderMaxClusterSize.getValue());
    this.client.sendServerSettings("maxClusterSize:" + (int) this.sliderMaxClusterSize.getValue());
  }

  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
  @FXML
  void onMouseReleasedNumberOfRobots(MouseEvent event) {
    this.labelNumberOfRobots.setText("Number Of Robots: " + this.sliderNumberOfRobots.getValue());
    this.client.sendServerSettings("numberOfRobots:" + (int) this.sliderNumberOfRobots.getValue());
  }

  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
  @FXML
  void onMouseReleasedOreDensity(MouseEvent event) {
    this.labelOreDensity.setText("Ore Density: " + this.sliderOreDensity.getValue());
    this.client.sendServerSettings("oreDensity:" + this.sliderOreDensity.getValue());
  }

  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
  @FXML
  void onMouseReleasedOreThreshold(MouseEvent event) {
    this.labelOreThreshold.setText("Ore Threshold: " + this.sliderOreThreshold.getValue());
    this.client.sendServerSettings("oreThreshold:" + this.sliderOreThreshold.getValue());
  }

  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
  @FXML
  void onMouseReleasedRadarDistance(MouseEvent event) {
    this.labelRadarDistance.setText("Radar Distance: " + this.sliderRadarDistance.getValue());
    this.client.sendServerSettings("radarDistance:" + (int) this.sliderRadarDistance.getValue());
  }

  /**
   * This function sets the label next to the associated setting to the current value
   *
   * @param event unused mouseEvent that triggers this function
   */
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

  /**
   * This function will display a tutorial step at a time when pressing the "Tutorial" Button.
   * @param e unused ActionEvent that triggers this function
   */
  @FXML
  public void onActionTutorialButton(ActionEvent e) {
    this.tutorialPopup.getContent().clear();
    tutorialPopup.hide();
    switch (tutorialCounter) {
      case 0:
        showRobotSelection();
        break;
      case 1:
        showCellSelection();
        break;
      case 2:
        showOptionSelection();
        break;
      case 3:
        showEndTurnSelection();
        break;
      case 4:
        showObjectiveSelection();
        break;
    }
    tutorialCounter++;
    tutorialCounter = tutorialCounter % 6;
  }

  /**
   * This function displays the first Tutorial stage, the stage at which the robot is selected.
   */
  private void showRobotSelection() {
    int randomId = (int) (Math.random() * this.client.getLobbyInClient().getServerSettings().numberOfRobots.getVal());
    this.tutorialRobotID = randomId;
    Label label = new Label();
    label.setText("Click this Robot or press the \"" + (randomId + 1) + "\" key to select.");
    double x = getCellSizeAndPosition().get("xPad") + getRobotObjectFromSelection(randomId).getPosition()[0] * getCellSizeAndPosition().get("cellSize") + getCellSizeAndPosition().get("cellSize");
    double y = getCellSizeAndPosition().get("yPad") + getRobotObjectFromSelection(randomId).getPosition()[1] * getCellSizeAndPosition().get("cellSize") + getCellSizeAndPosition().get("cellSize");
    int arrowX = 20;
    int arrowY = 20;
    tutorialPopup.setX(getCellSizeAndPosition().get("boundsMinX") + x + arrowX);
    tutorialPopup.setY(getCellSizeAndPosition().get("boundsMinY") + y + arrowY);
    Arrow arrow = new Arrow(0, 0, -arrowX, -arrowY, 10);

    Button button = new Button("Got it");
    button.setOnAction(e -> {
      if (tutorialPopup.isShowing()) {
        tutorialPopup.getContent().clear();
        tutorialPopup.hide();
      }
    });
    VBox box = new VBox();
    box.getChildren().add(button);
    box.getChildren().add(label);
    tutorialPopup.getContent().add(box);
    tutorialPopup.getContent().add(arrow);
    tutorialPopup.show(anchorPane.getScene().getWindow());
    this.selectedRobot = getRobotObjectFromSelection(tutorialRobotID);
  }

  /**
   * This function displays the second Tutorial stage, the stage at which a cell has to be clicked in order to move the robot.
   */
  private void showCellSelection() {

    Label label = new Label();
    label.setText("Click (this) Cell to show the options you have");
    xClicked = (int) (Math.random() * this.client.getLobbyInClient().getServerSettings().getMapWidth());
    yClicked = (int) (Math.random() * this.client.getLobbyInClient().getServerSettings().getMapHeight());

    double x = getCellSizeAndPosition().get("xPad") + xClicked * getCellSizeAndPosition().get("cellSize") + getCellSizeAndPosition().get("cellSize");
    double y = getCellSizeAndPosition().get("yPad") + yClicked * getCellSizeAndPosition().get("cellSize") + getCellSizeAndPosition().get("cellSize");
    int arrowX = 20;
    int arrowY = 20;
    tutorialPopup.setX(getCellSizeAndPosition().get("boundsMinX") + x + arrowX);
    tutorialPopup.setY(getCellSizeAndPosition().get("boundsMinY") + y + arrowY);
    Arrow arrow = new Arrow(0, 0, -arrowX, -arrowY, 10);

    Button button = new Button("Got it");
    button.setOnAction(e -> {
      if (tutorialPopup.isShowing()) {
        tutorialPopup.getContent().clear();
        tutorialPopup.hide();
      }
    });
    VBox box = new VBox();
    box.getChildren().add(button);
    box.getChildren().add(label);
    tutorialPopup.getContent().add(box);
    tutorialPopup.getContent().add(arrow);
    tutorialPopup.show(anchorPane.getScene().getWindow());
  }

  /**
   * This function displays the third Tutorial stage, the stage at which a cell has been clicked
   * and the user is prompted to decide which option to perform.
   */
  private void showOptionSelection() {
    Label label = new Label();
    label.setText("These are all the Options you have");
    double x = getCellSizeAndPosition().get("xPad") + xClicked * getCellSizeAndPosition().get("cellSize") + getCellSizeAndPosition().get("cellSize");
    double y = getCellSizeAndPosition().get("yPad") + yClicked * getCellSizeAndPosition().get("cellSize") + getCellSizeAndPosition().get("cellSize");
    int arrowX = 20;
    int arrowY = 20;
    tutorialPopup.setX(getCellSizeAndPosition().get("boundsMinX") + x + arrowX);
    tutorialPopup.setY(getCellSizeAndPosition().get("boundsMinY") + y + arrowY);

    Button button = new Button("Got it");
    button.setOnAction(e -> {
      if (tutorialPopup.isShowing()) {
        tutorialPopup.getContent().clear();
        tutorialPopup.hide();
      }
    });
    HBox hBox = new HBox();
    VBox optionBox = new VBox();
    optionBox.getChildren().add(getPopupContent());
    optionBox.getChildren().add(button);
    optionBox.getChildren().add(label);
    hBox.getChildren().add(optionBox);

    VBox labelBox = new VBox();


    Label labelMove = new Label("Move lets your Robot move to this location");
    Label labelWait = new Label("Wait lets your Robot move to this location and then wait.");
    Label labelRequestRadar = new Label("RequestRadar lets your Robot request a Radar. This will only work if your xPosition is 0.");
    Label labelRequestTrap = new Label("RequestTrap lets your Robot request a Trap. This will only work if your xPosition is 0.");
    Label labelDig = new Label("Dig lets your Robot move to this location if its not on the cell already, if it is however the Robot will dig up whatever ore there is on this cell and place its Inventory on this cell.");

    labelMove.setMinHeight(25);
    labelWait.setMinHeight(25);
    labelRequestRadar.setMinHeight(25);
    labelRequestTrap.setMinHeight(25);
    labelDig.setMinHeight(25);

    labelBox.getChildren().add(labelMove);
    labelBox.getChildren().add(labelDig);
    labelBox.getChildren().add(labelRequestRadar);
    labelBox.getChildren().add(labelRequestTrap);
    labelBox.getChildren().add(labelWait);
    hBox.getChildren().add(labelBox);
    tutorialPopup.getContent().add(hBox);
    tutorialPopup.show(anchorPane.getScene().getWindow());

  }

  /**
   * This function displays the fourth Tutorial stage, the stage at which the user should end their turn to proceed.
   */
  private void showEndTurnSelection () {
    Label label = new Label();
    label.setText("End your turn over here!");
    Bounds boundsInScreen = endTurnButton.localToScreen(endTurnButton.getBoundsInLocal());
    tutorialPopup.setX(boundsInScreen.getMaxX() + 5);
    tutorialPopup.setY(boundsInScreen.getMaxY() + 5);
    int arrowX = 20;
    int arrowY = 20;
    Arrow arrow = new Arrow(0, 0, -arrowX, -arrowY, 10);

    Button button = new Button("Got it");
    button.setOnAction(e -> {
      if (tutorialPopup.isShowing()) {
        tutorialPopup.getContent().clear();
        tutorialPopup.hide();
      }
    });
    VBox box = new VBox();
    box.getChildren().add(button);
    box.getChildren().add(label);

    tutorialPopup.getContent().add(box);
    tutorialPopup.getContent().add(arrow);

    tutorialPopup.show(anchorPane.getScene().getWindow());
  }

  /**
   * This function displays the fifth Tutorial stage, the stage at which the robot may has something in their
   * inventory and should return to the start area to increase their score.
   */
  private void showObjectiveSelection () {
    Label label = new Label();
    label.setText("When this robot has something in its inventory you have to go back to the first colum (x=0) to obtain points!");
    double x = getCellSizeAndPosition().get("xPad") + getRobotObjectFromSelection(tutorialRobotID).getPosition()[0] * getCellSizeAndPosition().get("cellSize") + getCellSizeAndPosition().get("cellSize");
    double y = getCellSizeAndPosition().get("yPad") + getRobotObjectFromSelection(tutorialRobotID).getPosition()[1] * getCellSizeAndPosition().get("cellSize") + getCellSizeAndPosition().get("cellSize");
    int arrowX = 20;
    int arrowY = 20;
    tutorialPopup.setX(getCellSizeAndPosition().get("boundsMinX") + x + arrowX);
    tutorialPopup.setY(getCellSizeAndPosition().get("boundsMinY") + y + arrowY);
    Arrow arrow = new Arrow(0, 0, -arrowX, -arrowY, 10);

    Button button = new Button("Got it");
    button.setOnAction(e -> {
      if (tutorialPopup.isShowing()) {
        tutorialPopup.getContent().clear();
        tutorialPopup.hide();
      }
    });
    VBox box = new VBox();
    box.getChildren().add(button);
    box.getChildren().add(label);
    tutorialPopup.getContent().add(box);
    tutorialPopup.getContent().add(arrow);
    tutorialPopup.show(anchorPane.getScene().getWindow());
    this.selectedRobot = getRobotObjectFromSelection(tutorialRobotID);
  }

  /**
   * This function gets the individual game size and scale and returns it as a hashmap.
   * @return a hashmap containing the size, scale and padding information of the current display
   */
  private HashMap<String, Double> getCellSizeAndPosition() {

    int xMax = currentGameMap.getGameMapSize()[0];
    int yMax = currentGameMap.getGameMapSize()[1];
    double xPixels = mapPane.getWidth();
    double yPixels = mapPane.getHeight();
    double cellSize = (int) Math.min(xPixels / xMax, yPixels / yMax);
    double xPad = (xPixels - xMax * cellSize) / 2;
    double yPad = (yPixels - yMax * cellSize) / 2;
    Bounds boundsInScreen = anchorPane.localToScreen(anchorPane.getBoundsInLocal());
    HashMap<String, Double> map = new HashMap<>();
    map.put("xMax", (double) xMax);
    map.put("yMax", (double) yMax);
    map.put("cellSize", cellSize);
    map.put("xPad", xPad);
    map.put("yPad", yPad);
    map.put("boundsMinX", boundsInScreen.getMinX());
    map.put("boundsMinY", boundsInScreen.getMinY());
    map.put("boundsMaxX", boundsInScreen.getMaxX());
    map.put("boundsMaxY", boundsInScreen.getMaxY());

    return map;
  }

  @SuppressWarnings("unused")
  @FXML
  public void onMouseClickedAnchorPane(MouseEvent e) {

  }

  /**
   * allows to select a robot with a key stroke if it is formt 1 to 9
   *
   * @param keyEvent keyboard key pressed
   */
  @FXML
  public void onKeyPressedAnchorPane(KeyEvent keyEvent) {
    if (currentGameMap == null) {
      return;
    }
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

