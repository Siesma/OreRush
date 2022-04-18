package game.client;

import game.gui.Player;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LobbyInClient {


    private final String name;

    private final StringProperty lastChatMessage = new SimpleStringProperty();
    private final ObservableList<Player> observablePlayerList = FXCollections.observableArrayList();



    public LobbyInClient(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addPlayer(String clientName) {
        observablePlayerList.add(new Player(clientName));
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

}
