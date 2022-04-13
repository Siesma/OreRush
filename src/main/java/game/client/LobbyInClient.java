package game.client;

import java.util.ArrayList;

public class LobbyInClient {


    private final String name;
    private final ArrayList<String> playerArrayList = new ArrayList<>();


    public LobbyInClient(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addPlayer(String clientName) {
        playerArrayList.add(clientName);
    }
}
