package game.server;

import game.datastructures.GameMap;
import game.datastructures.Robot;
import game.packet.PacketHandler;
import game.packet.packets.Update;

import java.util.ArrayList;

public class Lobby {

    private final String lobbyName;
    protected ArrayList<ClientThread> listOfClients = new ArrayList<>();
    protected GameMap gameMap;
    protected ServerSettings serverSettings; // just basic functionality to set constants, not modifiable yet.
    protected int turnCounter;

    public Lobby(String lobbyName, ClientThread clientThread) {
        this.lobbyName = lobbyName;
        this.serverSettings = new ServerSettings("");
        startGame();
    }

    public ClientThread getHost() {
        return listOfClients.get(0);
    }

    private void generateGameMap() {
        gameMap = new GameMap(serverSettings.getMapWidth(), serverSettings.getMapHeight(), serverSettings);
        gameMap.spawnOreInMap();
    }

    /**
     * Returns the amount of single cell moves that would have to be done to reach a destination
     */
    public int distanceFromPosition(int[] now, int[] then) {
        return Math.abs(now[0] - then[0]) + Math.abs(now[1] - then[1]);
    }

    /**
     * Returns the position the robot is allowed to move to relative to the maximum allowed moves.
     * X-Coordinate will be prioritised if the wanted destination is not a valid move.
     * <p>
     * This function can be used to not have to validate moves as every invalid move will automatically will be
     * cropped down.
     */
    public int[] getNextMove(Robot r, int[] destination) {
        System.out.println("Trying to get the new position for the robot");
        System.out.println("From: " + r.getPosition()[0] + " " + r.getPosition()[1] + "; to: " + destination[0] + " " + destination[1] );
        int xDif = (destination[0] - r.getPosition()[0]);
        int yDif = (destination[1] - r.getPosition()[1]);
        int xMoves = Math.min(serverSettings.getMaxAllowedMoves(), xDif);
        int yMoves = Math.min(serverSettings.getMaxAllowedMoves() - xMoves, yDif);
        return new int[] { r.getPosition()[0] + xMoves, r.getPosition()[1] + yMoves };
    }

    /**
     * Returns the players ID of whoms turn it is
     */
    public int turnOfPlayer() {
        return turnCounter % listOfClients.size();
    }

    public void updateMove () {
        turnCounter++;
        System.out.println("---");
        this.gameMap.printMapToConsole();
        System.out.println("---");
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

    public GameMap getGameMap() {
        return gameMap;
    }

    public ArrayList<ClientThread> getListOfClients() {
        return listOfClients;
    }

    public void startGame() {
        spawnRobots();
        generateGameMap();
    }

    public void spawnRobots() {
        for (int i = 0; i < listOfClients.size(); i++) {
            for (int j = 0; j < serverSettings.getNumberOfRobots(); j++) {
                listOfClients.get(i).addRobot();
            }
        }
    }
}
