package game.client;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client{

    private static Client client;

    private static OutputStream outputStream;
    private Socket socket;
    private InputStream inputStream;
    private boolean pongReceived = false;

    private final StringProperty nickname;
    private final StringProperty lastChatMessage = new SimpleStringProperty();
    private final ArrayList<LobbyInClient> lobbyInClientArrayList = new ArrayList<>();


    private LobbyInClient lobbyInClient;

    ObservableList<String> observableClientList = FXCollections.observableArrayList();
    ListProperty<String> clientList = new SimpleListProperty<>(observableClientList);

    ObservableList<String> observableLobbyList = FXCollections.observableArrayList();
    ListProperty<String> lobbyList = new SimpleListProperty<>(observableLobbyList);

    public Client(String hostAddress, int port, String name) {
        client = this;
        this.nickname = new SimpleStringProperty(name);

        try {
            socket = new Socket(hostAddress, port);
        } catch (Exception e) {
            System.out.println("The connection with the server failed. \nPlease ensure the server is running with same port and try again. ");
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
     */
    public void sendChatMessage(String message) {
        (new PacketHandler(this)).pushMessage(outputStream, (new Chat()).encodeWithContent(message));
    }

    public void createLobby(String newLobbyName) {
        (new PacketHandler(this)).pushMessage(outputStream, (new CreateLobby()).encodeWithContent(newLobbyName));
    }

    public void joinLobby(String lobbyName) {
        for (LobbyInClient lobby:lobbyInClientArrayList) {
            if (lobby.getName().equals(lobbyName)) {
                lobbyInClient = lobby;
            }
        }
        (new PacketHandler(this)).pushMessage(outputStream, (new JoinLobby()).encodeWithContent(lobbyName, nickname.getValue()));
    }
    public void addClient(String clientName) {
        Platform.runLater(() ->observableClientList.add(clientName));
    }

    public void addClientToLobby(String clientName, String lobbyName) {
        if(clientName.equals(getNickname())) {
            for (LobbyInClient lobby:lobbyInClientArrayList) {
                if (lobby.getName().equals(lobbyName)) {
                    lobbyInClient = lobby;
                }
            }
        }

        for (LobbyInClient lobbyInClient:lobbyInClientArrayList) {
            if (lobbyInClient.getName().equals(lobbyName)) {
                lobbyInClient.addPlayer(clientName);
            }
        }
    }

    // TODO (seb) disconnect packet
    public void removeClient(String clientName) {
        Platform.runLater(() ->observableClientList.remove(clientName));
    }
    public void addLobby(String lobbyName) {
        Platform.runLater(() ->observableLobbyList.add(lobbyName));
    }

    public void addLobbyInLobbyInClientsArrayList(String lobbyName) {
        lobbyInClientArrayList.add(new LobbyInClient(lobbyName));
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

    public StringProperty lastChatMessageProperty() { return lastChatMessage;}

    public void setLastChatMessage(String message) {
        Platform.runLater(() -> lastChatMessage.setValue(message));
    }

    public ListProperty<String> clientListProperty() {
        return clientList;
    }
    public ListProperty<String> lobbyListProperty() {
        return lobbyList;
    }
    public void changeNicknameOfOtherClient(String oldNickname, String newNickname) {
        Platform.runLater(() -> observableClientList.set(observableClientList.indexOf(oldNickname),newNickname));
    }
    public ArrayList<LobbyInClient> getLobbyInClientArrayList() {
        return lobbyInClientArrayList;
    }

    public LobbyInClient getLobbyInClient() {
        return lobbyInClient;
    }


    public String getNickname() {
        return nickname.get();
    }

    public void setLobbyInClient(LobbyInClient lobbyInClient) {
        this.lobbyInClient = lobbyInClient;
    }

}
