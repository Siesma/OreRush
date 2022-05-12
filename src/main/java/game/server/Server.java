package game.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class of the server.
 * Infinite loop to accept new client connections.
 * Holds a list of clients and a list of lobbys.
 */

public class Server {

    public static final Logger logger = LogManager.getLogger(Server.class);
    private static final ArrayList<ClientThread> clientThreads = new ArrayList<>();
    private final ArrayList<Lobby> lobbyArrayList = new ArrayList<>();

    public static ArrayList<ClientThread> getClientThreads() {
        return clientThreads;
    }

    public void run(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);
        logger.info("Now listening on port " + port);


        PingThread pT = new PingThread();
        Thread pingThread = new Thread(pT);
        pingThread.start();
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
                logger.info("EXITING");
                System.exit(1);
            }
        }
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

    /**
     *
     * @param lobbyName name of the wanted lobby
     * @return the lobby that is associated by that name, null if no lobby with that name exists
     */
    public Lobby getLobbyByName(String lobbyName) {
        for (Lobby lobby : lobbyArrayList) {
            if (lobby.getLobbyName().equals(lobbyName)) {
                return lobby;
            }
        }
        return null;
    }

    /**
     *
     * @param clientThread the client that has to be removed
     * @param lobbyName the lobby in which the client is (supposedly) located
     */
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


    /**
     * Saves the new high scores in a local file.
     * @param winnerClientThread a reference to the client that has won the game.
     */
    public void saveHighScore(ClientThread winnerClientThread) {

        try {
            FileWriter myWriter = new FileWriter("HighScore.txt",true);
            myWriter.write(winnerClientThread.getPlayerName() + ":" + winnerClientThread.getPlayerScore() + "\n");
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHighScore() {
        BufferedReader reader;
        String string;
        try {
            File highScore = new File(System.getProperty("user.dir") + "/HighScore.txt");
            if(!highScore.exists()) {
                logger.debug("The HighScore file did not exist! Creating the file in the folder " + System.getProperty("user.dir"));
                highScore.createNewFile();
            }
            reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/HighScore.txt"));
            string = reader.readLine();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return string;
    }
}
