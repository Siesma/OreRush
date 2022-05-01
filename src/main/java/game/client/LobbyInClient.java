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
    private final StringProperty lobbyName;
    private final StringProperty status;
    private final StringProperty players;

    private StringProperty playerOnPlay =  new SimpleStringProperty();

    private StringProperty turnCounter =  new SimpleStringProperty();

    private final StringProperty lastChatMessage = new SimpleStringProperty();
    private final ObservableList<Player> observablePlayerList = FXCollections.observableArrayList();

    private GameMap gameMap = new GameMap(new ServerSettings(""));

    public int getGameMapProperty() {
        return gameMapProperty.get();
    }

    public IntegerProperty gameMapPropertyProperty() {
        return gameMapProperty;
    }

    private final IntegerProperty gameMapProperty = new SimpleIntegerProperty(0);

    public LobbyInClient(String lobbyName) {
        this.lobbyName = new SimpleStringProperty(lobbyName);
        this.status = new SimpleStringProperty("open");
        this.players = new SimpleStringProperty();
    }

    public void updateGameMap(Object parent, String gameMapString) {
        gameMap = GameMap.getMapFromString(gameMapString);
        gameMapProperty.setValue(gameMapProperty.getValue()+1);
    }

    public String getLobbyName() {
        return lobbyName.getValue();
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName.set(lobbyName);
    }

    /**
     * Adds a player into the clients lobby view list.
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

    public StringProperty lastChatMessageProperty() {
        return lastChatMessage;
    }

    public void setLastChatMessage(String message) {
        Platform.runLater(() -> lastChatMessage.setValue(message));
    }

    public StringProperty lobbyNameProperty() {
        return lobbyName;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getPlayers() {
        return players.get();
    }

    public void setPlayers(String players) {
        this.players.set(players);
    }

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

    public GameMap getGameMap() {
        return gameMap;
    }

    public String getPlayerOnPlay() {
        return playerOnPlay.get();
    }

    public StringProperty playerOnPlayProperty() {
        return playerOnPlay;
    }

    public void setPlayerOnPlay(String playerOnPlay) {
        this.playerOnPlay.set(playerOnPlay);
    }

    public String getTurnCounter() {
        return turnCounter.get();
    }

    public StringProperty turnCounterProperty() {
        return turnCounter;
    }

    public void setTurnCounter(String turnCounter) {
        this.turnCounter.set(turnCounter);
    }
}
