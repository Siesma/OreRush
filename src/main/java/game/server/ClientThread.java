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

  private final Server server;
  private final Socket socket;
  private final InputStream inputStream;
  private final OutputStream outputStream;
  StringBuilder builder = new StringBuilder();
  private boolean connectedToServer;
  private boolean pingReceived;
  private String playerName;
  private Lobby connectedLobby;
  private int playerScore;
  private int playerID;
  private ArrayList<Robot> robots;
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
        System.out.println("server received: " + message);
        //PacketHandler.pushMessage(message);
        builder.setLength(0);

        //This part here prints out what the server received. This is here just for bug fixing and manual validation.
        // System.out.println("I have received a packet: " + message);

        try {
          AbstractPacket receivedPacket = AbstractPacket.getPacketByMessage(message);
          if (receivedPacket == null) {
            System.out.println("The received packet contains garbage.");
            break;
          }
          try {
            receivedPacket.decode(this, message);
          } catch (Exception e) {
            logger.fatal("While decoding the message there was a critical error!", e);
          }
        } catch (Exception e) {
          e.printStackTrace();
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
      e.printStackTrace();
    }
  }

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
      playerName = playerName + "_1";
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
   * Sends Chat packet to a all clients
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


  public int getPlayerScore() {
    return playerScore;
  }

  public void setPlayerScore(int playerScore) {
    for (ClientThread clientThread: connectedLobby.getListOfClients()) {
      (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Score()).encodeWithContent(playerName,String.valueOf(playerScore)));
    }
    this.playerScore = playerScore;
  }

  public boolean isPingReceived() {
    return pingReceived;
  }

  public void setPingReceived(boolean pingReceived) {
    this.pingReceived = pingReceived;
  }

  public void setConnectedToServer(boolean connectedToServer) {
    this.connectedToServer = connectedToServer;
  }

  public void setConnectedLobby(Lobby lobby) {
    this.connectedLobby = lobby;
    // connectedLobby.printMapForEveryone();
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public String getPlayerName() {
    return playerName;
  }

  public Server getServer() {
    return server;
  }

  public Lobby getConnectedLobby() {
    return connectedLobby;
  }

  public GameMap getCurrentGameMap() {
    return currentGameMap;
  }

  public void setCurrentGameMap(GameMap currentGameMap) {
    this.currentGameMap = currentGameMap;
//        currentGameMap.printMapToConsole();
  }

  public void setRobots (ArrayList<Robot> robots) {
    this.robots = robots;
  }

  public ArrayList<Robot> getRobots() {
    return robots;
  }

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
