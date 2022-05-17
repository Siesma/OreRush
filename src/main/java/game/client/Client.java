package game.client;

import game.datastructures.GameMap;
import game.gui.LobbyController;
import game.packet.PacketHandler;
import game.packet.packets.*;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * The Client class is the main class storing information and threads the client needs to work.
 * It is the model for the Startmenu
 * It holds information of the socket for server connection
 */
public class Client {
  /**
   * Static reference to this client used as model by the GUI controllers
   */
  private static Client client;
  /**
   * Outputstream of the client used to send packets to the server
   */
  private static OutputStream outputStream;
  /**
   * Socket of this client used to connect with the server
   */
  private Socket socket;
  /**
   * Inputstream of this client used to receive packages of the server
   */
  private InputStream inputStream;
  /**
   * Boolean to save if a pong packet from the server was received
   * is set to true if a pong packet was received
   * set to false after a success packet was sent, confirming that a pong packet was received
   */
  private boolean pongReceived = false;
  /**
   *stringproperty used to show the nickname the player chose in the GUI
   */
  private final StringProperty nickname;
  /**
   * Stringproperty used to update the chat text area of the StartMenu controller.
   * Is used by all packets updating the chat area such as chat, whisper, broadcast and highscore.
   */
  private final StringProperty lastChatMessage = new SimpleStringProperty();
  /**
   * LobbyInClient used to store the lobby information in which the client is.
   */
  private LobbyInClient lobbyInClient;
  /**
   * ObservableList of lobby used as model for the start menu controller allowing to see which lobby are currently
   * on the server, their status and the players in them
   */
  private final ObservableList<LobbyInClient> lobbyInClientObservableList = FXCollections.observableArrayList();
  /**
   * Observable list of client names that are currently connected to the server updated by the client
   */
  ObservableList<String> observableClientList = FXCollections.observableArrayList();
  /**
   * List property  of the observable list of clients that allows the start menu controller to bind the list to a list view
   */
  ListProperty<String> clientList = new SimpleListProperty<>(observableClientList);
  /**
   * Log4j logger that allows great, clear and useful logging of information and errors
   * instead of the ugly commandline prints
   */
  public static final Logger logger = LogManager.getLogger(Client.class);

  /**
   * Constructor of the Client class. Used at the start of the GUI, used as the model of the start menu controller and
   * lobby controller
   * @param hostAddress address to connect to the server, can be an ip address in string form or the string "localhost".
   * @param port integer representing the port used by the server.
   * @param name string of the nickname of the player
   */
  public Client(String hostAddress, int port, String name) {
    client = this;
    this.nickname = new SimpleStringProperty(name);

    try {
      socket = new Socket(hostAddress, port);
    } catch (Exception e) {
      logger.fatal("The connection with the server failed. Please ensure the server is running with same port and try again. ", e);
      System.exit(0);
    }
    try {
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();

      InputStreamThread iT = new InputStreamThread(this);
      Thread inputStreamThread = new Thread(iT);
      inputStreamThread.start();

      //Sends packet to the server to set the name passed at launch.
      (new PacketHandler(this)).pushMessage(outputStream, (new Connect()).encodeWithContent(name));


      PongThread pT = new PongThread(this);
      Thread pongThread = new Thread(pT);
      pongThread.start();


    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * getter for the client
   * @return client
   */
  public static Client getClient() {
    return client;
  }


  /**
   * Shuts down the client.
   */
  public void shutDownClient() {
    logger.info("terminating ..");
    try {
      inputStream.close();
      outputStream.close();
      socket.close();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    logger.info("EXITING");
    System.exit(0);
  }

  /**
   * Changes the nickname.
   *
   * @param newNickname the new nickname
   */
  public void changeNickname(String newNickname) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Nickname()).encodeWithContent(nickname.get(), newNickname));
  }

  /**
   * Sends close packet to server if client is shut down from the GUI
   */
  public void sendClosePacket() {
    (new PacketHandler(this)).pushMessage(outputStream, (new Close()).encodeWithContent(getNickname()));
  }

  /**
   * Sends the move packet
   *
   * @param lobbyController the currently connected lobby to retrieve the selected moves.
   */
  public void makeMove(LobbyController lobbyController) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Move()).encodeWithContent(lobbyController.currentRobotMovesList.toArray(new String[0])));
  }

  /**
   * informs the server about a changed server setting.
   *
   * @param content the encoded String in the format "name_of_variable:new_value_of_variable"
   */
  public void sendServerSettings(String content) {
    (new PacketHandler(this)).pushMessage(outputStream, (new ServerSettingsPacket()).encodeWithContent(content));
  }

  /**
   * Sends a new Chat-packet and encodes it with the predetermined content
   *
   * @param message the message the should be sent in the chat
   */
  public void sendChatMessage(String message) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Chat()).encodeWithContent(message));
  }

  /**
   * Sends a new ChatLobby-packet and encodes it with the predetermined content
   *
   * @param message   the message that should be sent in the lobby chat
   * @param lobbyName the name of the lobby the message should be sent in
   */
  public void sendChatMessageToLobby(String lobbyName, String message) {
    (new PacketHandler(this)).pushMessage(outputStream, (new ChatLobby()).encodeWithContent(lobbyName, message));
  }

  /**
   * Sends a new Whisper-packet and encodes it with the predetermined content
   *
   * @param message      the message of the whisper
   * @param receiverName the name of the player that should received the whisper
   */
  public void sendWhisper(String receiverName, String message) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Whisper()).encodeWithContent(receiverName, message));
  }

  /**
   *
   * @param amount the value of the new score
   */
  public void setScoreWithCheats (int amount) {
    (new PacketHandler(this)).pushMessage(outputStream, (new CheatSetScore()).encodeWithContent("" + amount));
  }

  /**
   * Sends a new Broadcast-packet and encodes it with the predetermined content
   *
   * @param message the message that should be broadcasted
   */
  public void sendBroadcast(String message) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Broadcast()).encodeWithContent(message));
  }

  /**
   * Sends a new CreateLobby-packet and encodes it with the predetermined content
   *
   * @param newLobbyName the name of the new lobby
   */
  public void createLobby(String newLobbyName) {
    (new PacketHandler(this)).pushMessage(outputStream, (new CreateLobby()).encodeWithContent(newLobbyName));
  }

  public void sendStartGame() {
    (new PacketHandler(this)).pushMessage(outputStream, (new StartGame()).encodeWithContent(client.getLobbyInClient().getLobbyName()));
  }

  /**
   * Sends a new JoinLobby-packet and encodes it with the predetermined content
   *
   * @param lobbyName the name of the lobby that should be joined
   */
  public void joinLobby(String lobbyName) {
    (new PacketHandler(this)).pushMessage(outputStream, (new JoinLobby()).encodeWithContent(lobbyName, nickname.getValue()));
  }

  /**
   * adds the client to the observableClientList
   *
   * @param clientName the name of the client that should be added to the list
   */
  public void addClient(String clientName) {
    Platform.runLater(() -> observableClientList.add(clientName));
  }


  /**
   * checks if it is possible to add the Client to the Lobby.
   * adds the Client to the Playerlist of the Lobby
   *
   * @param lobbyName  the name of the lobby that the client should be added to
   * @param clientName the name of the client that should be added to the lobby
   */
  public void addClientToLobby(String clientName, String lobbyName) {
    if (clientName.equals(getNickname())) {
      for (LobbyInClient lobby : lobbyInClientObservableList) {
        if (lobby.getLobbyName().equals(lobbyName)) {
          if (lobby.getStatus().equals("in game")) {
            return;
          }
          if (lobby.getStatus().equals("open")) {
            this.lobbyInClient = lobby;
          }
        }
      }
    }

    for (LobbyInClient lobbyInClient : lobbyInClientObservableList) {
      if (lobbyInClient.getLobbyName().equals(lobbyName)) {
        lobbyInClient.addPlayer(clientName);
      }
    }
  }

  /**
   * Sends a new LeaveLobby-packet and encodes it with the predetermined content
   *
   * @param lobbyName the name the lobby that is being left
   */
  public void leaveLobby(String lobbyName) {
    (new PacketHandler(this)).pushMessage(outputStream, (new LeaveLobby()).encodeWithContent(lobbyName, getNickname()));
  }

  /**
   * remove the Client from The observableClientlist
   *
   * @param clientName the name of the client that should be removed
   */
  public void removeClient(String clientName) {
    Platform.runLater(() -> observableClientList.remove(clientName));
  }
  /**
   * Sends a new ChatLobby-packet and encodes it with the predetermined content
   *
   * @param lobbyName the name of the lobby the message should be sent it
   */
  public void addLobbyInClient(String lobbyName) {
    lobbyInClientObservableList.add(new LobbyInClient(lobbyName));
  }

  /**
   * Getter for the outpustream
   * @return outpustream of the client
   */
  public OutputStream getOutputStream() {
    return outputStream;
  }

  /**
   * Getter for the inputstream
   * @return inputstream of the client
   */
  public InputStream getInputStream() {
    return inputStream;
  }

  /**
   * getter for the pong received boolean
   * @return pong received boolean state
   */
  public boolean isPongReceived() {
    return pongReceived;
  }

  /**
   * Setter for the pong received boolean
   * @param pongReceived boolean that is new state of the pong received boolean
   */
  public void setPongReceived(boolean pongReceived) {
    this.pongReceived = pongReceived;
  }

  /**
   * Getter for the nickname property of the client
   * @return the nickname property of the client
   */
  public StringProperty nicknameProperty() {
    return nickname;
  }
  /**
   * Getter for the last Chat Message Property of the client
   * @return the last Chat Message Property of the client
   */
  public StringProperty lastChatMessageProperty() {
    return lastChatMessage;
  }

  /**
   * Setter for the last chat message property
   * Uses Platform.runLater to not conflict with javafx threads
   * @param message string that is set as the last chat message property of the client
   */
  public void setLastChatMessage(String message) {
    Platform.runLater(() -> lastChatMessage.setValue(message));
  }

  /**
   * Getter for the client list property
   * @return the client list property
   */
  public ListProperty<String> clientListProperty() {
    return clientList;
  }

  /**
   * replace the old nickname with the new nickname in the observableClientList
   *
   * @param oldNickname the old nickname
   * @param newNickname the new nickname
   */
  public void changeNicknameOfOtherClient(String oldNickname, String newNickname) {

    Platform.runLater(() -> observableClientList.set(observableClientList.indexOf(oldNickname), newNickname));
  }

  /**
   * Getter for the lobby in client of the client
   * @return the lobby in client of the client
   */
  public LobbyInClient getLobbyInClient() {
    return lobbyInClient;
  }

  /**
   * Getter for the nickname string of the client
   * @return the nickname string of the client
   */
  public String getNickname() {
    return nickname.get();
  }

  /**
   * Setter for the lobby in client of the client
   * @param lobbyInClient LobbyInClient that is the new lobbyInClient of the client
   */
  public void setLobbyInClient(LobbyInClient lobbyInClient) {
    this.lobbyInClient = lobbyInClient;
  }

  /**
   * Getter for the ObservableList of LobbyInClient of the client
   * @return the ObservableList of LobbyInClient of the client
   */
  public ObservableList<LobbyInClient> getLobbyData() {
    return lobbyInClientObservableList;
  }

  /**
   * Handle for the Highscore button of the start menu controller
   * sends a packet asking for the high score list to be sent to this client
   */
  public void sendHighScore() {
    (new PacketHandler(this)).pushMessage(outputStream, (new HighScore()).encodeWithContent());
  }
}
