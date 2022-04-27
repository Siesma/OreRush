package game.client;

import game.datastructures.GameMap;
import game.datastructures.Robot;
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
import java.util.ArrayList;

public class Client {

  private static Client client;

  private static OutputStream outputStream;
  private Socket socket;
  private InputStream inputStream;
  private boolean pongReceived = false;

  private final StringProperty nickname;
  private final StringProperty lastChatMessage = new SimpleStringProperty();

  private LobbyInClient lobbyInClient;

  private final ObservableList<LobbyInClient> lobbyInClientObservableList = FXCollections.observableArrayList();

  ObservableList<String> observableClientList = FXCollections.observableArrayList();
  ListProperty<String> clientList = new SimpleListProperty<>(observableClientList);

  ObservableList<String> observableLobbyList = FXCollections.observableArrayList();
  ListProperty<String> lobbyList = new SimpleListProperty<>(observableLobbyList);

  public static final Logger logger = LogManager.getLogger(Client.class);

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


//            PongThread pT = new PongThread(this);
//            Thread pongThread = new Thread(pT);
//            pongThread.start();

      CommandLineInputThread cT = new CommandLineInputThread(this);
      Thread commandLineInputThread = new Thread(cT);
      commandLineInputThread.start();


    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static Client getClient() {
    return client;
  }


  /**
   * Shuts down the client.
   */
  public void shutDownClient() {
    System.out.println("terminating ..");
    try {
      inputStream.close();
      outputStream.close();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("EXITING");
    System.exit(0);
  }

  /**
   * Changes the nickname.
   * @param newNickname the new nickname
   */
  public void changeNickname(String newNickname) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Nickname()).encodeWithContent(nickname.get(), newNickname));
    nickname.setValue(newNickname);
  }

  public void makeMove(LobbyController lobbyController) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Move()).encodeWithContent(lobbyController.currentRobotMovesList.getItems().toArray(new String[0])));
  }

  /**
   * Sends a new Chat-packet and encodes it with the predetermined content
   * @param message the message the should be sent in the chat
   */
  public void sendChatMessage(String message) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Chat()).encodeWithContent(message));
  }

  /**
   * Sends a new ChatLobby-packet and encodes it with the predetermined content
   */
  public void sendChatMessageToLobby(String lobbyName, String message) {
    (new PacketHandler(this)).pushMessage(outputStream, (new ChatLobby()).encodeWithContent(lobbyName, message));
  }
  /**
   * Sends a new Whisper-packet and encodes it with the predetermined content
   * @param message the message of the whisper
   * @param receiverName the name of the player that should received the whisper
   */
  public void sendWhisper(String receiverName, String message) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Whisper()).encodeWithContent(receiverName, message));
  }
  /**
   * Sends a new Broadcast-packet and encodes it with the predetermined content
   * @param message the message that should be broadcasted
   */
  public void sendBroadcast(String message) {
    (new PacketHandler(this)).pushMessage(outputStream, (new Broadcast()).encodeWithContent(message));
  }
  /**
   * Sends a new CreateLobby-packet and encodes it with the predetermined content
   * @param newLobbyName the name of the new lobby
   */
  public void createLobby(String newLobbyName) {
    (new PacketHandler(this)).pushMessage(outputStream, (new CreateLobby()).encodeWithContent(newLobbyName));
  }
  /**
   * Sends a new JoinLobby-packet and encodes it with the predetermined content
   * @param lobbyName the name of the lobby that should be joined
   */
  public void joinLobby(String lobbyName) {
    (new PacketHandler(this)).pushMessage(outputStream, (new JoinLobby()).encodeWithContent(lobbyName, nickname.getValue()));
  }
  /**
   * adds the client to the observableClientList
   * @param clientName the name of the client that should be added to the list
   */
  public void addClient(String clientName) {
    Platform.runLater(() -> observableClientList.add(clientName));
  }


  /**
   * checks if it is possible to add the Client to the Lobby.
   * adds the Client to the Playerlist of the Lobby
   * @param lobbyName the name of the lobby that the client should be added to
   * @param clientName the name of the client that should be added to the lobby
   */
  public void addClientToLobby(String clientName, String lobbyName) {
    if (clientName.equals(getNickname())) {
      for (LobbyInClient lobby : lobbyInClientObservableList) {
        if (lobby.getLobbyName().equals(lobbyName)) {
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
   * @param lobbyName the name the lobby that is being left
   */
  public void leaveLobby(String lobbyName) {
    (new PacketHandler(this)).pushMessage(outputStream, (new LeaveLobby()).encodeWithContent(lobbyName, getNickname()));
  }

  // TODO (seb) disconnect packet
  /**
   * remove the Client from The observableClientlist
   * @param clientName the name of the client that should be removed
   */
  public void removeClient(String clientName) {
    Platform.runLater(() -> observableClientList.remove(clientName));
  }

  /**
   * add the Lobby to the observableLobbyList
   * @param lobbyName the name of the lobby that should be added to the lobby list
   */
  public void addLobby(String lobbyName) {
    Platform.runLater(() -> observableLobbyList.add(lobbyName));
  }

  /**
   * Sends a new ChatLobby-packet and encodes it with the predetermined content
   * @param lobbyName the name of the lobby the message should be sent it
   */
  public void addLobbyInClient(String lobbyName) {
    lobbyInClientObservableList.add(new LobbyInClient(lobbyName));
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public boolean isPongReceived() {
    return pongReceived;
  }

  public void setPongReceived(boolean pongReceived) {
    this.pongReceived = pongReceived;
  }

  public StringProperty nicknameProperty() {
    return nickname;
  }

  public StringProperty lastChatMessageProperty() {
    return lastChatMessage;
  }

  public void setLastChatMessage(String message) {
    Platform.runLater(() -> lastChatMessage.setValue(message));
  }

  public ListProperty<String> clientListProperty() {
    return clientList;
  }

  public ListProperty<String> lobbyListProperty() {
    return lobbyList;
  }

  /**
   * replaces the old nickname with the new nickname in the observableClientList
   */
  /**
   * replace the old nickname with the new nickname in the observableClientList
   * @param oldNickname the old nickname
   * @param newNickname the new nickname
   */
  public void changeNicknameOfOtherClient(String oldNickname, String newNickname) {
//    for(String s : observableClientList) {
//      System.out.println("Nickname: \t" + s);
//    }
    Platform.runLater(() -> observableClientList.set(observableClientList.indexOf(oldNickname), newNickname));
  }

  public LobbyInClient getLobbyInClient() {
    return lobbyInClient;
  }

  public void printCurrentGameMap(GameMap gameMap) {
    gameMap.printMapToConsole();
  }

  public String getNickname() {
    return nickname.get();
  }

  public void setLobbyInClient(LobbyInClient lobbyInClient) {
    this.lobbyInClient = lobbyInClient;
  }

  public ObservableList<LobbyInClient> getLobbyData() {
    return lobbyInClientObservableList;
  }

}
