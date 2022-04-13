package game.server;

import game.client.Client;
import game.datastructures.GameMap;
import game.datastructures.GameObject;

import java.util.ArrayList;

public class Lobby {

    private final String lobbyName;
    protected ArrayList<ClientThread> listOfClients = new ArrayList<>();
    protected GameMap gameMap;
    protected ServerSettings serverSettings; //No functionality yet
    protected int turnCounter;

    public Lobby(String lobbyName, ClientThread clientThread) {
        this.lobbyName = lobbyName;
    }

    public ClientThread getHost() {
        return listOfClients.get(0);
    }

    private void generateGameMap() {
        gameMap = new GameMap(serverSettings.getMapWidth(), serverSettings.getMapHeight(), serverSettings.getOreDensity());
    }

    public void addClient(ClientThread clientThread) {
        listOfClients.add(clientThread);
    }

    public String getLobbyName() {
        return lobbyName;
    }

}
