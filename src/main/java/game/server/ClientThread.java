package game.server;

import game.packet.PacketGenerator;
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

    PingThread pT = new PingThread();
    Thread pingThread = new Thread(pT);

    StringBuilder builder = new StringBuilder();

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.connectedToServer = true;
    }
    public void run() {
        pingThread.start();

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

            /*
            This part is executed once the end of the message is reached.
             */
            if (cur == ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE) {
                startingToRecordMessage = false;
                String message = builder.toString();
                //PacketHandler.pushMessage(message);
                builder.setLength(0);

                //This part here prints out what the server received. This is here just for bug fixing and manual validation.
                System.out.println("I have received a packet: " + message);
                try {
                    PacketType receivedPacket = PacketHandler.decode(message);
                    receivedPacket.printPacketOnCommandLine();
                    generateAppropriateReaction(receivedPacket);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


            }
            /*
            This will read the whole message into the builder.
             */
            if (startingToRecordMessage) {
                builder.append((char) cur);
            }
            /*
            This is executed when the server detects the start of a message.
             */
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

    /*
    This causes a reaction based on a received Packet.
     */
    private void generateAppropriateReaction(PacketType packet) {
        switch (packet.type) {
            case "reqst":
                break;
            case "timeo":
                break;
            case "succs":
                pT.isPingReceived = true;
                break;
            case "awake":
                break;
            case "close":
                pT.disconnectClient(this);
                break;
            case "updte":
                break;
            case "pmove":
                break;
            case "pchat":
                pushChatMessageToAllClients(packet);
                break;
            case "nickn":
                setPlayerName(packet);
                break;
            case "settn":
                break;
            default:
        }
    }

    /**
     * Changes the playername of the client. Verifies if the name is unique and changes it if necessary.
     * @param packet is the packet that contains the new name that the client wants to use.
     */
    public void setPlayerName(PacketType packet) {
        String playerName = (String) packet.content[1];
        while (!isPlayerNameUnique(playerName)) {
            playerName = changeDuplicateName(playerName);
        }
        this.playerName = playerName;
        System.out.println("New Player Name: " + playerName);

        //This will generate a reply to the Client, informing them that the Nickname was successfully changed
        Object[] content = new Object[100];
        content[0] = 0;
        content[1] = "(Successfully changed their nickname to: " + playerName + ")";
        try {
            pushChatMessageToAllClients(PacketGenerator.generatePacket("pchat", content));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Goes through all client threads and checks if a name is already taken.
     * @param newPlayerName name that needs its uniqueness to be verified
     * @return boolean indicating uniqueness
     */
    public boolean isPlayerNameUnique(String newPlayerName) {
        /*
        for (ClientThread clientThread:Server.getClientThreads()) {
            if (clientThread.getPlayerName().equals(newPlayerName)) {
                return false;
            }
        }
        */
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

    /*
    This should push the given (recieved) chat-Packet back to all the clients.
    It also adds the authors player name
     */
    private void pushChatMessageToAllClients(PacketType chatPacket)
    {
        chatPacket.content[1] = playerName + ": " + chatPacket.content[1];
        PacketHandler.pushMessage(getOutputStream(), chatPacket);
        System.out.println("Pushed Chat Packet to Clients");
    }
}
