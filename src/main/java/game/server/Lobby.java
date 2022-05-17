package game.server;

import game.datastructures.GameMap;
import game.packet.PacketHandler;
import game.packet.packets.UpdateTurn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * the lobby class is responsible for handling one game instance.
 * It holds a list of clients playing that game instance.
 * As well as variables needed for the game, such as the map or turncounter.
 */
public class Lobby {

  /**
   * The server from which this lobby originates
   */
  private final Server server;
  /**
   * the name of this lobby
   */
  private final String lobbyName;
  /**
   * An arraylist of clients that are in this lobby
   */
  protected ArrayList<ClientThread> listOfClients = new ArrayList<>();
  /**
   * the current game map.
   */
  protected GameMap gameMap;
  /**
   * the settings for this lobby
   */
  protected ServerSettings serverSettings;
  /**
   * the turncounter indicating whoms turn it is
   */
  protected int turnCounter;
  public static final Logger logger = LogManager.getLogger(Server.class);

  /**
   * the client that has won
   */
  ClientThread winnerClientThread;
  /**
   * the score of the client that has won
   */
  int winnerScore = -1;

  public Lobby(String lobbyName, Server server) {
    this.server = server;
    this.lobbyName = lobbyName;
    this.serverSettings = new ServerSettings();
    initialize();
  }

  /**
   *
   * @return the person who made this lobby
   */
  public ClientThread getHost() {
    return listOfClients.get(0);
  }

  /**
   * creates a new game map
   */
  private void generateGameMap() {
    recreateGameMap();
  }

  /**
   * sets the gamemap variable to a newly created gamemap
   */
  public void recreateGameMap () {
    this.gameMap = new GameMap(serverSettings);
    gameMap.spawnOreInMap();
    respawnRobots();
  }


  /**
   * Returns the players ID of whoms turn it is
   *
   * @return the id of the player who's turn it is.
   */
  public int turnOfPlayer() {
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
//    printMap();
    for (ClientThread clientThread : listOfClients) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(),
        (new UpdateTurn()).encodeWithContent(listOfClients.get(turnOfPlayer()).getPlayerName(), String.valueOf(turnCounter)));
    }
    checkGameEnd();
  }

  /**
   * checks whether the game has ended or not.
   * And if the game has ended it will inform the players about it.
   */
  private void checkGameEnd() {
    if (turnCounter == serverSettings.getNumberOfRounds() * listOfClients.size()) {
      for (ClientThread clientThread : listOfClients) {
        if (clientThread.getPlayerScore() > winnerScore) {
          winnerClientThread = clientThread;
          winnerScore = clientThread.getPlayerScore();
        }
      }
      server.saveHighScore(winnerClientThread);
      winnerClientThread.informOfWinner();
    }
  }
  @SuppressWarnings("unused")
  public void printMap() {
    logger.info("---");
    GameMap.printMapToConsole(gameMap);
    logger.info("---");
  }

  public int getTurnCounter() {
    return turnCounter;
  }

  /**
   *
   * @param clientThread the client from which the id is needed
   * @return the id of the client in question, returns -1 if the client is not connected to the lobby
   */
  public int getIDOfClient(ClientThread clientThread) {
    for (int i = 0; i < listOfClients.size(); i++) {
      ClientThread c = listOfClients.get(i);
      if (c.getPlayerName().equals(clientThread.getPlayerName())) {
        return i;
      }
    }
    return -1;
  }

  /**
   * prematurely ends the game.
   */
  public void endGame () {
      this.turnCounter = serverSettings.getNumberOfRounds() * listOfClients.size();
      checkGameEnd();
  }

  /**
   *
   * @return the name of this lobby
   */
  public String getLobbyName() {
    return lobbyName;
  }

  /**
   *
   * @return the serversettings of this lobby
   */
  public ServerSettings getServerSettings() {
    return serverSettings;
  }

  /**
   *
   * @return the gamemap of this lobby
   */
  public GameMap getGameMap() {
    return gameMap;
  }

  /**
   *
   * @return the list of all the connected clients of this lobby
   */
  public ArrayList<ClientThread> getListOfClients() {
    return listOfClients;
  }

  /**
   * initializes the gamemap and the robots
   */
  public void initialize() {
    respawnRobots();
    generateGameMap();
  }

  /**
   * adds the amount of robots for each client connected to the lobby.
   */
  public void respawnRobots() {
    for (int i = 0; i < listOfClients.size(); i++) {
      listOfClients.get(i).getRobots().clear();
      for (int j = 0; j < serverSettings.getNumberOfRobots(); j++) {
        listOfClients.get(i).addRobot();
      }
    }
  }
}
