package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;
import javafx.application.Platform;

import java.util.Scanner;
/**
 * class representing the NICKNAME packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * This Packet is used by a client to send a message to a specific client
 */
public class Whisper extends AbstractPacket{
    /**
     * Constructor for the whisper packet
     */
    public Whisper() {
        super("", new String[]{"^.*$", //receiver name
                "^.*$"// message
        }, "");
    }

    /**
     * This function will create a whisper message where the input is already predetermined.
     * @param content contains the name of the client that should receive the message
     *                and the message that is sent.
     * @return the formatted whisper packet
     */
    @Override
    public String encodeWithContent(String... content) {
        if (content.length < 1) {
            encode();
        }
        String name = content[0];
        String msg = content[1];
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                msg +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * This function will create a whisper message where the input is given on th CLI.
     */
    @Override
    public String encode() {
        logger.info("whisper-message:");
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * decodes the packet
     * @param parent server or client
     *               if the server receives the packet the sender and receiver are sent a whisper packet
     *               if a client receives the packet, the contained message is added to the GUI
     * @param message that is sent
     */
    @Override
    public void decode(Object parent, String message) {
        message = message.replace("Whisper" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        String name = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
        message = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];

        if(parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            try {
                obj.pushWhisperToAClient(obj.getPlayerName(), message);
                obj.pushWhisperToAClient(name, message);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if(parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            if (obj.getClient().getLobbyInClient() != null) {
                obj.getClient().getLobbyInClient().setLastChatMessage(name + " (whisper): " + message + "\n");
            }
            String finalMessage = message;
            Platform.runLater(() -> {
                obj.getClient().lastChatMessageProperty().setValue(name + " (whisper): " + finalMessage + "\n");
            });
        }
    }
}
