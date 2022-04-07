package game.client;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;
import game.packet.PacketType;
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

    public Client() {

    }

    public void connectToServer(String hostAddress, int port, String name){
        this.nickname = new SimpleStringProperty(name);

        try {
            socket = new Socket(hostAddress, port);
        } catch (Exception e) {
            System.out.println("The connection with the server failed. \nPlease ensure the server is running with same port and try again. ");
            //System.exit(0);
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

    public void changeNickname(String newNickname) throws Exception { //TODO: modify with packet generator
        nickname.setValue(newNickname);
        PacketType namePacket = PacketGenerator.generateNewPacket("nickn");
        namePacket.content[1] = newNickname;
        PacketHandler.pushMessage(outputStream, namePacket);
    }

    public void sendChatMessage(String message) throws Exception { //TODO: modify with packet generator
        PacketType chatPacket = PacketGenerator.generateNewPacket("pchat");
        chatPacket.content[1] = message;
        PacketHandler.pushMessage(outputStream, chatPacket);
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
}
