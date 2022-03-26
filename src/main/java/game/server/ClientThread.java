package game.server;

import game.packet.PacketHandler;
import game.packet.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientThread implements Runnable{

    private boolean connectedToServer;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private String playerName;

    StringBuilder builder = new StringBuilder();

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.connectedToServer = true;
    }
    public void run() {
        boolean startingToRecordMessage = false;
        while (connectedToServer) {

            int cur;
            try {
                cur = inputStream.read();
            } catch (IOException e) {
                Server.getClientThreads().remove(this);

                System.out.println("Client disconnected." );
                System.out.println(Server.getClientThreads().size() + " clients are connected to the server.");
                cur = -1;
            }
            if (cur == -1) {
                return;
            }

            if (cur == ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE) {
                startingToRecordMessage = false;
                String message = builder.toString();
                //PacketHandler.pushMessage(message);
                builder.setLength(0);

                System.out.println("I have received a packet: " + message);
                try {
                    PacketType receivedPacket = PacketHandler.decode(message);
                    receivedPacket.printPacketOnCommandLine();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


                /*
                try {
                    PacketHandler.pushMessage(outputStream, PacketHandler.decode(message));
                    System.out.println("I have recieved a message!");
                    //outputStream.write(PacketHandler.generateOutputFromInput(message).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                */

            }
            if (startingToRecordMessage) {
                builder.append((char) cur);
            }
            if (cur == ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) {
                startingToRecordMessage = true;
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setConnectedToServer(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getPlayerName() {
        return playerName;
    }


    /**
     * Changes the playername of the client. Verifies if the name is unique and changes it if necessary.
     * @param playerName new name that the client wants to use.
     */
    public void setPlayerName(String playerName) {
        while (isPlayerNameUnique(playerName)) {
            playerName = changeDuplicateName(playerName);
        }
        this.playerName = playerName;
    }

    /**
     * Goes through all client threads and checks if a name is already taken.
     * @param newPlayerName name that needs its uniqueness to be verified
     * @return boolean indicating uniqueness
     */
    public boolean isPlayerNameUnique(String newPlayerName) {
        for (ClientThread clientThread:Server.getClientThreads()) {
            if (clientThread.getPlayerName().equals(newPlayerName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Changes a duplicate name by either adding a 1 at the end of the name or increasing the last digit by 1
     * @param playerName name to be modified
     * @return modified name
     */

    public String changeDuplicateName(String playerName){
        if (Character.isDigit(playerName.charAt(playerName.length()-1))) {
            playerName = playerName.substring(0,playerName.length()-1)+ (Integer.parseInt(playerName.substring(playerName.length()-1))+1);
        } else {
            playerName = playerName + "_1";
        }
        return playerName;
    }
}
