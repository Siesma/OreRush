package game.server;

import game.datastructures.GameMap;
import game.datastructures.Robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Lobby {

  private final String lobbyName;
  protected ArrayList<ClientThread> listOfClients = new ArrayList<>();
  protected GameMap gameMap;
  protected ServerSettings serverSettings; // just basic functionality to set constants, not modifiable yet.
  protected int turnCounter;
  public static final Logger logger = LogManager.getLogger(Server.class);

  public Lobby(String lobbyName, ClientThread clientThread) {
    this.lobbyName = lobbyName;
    this.serverSettings = new ServerSettings("");
    initialize();
  }

  public ClientThread getHost() {
    return listOfClients.get(0);
  }

  private void generateGameMap() {
    gameMap = new GameMap(serverSettings.getMapWidth(), serverSettings.getMapHeight(), serverSettings);
    gameMap.spawnOreInMap();
  }

  /**
   * @param now current position
   * @param then destination
   * @return the amount of single cell moves that would need to be done to reach a destination
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
   *
   * @param r //TODO: Tom was soll hier rein?
   * @param destination //TODO: Tom was soll hier rein?
   * @return //TODO: Tom was soll hier rein?
   */
  public int[] getNextMove(Robot r, int[] destination) {
    logger.debug("Trying to get the new position for the robot");
    logger.debug("From: " + r.getPosition()[0] + " " + r.getPosition()[1] + "; to: " + destination[0] + " " + destination[1]);
    int xDif = (destination[0] - r.getPosition()[0]);
    int yDif = (destination[1] - r.getPosition()[1]);
    int xMoves = Math.min(serverSettings.getMaxAllowedMoves(), xDif);
    int yMoves = Math.min(serverSettings.getMaxAllowedMoves() - xMoves, yDif);
    logger.debug("Due to the max allowed cells that someone can move the robot is now at " + (r.getPosition()[0] + xMoves) + " " + (r.getPosition()[1] + yMoves));
    return new int[]{r.getPosition()[0] + xMoves, r.getPosition()[1] + yMoves};
  }

    /**
     *
     * Returns the players ID of whoms turn it is
     * @return the id of the player who's turn it is.
     */
    public int turnOfPlayer () {
        return turnCounter % listOfClients.size();
    }
    public void addClient(ClientThread clientThread) {
        listOfClients.add(clientThread);
    }
    public void removeClient(ClientThread clientThread) {
        listOfClients.remove(clientThread);
    }

  public void updateMove() {
    turnCounter++;
    printMap();
  }

  public void printMap () {
    System.out.println("---");
    this.gameMap.printMapToConsole();
    System.out.println("---");
  }

  public int getTurnCounter() {
    return turnCounter;
  }

  public int getIDOfClient (ClientThread clientThread) {
    for(int i = 0; i < listOfClients.size(); i++) {
      ClientThread c = listOfClients.get(i);
      if(c.getPlayerName().equals(clientThread.getPlayerName())) {
        return i;
      }
    }
    return -1;
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

  public void initialize() {
    spawnRobots();
    generateGameMap();
    printMapForEveryone();
  }
  public void printMapForEveryone () {
    for(ClientThread c : getListOfClients()) {
      c.updatePlayersAboutMapChanges();
    }
  }

  public void spawnRobots() {
    for (int i = 0; i < listOfClients.size(); i++) {
      for (int j = 0; j < serverSettings.getNumberOfRobots(); j++) {
        listOfClients.get(i).addRobot();
      }
    }
  }
}
