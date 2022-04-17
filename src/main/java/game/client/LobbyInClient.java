package game.client;

import game.gui.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LobbyInClient {


    private final String name;


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
}
