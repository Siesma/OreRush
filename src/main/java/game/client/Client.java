package game.client;

import game.packet.PacketHandler;
import game.packet.packets.Chat;
import game.packet.packets.Nickname;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client{

    private static OutputStream outputStream;
    private Socket socket;
    private InputStream inputStream;
    private boolean pongReceived = false;

    private String nickname;
    private String lastChatMessage;

    public Client(String hostAddress, int port, String name) {
        this.nickname = name;

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

    public void changeNickname(String newNickname) throws Exception {
        nickname = (newNickname);
        (new PacketHandler(this)).pushMessage(outputStream, (new Nickname()).encodeWithContent(newNickname));
    }

    public void sendChatMessage(String message) throws Exception {
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

    public String nicknameProperty() {
        return nickname;
    }

    public String lastChatMessageProperty() { return lastChatMessage;}

    public void setLastChatMessage(String message) {
        lastChatMessage = (message);
    }
}
