package game.client;

import game.packet.PacketHandler;
import game.packet.packets.Chat;
import game.packet.packets.Nickname;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client{

    private static OutputStream outputStream;
    private Socket socket;
    private InputStream inputStream;
    private boolean pongReceived = false;

    private StringProperty nickname;
    private StringProperty lastChatMessage = new SimpleStringProperty();

    public Client(String hostAddress, int port, String name) {
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
            changeNickname(name);

            PongThread pT = new PongThread(this);
            Thread pongThread = new Thread(pT);
            pongThread.start();

            CommandLineInputThread cT = new CommandLineInputThread(this);
            Thread commandLineInputThread = new Thread(cT);
            commandLineInputThread.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
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
        nickname.setValue(newNickname);
        (new PacketHandler(this)).pushMessage(outputStream, (new Nickname()).encodeWithContent(newNickname));
    }

    /**
     * Sends a new Chat-packet and encodes it with the predetermined content
     */
    public void sendChatMessage(String message) {
        (new PacketHandler(this)).pushMessage(outputStream, (new Chat()).encodeWithContent(message));
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
        lastChatMessage.setValue(message);
    }
}
