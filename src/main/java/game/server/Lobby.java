package game.server;

import game.datastructures.GameMap;
import game.datastructures.Robot;
import game.helper.MathHelper;
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
  }

  public void spawnRobots() {
    for (int i = 0; i < listOfClients.size(); i++) {
      for (int j = 0; j < serverSettings.getNumberOfRobots(); j++) {
        listOfClients.get(i).addRobot();
      }
    }
  }
}
