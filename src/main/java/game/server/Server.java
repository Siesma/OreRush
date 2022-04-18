package game.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

  public static final Logger logger = LogManager.getLogger(Server.class);
  private static final ArrayList<ClientThread> clientThreads = new ArrayList<>();
  private ArrayList<Lobby> lobbyArrayList = new ArrayList<>();


  public void run(int port) throws IOException {

    ServerSocket serverSocket = new ServerSocket(port);
    logger.info("Now listening on port " + port);


//        PingThread pT = new PingThread();
//        Thread pingThread = new Thread(pT);
//        pingThread.start();

    while (true) {
      try {
        logger.info(clientThreads.size() + " clients are connected to the server.");
        logger.info("Waiting for client connection... ");

        Socket socket = serverSocket.accept();
        ClientThread cT = new ClientThread(this, socket);
        clientThreads.add(cT);
        Thread clientThread = new Thread(cT);
        clientThread.start();
        logger.info("New client connected.");


      } catch (IOException e) {
        logger.error(e.getMessage());
        e.printStackTrace();
        System.out.println("EXITING");
        System.exit(1);
      }
    }
  }

  public static ArrayList<ClientThread> getClientThreads() {
    return clientThreads;
  }

  public void addLobby(Lobby lobby) {
    lobbyArrayList.add(lobby);
  }

  public void addClientToLobby(ClientThread clientThread, String lobbyName) {
    Lobby lobby = getLobbyByName(lobbyName);
    if (lobby == null) {
      return;
    }
    lobby.addClient(clientThread);
  }

  public Lobby getLobbyByName(String lobbyName) {
    for (Lobby lobby : lobbyArrayList) {
      if (lobby.getLobbyName().equals(lobbyName)) {
        return lobby;
      }
    }
    return null;
  }

  public void removeClientFromLobby(ClientThread clientThread, String lobbyName) {
    for (Lobby lobby : lobbyArrayList) {
      if (lobby.getLobbyName().equals(lobbyName)) {
        lobby.removeClient(clientThread);
      }
    }
  }

  public ArrayList<Lobby> getLobbyArrayList() {
    return lobbyArrayList;
  }

}
