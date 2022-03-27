package game.server;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;
import game.packet.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientThread implements Runnable{

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private boolean connectedToServer;
    private boolean pingReceived;

    private String playerName;

    StringBuilder builder = new StringBuilder();

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.connectedToServer = true;
        playerName = "unknown";
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
                if (Server.getClientThreads().size() == 0) {
                    System.out.println("No clients are connected to the server.");
                } else if (Server.getClientThreads().size() == 1) {
                    System.out.println("1 client is connected to the server.");
                } else {
                    System.out.println(Server.getClientThreads().size()
                            + " clients are connected to the server.");
                }

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
                    if (receivedPacket == null) {
                        System.out.println("The recieved packet contains garbage.");
                        break;
                    }
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
                setPingReceived(true);
                break;
            case "awake":
                confirmPong();
                break;
            case "close":
                setConnectedToServer(false);
                Server.getClientThreads().remove(this);
                System.out.println(getPlayerName() + " was disconnected from the server.");
                break;
            case "updte":
                break;
            case "pmove":
                break;
            case "pchat":
                pushChatMessageToAllClients(packet);
                break;
            case "nickn":
                changePlayerName((String) packet.content[1]);
                break;
            case "settn":
                break;
            default:
        }
    }

    /**
     * Changes the playerName of the client. Verifies if the name is unique and changes it if necessary.
     * @param playerName is the new name that the client wants to use and needs to be checked.
     */
    public void changePlayerName(String playerName) {
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

    /*
    This should push the given (received) chat-Packet back to all the clients.
    It also adds the authors player name
     */
    private void pushChatMessageToAllClients(PacketType chatPacket)
    {
        chatPacket.content[1] = playerName + ": " + chatPacket.content[1];
        for (ClientThread clientThread:Server.getClientThreads()) {
            PacketHandler.pushMessage(clientThread.getOutputStream(), chatPacket);
        }
        System.out.println("Pushed Chat Packet to Clients");
    }

    private void confirmPong() {
        try {
            PacketHandler.pushMessage(outputStream, PacketGenerator.generateNewPacket("succs"));
        } catch (Exception e) {

        }
    }

    // getters and setters

    public boolean isPingReceived() {
        return pingReceived;
    }

    public void setPingReceived(boolean pingReceived) {
        this.pingReceived = pingReceived;
    }

    public void setConnectedToServer(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getPlayerName() {
        return playerName;
    }
}
