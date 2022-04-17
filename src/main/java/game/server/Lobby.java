package game.server;

import game.client.Client;
import game.datastructures.GameMap;
import game.datastructures.GameObject;
import game.datastructures.Robot;

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
        gameMap = new GameMap(serverSettings.getMapWidth(), serverSettings.getMapHeight(), serverSettings);
    }

    /**
     *
     * Returns the amount of single cell moves that would have to be done to reach a destination
     */
    private int distanceFromPosition (int[] now, int[] then) {
        return Math.abs(now[0] - then[0]) + Math.abs(now[1] - then[1]);
    }

    /**
     *
     * Returns the position the robot is allowed to move to relative to the maximum allowed moves.
     * X-Coordinate will be prioritised if the wanted destination is not a valid move.
     *
     * This function can be used to not have to validate moves as every invalid move will automatically will be
     * cropped down.
     */
    public int[] getNextMove (Robot r, int[] destination) {
        int xDif = (r.getPosition()[0] - destination[0]);
        int yDif = (r.getPosition()[1] - destination[1]);
        int xMovesMade = Math.min(serverSettings.getMaxAllowedMoves(), xDif);
        int maxYMoves = Math.min(serverSettings.getMaxAllowedMoves() - xMovesMade, Math.min(serverSettings.getMaxAllowedMoves(), yDif));
        return new int[] {r.getPosition()[0] - xMovesMade, r.getPosition()[1] - maxYMoves};
    }

    /**
     *
     * Returns the players ID of whoms turn it is
     */
    public int turnOfPlayer () {
        return turnCounter % listOfClients.size();
    }
    public void addClient(ClientThread clientThread) {
        listOfClients.add(clientThread);
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public ServerSettings getServerSettings() {
        return serverSettings;
    }

    public void startGame() {
        generateGameMap();
        spawnRobots();
    }

    public void spawnRobots() {
        for (int i = 0; i < listOfClients.size(); i++) {
            for (int j = 0; j < serverSettings.getNumberOfRobots(); j++) {
                listOfClients.get(i).addRobot();
            }
        }
    }
}
