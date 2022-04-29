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
    gameMap = new GameMap(serverSettings);
    gameMap.spawnOreInMap();
  }


  /**
   * Returns the position the robot is allowed to move to relative to the maximum allowed moves.
   * X-Coordinate will be prioritised if the wanted destination is not a valid move.
   * <p>
   * This function can be used to not have to validate moves as every invalid move will automatically will be
   * cropped down.
   *
   * @param r is the Robot in question that tries to move.
   * @param destination is the Position to which the robot in question wants to move to. Expecteto be two integers.
   * @return the next Position that is within the reach of the robots original position without moving more than allowed.
   */
  public int[] getNextMove(Robot r, int[] destination) {
//    int xDif = (destination[0] - r.getPosition()[0]);
//    int yDif = (destination[1] - r.getPosition()[1]);
//    int xMoves = Math.min(serverSettings.getMaxAllowedMoves(), xDif);
//    int yMoves = Math.min(serverSettings.getMaxAllowedMoves() - xMoves, yDif);
//    return new int[]{r.getPosition()[0] + xMoves, r.getPosition()[1] + yMoves};

    int xDif = -r.getPosition()[0] + destination[0];
    int yDif = -r.getPosition()[1] + destination[1];
    int amountOfXMoves = 0;
    return null;

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
    GameMap.printMapToConsole(gameMap);
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
    // printMapForEveryone();
  }
//  public void printMapForEveryone () {
//    for(ClientThread c : getListOfClients()) {
//      c.updatePlayersAboutMapChanges();
//    }
//  }

  public void spawnRobots() {
    for (int i = 0; i < listOfClients.size(); i++) {
      for (int j = 0; j < serverSettings.getNumberOfRobots(); j++) {
        listOfClients.get(i).addRobot();
      }
    }
  }
}
