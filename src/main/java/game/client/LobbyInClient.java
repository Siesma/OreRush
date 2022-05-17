package game.client;

import game.datastructures.GameMap;
import game.gui.Player;
import game.server.ServerSettings;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The lobbyInClient class is the model for the lobby GUI, it has all the needed meta information about the lobby,
 * as well as the game status, such as the game Map and round number.
 */
public class LobbyInClient {
  /**
   * The name of the lobby
   */
  private final StringProperty lobbyName;
  /**
   * The state of the lobby ('open', 'in game' or 'finished')
   */
  private final StringProperty status;
  /**
   * A list of the player names in a string, seperated by a space
   */
  private final StringProperty players;

  /**
   * getter for the winner information
   *
   * @return the winner name
   */
  public String getWinner() {
    return winner.get();
  }

  /**
   * getter for the winnerProperty
   *
   * @return winnerProperty
   */
  public StringProperty winnerProperty() {
    return winner;
  }

  /**
   * Setter for the winnerProperty
   *
   * @param winner information about the winner
   */
  public void setWinner(String winner) {
    this.winner.set(winner);
  }

  /**
   * property storing the information on the winner
   */
  private final StringProperty winner = new SimpleStringProperty();
  /**
   * The name of the player who´s turn it currently is
   */
  private StringProperty playerOnPlay = new SimpleStringProperty();
  /**
   * The turn counter, increases at the end of each player´s turn
   */
  private StringProperty turnCounter = new SimpleStringProperty();
  /**
   * The last message that was sent in chat
   */
  private final StringProperty lastChatMessage = new SimpleStringProperty();
  /**
   * The list of the names of the players in the lobby
   */
  private final ObservableList<Player> observablePlayerList = FXCollections.observableArrayList();
  /**
   * The GameMap the game will be played on
   */
  private GameMap gameMap = new GameMap(new ServerSettings());

  /**
   * getter for the Game map
   *
   * @return the GameMap´s gameMapProperty
   */
  public int getGameMapProperty() {
    return gameMapProperty.get();
  }

  /**
   * getter for the game map property
   *
   * @return something or another
   */
  public IntegerProperty gameMapPropertyProperty() {
    return gameMapProperty;
  }

  /**
   * Integer property representing the turn counter
   */
  private final IntegerProperty gameMapProperty = new SimpleIntegerProperty(0);

  /**
   * The settings of the game
   */
  private ServerSettings serverSettings;

  /**
   * Constructor for the LobbyInClient. Used in the Client Class to add a lobby.
   *
   * @param lobbyName the name of the lobby
   */
  public LobbyInClient(String lobbyName) {
    this.lobbyName = new SimpleStringProperty(lobbyName);
    this.status = new SimpleStringProperty("open");
    this.players = new SimpleStringProperty();
    this.serverSettings = new ServerSettings();
  }

  /**
   * Updates the GameMap.
   *
   * @param gameMapString The encoded map in form of a string
   */
  public void updateGameMap(String gameMapString) {
    gameMap = GameMap.getMapFromString(gameMapString, serverSettings);
    gameMapProperty.setValue(gameMapProperty.getValue() + 1);
  }

  /**
   * Getter for the Lobby Name
   *
   * @return the Lobby Name
   */
  public String getLobbyName() {
    return lobbyName.getValue();
  }

  /**
   * Setter for the Lobby Name
   *
   * @param lobbyName the new Lobby Name
   */
  public void setLobbyName(String lobbyName) {
    this.lobbyName.set(lobbyName);
  }

  /**
   * Adds a player into the clients lobby view list.
   *
   * @param clientName name of the new player.
   */
  public void addPlayer(String clientName) {
    observablePlayerList.add(new Player(clientName));
    if (players.getValue() == null) {
      players.setValue(clientName);
    } else {
      players.setValue(players.getValue() + " " + clientName);
    }
  }

  /**
   * Returns the data as an observable list of Persons.
   *
   * @return the list of all Players in the lobby
   */
  public ObservableList<Player> getPlayerData() {
    return observablePlayerList;
  }

  /**
   * Sets the StringProperty of the last chat message for proper function in multi threading
   *
   * @return the last chat message
   */
  public StringProperty lastChatMessageProperty() {
    return lastChatMessage;
  }

  /**
   * Setter for the last chat message
   *
   * @param message the last chat message
   */
  public void setLastChatMessage(String message) {
    Platform.runLater(() -> lastChatMessage.setValue(message));
  }

  /**
   * Sets the StringProperty of the lobby name for proper function in multi threading
   *
   * @return the lobby name
   */
  public StringProperty lobbyNameProperty() {
    return lobbyName;
  }

  /**
   * Getter for the lobby status
   *
   * @return the lobby status
   */
  public String getStatus() {
    return status.get();
  }

  /**
   * Setter for the lobby status
   *
   * @param status the lobby status
   */
  public void setStatus(String status) {
    this.status.set(status);
  }

  /**
   * Sets the StringProperty of the status for proper function in multi threading
   *
   * @return the status
   */
  public StringProperty statusProperty() {
    return status;
  }

  /**
   * Getter for the list of players
   *
   * @return the list of players in a String
   */
  public String getPlayers() {
    return players.get();
  }

  /**
   * Setter for the list of players
   *
   * @param players list of players in a String
   */
  public void setPlayers(String players) {
    this.players.set(players);
  }

  /**
   * Sets the StringProperty of the list of players for proper function in multi threading
   *
   * @return the list of players
   */
  public StringProperty playersProperty() {
    return players;
  }

  /**
   * removes Player from observablePlayerList
   *
   * @param playerToDel the player that should be removed from the list
   */
  public void removePlayer(Player playerToDel) {
    players.setValue(players.getValue().replace(playerToDel.getNickname(), ""));
    observablePlayerList.remove(playerToDel);
  }

  /**
   * Getter for the game settings
   *
   * @return the game settings
   */
  public ServerSettings getServerSettings() {
    return serverSettings;
  }

  /**
   * Setter for the game settings
   *
   * @param serverSettings the game settings
   */
  public void setServerSettings(ServerSettings serverSettings) {
    this.serverSettings = serverSettings;
  }

  /**
   * Getter for the game map
   *
   * @return the game map
   */
  public GameMap getGameMap() {
    return gameMap;
  }

  /**
   * Getter of current player who´s turn it is
   *
   * @return the current player who´s turn it is
   */
  public String getPlayerOnPlay() {
    return playerOnPlay.get();
  }

  /**
   * Sets the StringProperty of the current player who´s turn it is for proper function in multi threading
   *
   * @return the current player who´s turn it is
   */
  public StringProperty playerOnPlayProperty() {
    return playerOnPlay;
  }

  /**
   * Setter of current player who´s turn it is
   *
   * @param playerOnPlay the current player who´s turn it is
   */
  public void setPlayerOnPlay(String playerOnPlay) {
    this.playerOnPlay.set(playerOnPlay);
  }

  /**
   * Getter of the turn counter
   *
   * @return the current turn count in a string
   */
  public String getTurnCounter() {
    return turnCounter.get();
  }

  /**
   * Sets the StringProperty of the turn counter for proper function in multi threading
   *
   * @return the count of turns
   */
  public StringProperty turnCounterProperty() {
    return turnCounter;
  }

  /**
   * Setter of the turn counter
   *
   * @param turnCounter new turn count in a string
   */
  public void setTurnCounter(String turnCounter) {
    this.turnCounter.set(turnCounter);
  }
}
