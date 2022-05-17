package game.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Main class of the server.
 * Infinite loop to accept new client connections.
 * Holds a list of clients and a list of lobbys.
 */

public class Server {

    public static final Logger logger = LogManager.getLogger(Server.class);
    /**
     * A list of all the clients that are connected to the client
     */
    private static final ArrayList<ClientThread> clientThreads = new ArrayList<>();
    /**
     * A list of all the lobbies that have been created on this server
     */
    private final ArrayList<Lobby> lobbyArrayList = new ArrayList<>();

    /**
     *
     * @return the clients that are connected to the server
     */
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

    /**
     * Adds a lobby to the list of lobbies that are created on this server
     * @param lobby the lobby that has to be added to the servers list
     */
    public void addLobby(Lobby lobby) {
        lobbyArrayList.add(lobby);
    }

    /**
     * Adds a client to a lobby
     * @param clientThread the client that has to be moved
     * @param lobbyName the lobby in which the client has to be moved
     */
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

    /**
     *
     * @return the list of all the lobbies from this server
     */
    public ArrayList<Lobby> getLobbyArrayList() {
        return lobbyArrayList;
    }


    /**
     * Saves the new high scores in a local file.
     * @param winnerClientThread a reference to the client that has won the game.
     */
    public void saveHighScore(ClientThread winnerClientThread) {
        try {
            String highscore = getHighScore();
            String newScore = winnerClientThread.getPlayerName() +
                    (char) ServerConstants.DEFAULT_TEXT_SPACER + winnerClientThread.getPlayerScore();

            ArrayList<String> list = new ArrayList<>(Arrays.asList(highscore.split("\n")));
            if (highscore.equals("")) {
                list.set(0, newScore);
            } else {
                for (String item:list) {
                    if (Integer.parseInt(item.split(String.valueOf((char) ServerConstants.DEFAULT_TEXT_SPACER))[1]) < winnerClientThread.getPlayerScore()) {
                        list.add(list.indexOf(item), newScore);
                        break;
                    }
                }
                if (!list.contains(newScore)) {
                    list.add(newScore);
                }
            }

            FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/HighScore.txt");
            myWriter.write(String.join("\n",list));
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return a String concatenated with all the highscores of the previous records.
     */
    public String getHighScore() {
        BufferedReader reader;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File highScore = new File(System.getProperty("user.dir") + "/HighScore.txt");
            if(!highScore.exists()) {
                logger.debug("The HighScore file did not exist! Creating the file in the folder " + System.getProperty("user.dir"));
                highScore.createNewFile();
            }
            reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/HighScore.txt"));
            String s;
            while((s = reader.readLine()) != null) {
                stringBuilder.append(s).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }
}
