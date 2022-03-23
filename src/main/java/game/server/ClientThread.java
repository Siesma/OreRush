package game.server;

import game.packet.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientThread implements Runnable{

    InputStream inputStream;
    OutputStream outputStream;

    StringBuilder builder = new StringBuilder();

    public ClientThread(Socket clientSocket) throws IOException {
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
    }
    public void run() {
        boolean startingToRecordMessage = false;
        while (true) {

            int cur;
            try {
                cur = inputStream.read();
            } catch (IOException e) {
                Server.clientThreads.remove(this);
                System.out.println("Client disconnected." );
                System.out.println(Server.clientThreads.size() + " clients are connected to the server.");
                cur = -1;
            }
            if (cur == -1) {
                return;
            }

            if (cur == ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE) {
                startingToRecordMessage = false;
                String message = builder.toString();
                pushMessage(message);
                builder.setLength(0);
                try {
                    outputStream.write(generateOutputFromInput(message).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (startingToRecordMessage) {
                builder.append((char) cur);
            }
            if (cur == ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) {
                startingToRecordMessage = true;
            }
        }
    }
}
