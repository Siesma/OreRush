package game.client;

import game.gui.Player;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LobbyInClient {
    private final StringProperty lobbyName;
    private final StringProperty status;
    private final StringProperty players;

    private final StringProperty lastChatMessage = new SimpleStringProperty();
    private final ObservableList<Player> observablePlayerList = FXCollections.observableArrayList();



    public LobbyInClient(String lobbyName) {
        this.lobbyName = new SimpleStringProperty(lobbyName);
        this.status = new SimpleStringProperty("open");
        this.players = new SimpleStringProperty();
    }

    public String getLobbyName() {
        return lobbyName.getValue();
    }

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
     * @return
     */
    public ObservableList<Player> getPlayerData() {
        return observablePlayerList;
    }

    public StringProperty lastChatMessageProperty() { return lastChatMessage;}

    public void setLastChatMessage(String message) {
        Platform.runLater(() -> lastChatMessage.setValue(message));
    }

    public StringProperty lobbyNameProperty() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName.set(lobbyName);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getPlayers() {
        return players.get();
    }

    public StringProperty playersProperty() {
        return players;
    }

    public void setPlayers(String players) {
        this.players.set(players);
    }
    /**
    *removes Player from observablePlayerList
     */
    public void removePlayer(Player playerToDel) {
        players.setValue(players.getValue().replace(playerToDel.getNickname(),""));
        observablePlayerList.remove(playerToDel);

    }
}
