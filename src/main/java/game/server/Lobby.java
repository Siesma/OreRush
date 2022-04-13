package game.server;

import game.datastructures.GameMap;
import game.datastructures.GameObject;

import java.util.ArrayList;

public class Lobby {
    protected ArrayList<ClientThread> listOfClients = new ArrayList<>();
    protected GameMap gameMap;
    protected ServerSettings serverSettings; //No functionality yet
    protected int turnCounter;

    public ClientThread getHost() {
        return listOfClients.get(0);
    }

    private void generateGameMap() {
        gameMap = new GameMap(serverSettings.getMapWidth(), serverSettings.getMapHeight(), serverSettings.getOreDensity());
    }
}
