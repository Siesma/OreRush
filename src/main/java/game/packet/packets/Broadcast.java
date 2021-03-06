package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;
import javafx.application.Platform;

import java.util.Scanner;
/**
 * class representing the Broadcast packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * The Broadcast packet is used to send a chat message accross startmenus and lobbys of all clients
 */
public class Broadcast extends AbstractPacket {

    /**
     * Constructor for the Broadcast packet
     */
    public Broadcast() {
        super("", new String[]{"^.*$"}, "");
    }


    /**
     * This function will create a chat message where the input is already predetermined.
     */
    @Override
    public String encodeWithContent(String... content) {
        if (content.length == 0) {
            encode();
        }
        String msg = content[0];
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                msg +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }


    /**
     * This means "Start" MESSAGE "End"
     * where "Start" is the default start char and "End" is the default end char.
     * MESSAGE stands for the message that the user has typed in
     */
    @Override
    public String encode() {
        logger.info("Broadcast-message:");
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * Decodes the message and will handle the message correctly by sending it to the other clients if the server received it
     * or by showing it in the lobby and start menu if a client receives it.
     */
    @Override
    public void decode(Object parent, String message) {
        if (message.startsWith("Broadcast" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
            message = message.replace("Broadcast" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        }
        if(parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            message = obj.getPlayerName()+ ": " + message;
            for (ClientThread clientThread: Server.getClientThreads()) {
                (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Broadcast()).encodeWithContent(message));
            }
        }
        if(parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            String finalMessage = message;
            Platform.runLater(() -> {
                obj.getClient().lastChatMessageProperty().setValue(finalMessage + "\n");
            });
            if (obj.getClient().getLobbyInClient() != null) {

                Platform.runLater(() -> {
                    obj.getClient().getLobbyInClient().lastChatMessageProperty().setValue(finalMessage + "\n");
                });
            }
        }
    }
}
