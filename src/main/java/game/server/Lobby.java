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

  private final Server server;
  private final String lobbyName;
  protected ArrayList<ClientThread> listOfClients = new ArrayList<>();
  protected GameMap gameMap;
  protected ServerSettings serverSettings;
  protected int turnCounter;
  public static final Logger logger = LogManager.getLogger(Server.class);

  ClientThread winnerClientThread;
  int winnerScore = -1;

  public Lobby(String lobbyName, Server server) {
    this.server = server;
    this.lobbyName = lobbyName;
    this.serverSettings = new ServerSettings();
    initialize();
  }

  public ClientThread getHost() {
    return listOfClients.get(0);
  }

  private void generateGameMap() {
    recreateGameMap();
  }

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

  public void printMap() {
    logger.info("---");
    GameMap.printMapToConsole(gameMap);
    logger.info("---");
  }

  public int getTurnCounter() {
    return turnCounter;
  }

  public int getIDOfClient(ClientThread clientThread) {
    for (int i = 0; i < listOfClients.size(); i++) {
      ClientThread c = listOfClients.get(i);
      if (c.getPlayerName().equals(clientThread.getPlayerName())) {
        return i;
      }
    }
    return -1;
  }

  public void endGame () {
      this.turnCounter = serverSettings.getNumberOfRounds() * listOfClients.size();
      checkGameEnd();
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
    respawnRobots();
    generateGameMap();
  }

  public void respawnRobots() {
    for (int i = 0; i < listOfClients.size(); i++) {
      listOfClients.get(i).getRobots().clear();
      for (int j = 0; j < serverSettings.getNumberOfRobots(); j++) {
        listOfClients.get(i).addRobot();
      }
    }
  }
}
