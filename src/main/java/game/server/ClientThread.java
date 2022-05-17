package game.server;

import game.datastructures.GameMap;
import game.datastructures.Robot;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.packet.packets.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is created by the server for each client that connects to it.
 * It holds information about the client auch as the name or the lobby to which it connects.
 * It handles the receiving of packets from the client by validating and decoding them.
 */

public class ClientThread implements Runnable {

  /**
   * The server this clientthread is connected to
   */
  private final Server server;
  /**
   * The socket of this clientthread
   */
  private final Socket socket;
  /**
   * the inputStream of this clientthread
   */
  private final InputStream inputStream;
  /**
   * the outputstream of this clientthread
   */
  private final OutputStream outputStream;
  /**
   * The stringbuilder that is used for incoming data.
   */
  StringBuilder builder = new StringBuilder();
  /**
   * a boolean that represents whether this clientthread is connected to a server or not
   */
  private boolean connectedToServer;
  /**
   * a boolean that represents whether a ping has been received or not
   */
  private boolean pingReceived;
  /**
   * the nickname of this client
   */
  private String playerName;
  /**
   * the lobby this client is connected to
   */
  private Lobby connectedLobby;
  /**
   * the score of this player
   */
  private int playerScore;
  /**
   * the id of this player
   */
  private int playerID;
  /**
   * An arraylist that stores the robots of the player
   */
  private ArrayList<Robot> robots;
  /**
   * the current Map of the lobby in which this clientthread is in
   */
  private GameMap currentGameMap;

  public static final Logger logger = LogManager.getLogger(ClientThread.class);

  public ClientThread(Server server, Socket socket) throws IOException {
    this.server = server;
    this.socket = socket;
    this.inputStream = socket.getInputStream();
    this.outputStream = socket.getOutputStream();
    this.connectedToServer = true;
    playerName = "unknown";
    this.playerScore = 0;
    this.robots = new ArrayList<>();
  }

  /**
   * starts the new thread that runs the clientthread
   */
  public void run() {

    boolean startingToRecordMessage = false;
    while (connectedToServer) {

      int cur;
      try {
        cur = inputStream.read();
      } catch (IOException e) {
        removeThreadFromServer();
        logger.info("Client disconnected.");
        if (Server.getClientThreads().size() == 0) {
          logger.info("No clients are connected to the server.");
        } else if (Server.getClientThreads().size() == 1) {
          logger.info("1 client is connected to the server.");
        } else {
          logger.info(Server.getClientThreads().size()
            + " clients are connected to the server.");
        }

        cur = -1;
      }
      if (cur == -1) {
        return;
      }


      //This part is executed once the end of the message is reached.
      if (cur == ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE) {
        startingToRecordMessage = false;
        String message = builder.toString();
        logger.info("server received: " + message);
        //PacketHandler.pushMessage(message);
        builder.setLength(0);

        //This part here prints out what the server received. This is here just for bug fixing and manual validation.
        // System.out.println("I have received a packet: " + message);

        try {
          AbstractPacket receivedPacket = AbstractPacket.getPacketByMessage(message);
          if (receivedPacket == null) {
            logger.debug("The received packet contains garbage.");
            break;
          }
          try {
            receivedPacket.decode(this, message);
          } catch (Exception e) {
            logger.fatal("While decoding the message there was a critical error!", e);
          }
        } catch (Exception e) {
          logger.error(e.getMessage());
        }
      }
      // This will read the whole message into the builder.
      if (startingToRecordMessage) {
        builder.append((char) cur);
      }
      // This is executed when the server detects the start of a message.
      if (cur == ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) {
        startingToRecordMessage = true;
      }
    }

    try {
      socket.close();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * removes this client from the server in case of a disconnect
   */
  public void removeThreadFromServer() {
    Server.getClientThreads().remove(this);
  }


  /**
   * Changes the playerName of the client. Verifies if the name is unique and changes it if necessary.
   *
   * @param playerName is the new name that the client wants to use and needs to be checked.
   */
  public void changePlayerName(String playerName) {
    while (!isPlayerNameUnique(playerName)) {
      playerName = changeDuplicateName(playerName);
    }
    this.playerName = playerName;
  }

  /**
   * Goes through all client threads and checks if a name is already taken.
   *
   * @param newPlayerName name that needs its uniqueness to be verified
   * @return boolean indicating uniqueness
   */
  public boolean isPlayerNameUnique(String newPlayerName) {
    for (ClientThread clientThread : Server.getClientThreads()) {
      if (clientThread.getPlayerName().equals(newPlayerName)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Changes a duplicate name by either adding a 1 at the end of the name or increasing the last digit by 1
   *
   * @param playerName name to be modified
   * @return modified name
   */
  public String changeDuplicateName(String playerName) {
    if (Character.isDigit(playerName.charAt(playerName.length() - 1))) {
      playerName = playerName.substring(0, playerName.length() - 1) + (Integer.parseInt(playerName.substring(playerName.length() - 1)) + 1);
    } else {
      playerName = playerName + "1";
    }
    return playerName;
  }

  /**
   * Sends Chat packet to a specific client
   *
   * @param receiverName name of the specific client that should receive the message
   * @param msg          message that is sent preceded by the name of the sender
   */
  public void pushWhisperToAClient(String receiverName, String msg) {
    for (ClientThread clientThread : Server.getClientThreads()) {
      if (clientThread.getPlayerName().equals(receiverName)) {
        (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Whisper()).encodeWithContent(playerName, msg));
      }
    }
  }

  /**
   * Sends Chat packet to a all clients
   *
   * @param msg message that is sent preceded by the name of the sender
   */
  public void pushChatMessageToAllClients(String msg) {
    msg = playerName + ": " + msg;
    for (ClientThread clientThread : Server.getClientThreads()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Chat()).encodeWithContent(msg));
    }
  }


  /**
   * Sends Chat packet to a all clients
   * @param lobbyName the name of the target lobby
   * @param msg message that is sent preceded by the name of the sender
   */
  public void pushChatMessageToALobby(String lobbyName, String msg) {
    msg = playerName + ": " + msg;
    for (Lobby lobby : server.getLobbyArrayList()) {
      if (lobby.getLobbyName().equals(lobbyName)) {
        for (ClientThread clientThread : lobby.listOfClients) {
          (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new ChatLobby()).encodeWithContent(lobbyName, msg));
        }
      }
    }
  }

  /**
   * Sends an Update packet to all the Clients informing them about the new Map
   */
  public void updatePlayersAboutMapChanges() {
    for (ClientThread clientThread : this.connectedLobby.getListOfClients()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Update()).encodeWithContent(clientThread.getConnectedLobby().gameMap.cellStrings()));
    }
  }
  /**
   * Sends an AddBot packet to all the Clients informing them about a new Robot in the lobby.
   * @param botName the new Robots name.
   */
  public void updatePlayerAboutANewBot(String botName) {
    for (ClientThread clientThread : this.connectedLobby.getListOfClients()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new AddBot()).encodeWithContent(botName));
    }
  }

  /**
   * Sends a ServerSettingsPacket to all the users informing them about changes.
   * @param content the name of the setting that is being changed and the new value of said setting
   */
  public void updateLobbyAboutSettingChange (String content) {
    for (ClientThread clientThread : this.connectedLobby.getListOfClients()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new ServerSettingsPacket()).encodeWithContent(content));
    }
  }

  /**
   * Sends Chat packet to all clients
   *
   * @param msg message that is sent preceded by the name of the sender
   */
  public void pushServerMessageToAllClients(String msg) {
    msg = "Server: " + msg;
    for (ClientThread clientThread : Server.getClientThreads()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Chat()).encodeWithContent(msg));
    }
  }

  /**
   * Adds a robot to the Robot array of the player and to the map
   */
  public void addRobot() {
    Robot robot = new Robot();
    robot.setOwner(getPlayerName());
    int height = connectedLobby.gameMap.getGameMapSize()[1];
    robot.setPosition(0, (int) (Math.random() * height));
    robot.setID(this.getRobots().size());
    this.getRobots().add(robot);
    this.getConnectedLobby().gameMap.placeObjectOnMap(robot, robot.getPosition());
  }

  /**
   * Sends a success packet to the appropriate client, to confirm an awake package was received
   */
  public void confirmPong() {
    try {
      (new PacketHandler(this)).pushMessage(outputStream, (new Success()).encode());
    } catch (Exception e) {
      logger.error("Server-Client connection has been lost");
    }
  }

  // getters and setters

  /**
   * getter for playerScore
   * @return playerScore
   */

  public int getPlayerScore() {
    return playerScore;
  }

  /**
   * setter for playerScore and informs the clients of the lobby
   * @param playerScore integer of the score of a player
   */
  public void setPlayerScore(int playerScore) {
    for (ClientThread clientThread: connectedLobby.getListOfClients()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Score()).encodeWithContent(playerName,String.valueOf(playerScore)));
    }
    this.playerScore = playerScore;
  }
  /**
   * getter for pingReceived
   * @return pingReceived
   */
  public boolean isPingReceived() {
    return pingReceived;
  }

  /**
   * Setter for pingReceived
   * @param pingReceived variable indicating if pingReceived
   */
  public void setPingReceived(boolean pingReceived) {
    this.pingReceived = pingReceived;
  }

  /**
   * Setter for connectedToServer
   * @param connectedToServer indicates if connected to server or not
   */
  public void setConnectedToServer(boolean connectedToServer) {
    this.connectedToServer = connectedToServer;
  }

  /**
   * setter for connectedLobby
   * @param lobby that the client connects to
   */
  public void setConnectedLobby(Lobby lobby) {
    this.connectedLobby = lobby;
    // connectedLobby.printMapForEveryone();
  }
  /**
   * getter for outputStream
   * @return outputStream
   */
  public OutputStream getOutputStream() {
    return outputStream;
  }
  /**
   * getter for playerName
   * @return playerName
   */
  public String getPlayerName() {
    return playerName;
  }
  /**
   * getter for server
   * @return server
   */
  public Server getServer() {
    return server;
  }
  /**
   * getter for connectedLobby
   * @return connectedLobby
   */
  public Lobby getConnectedLobby() {
    return connectedLobby;
  }
  /**
   * getter for currentGameMap
   * @return currentGameMap
   */
  public GameMap getCurrentGameMap() {
    return currentGameMap;
  }

  /**
   * setter for currentGameMap
   * @param currentGameMap current game map
   */
  public void setCurrentGameMap(GameMap currentGameMap) {
    this.currentGameMap = currentGameMap;
//        currentGameMap.printMapToConsole();
  }

  /**
   * setter for robots
   * @param robots to be set
   */
  public void setRobots (ArrayList<Robot> robots) {
    this.robots = robots;
  }

  /**
   * getter for robots
   * @return an arraylist of robots
   */
  public ArrayList<Robot> getRobots() {
    return robots;
  }

  /**
   * sends a packet to all client in lobby to inform of a winner
   * sends a packet to update the lobby list in the start menus
   */
  public void informOfWinner() {
    for (ClientThread clientThread : Server.getClientThreads()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Winner()).encodeWithContent(connectedLobby.getLobbyName(),getPlayerName(),String.valueOf(playerScore)));
    }
    String message = playerName + " has won with " + playerScore + " in " + connectedLobby.getLobbyName() + ".";

    for (ClientThread clientThread: Server.getClientThreads()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Broadcast()).encodeWithContent(message));
    }
  }
}
