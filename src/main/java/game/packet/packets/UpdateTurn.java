package game.packet.packets;

import game.client.InputStreamThread;
import game.datastructures.GameMap;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;
import javafx.application.Platform;

/**
 * class representing the updateturn packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * This Packet is used by the server to update the players of the current player which turn it is
 */
public class UpdateTurn extends AbstractPacket {
    /**
     * Constructor for the UpdateTurn packet
     */
    public UpdateTurn() {
        super("", new String[]{
                "^.*$", // player whose turn it is
                "^.*$" // turn number
        }, "Updating the user about the board!");
    }

    /**
     * encodes the packet with the content using appropriate beginning, spacer and ending characters
     * @param content player name and turn number
     * @return encoded packet
     */
    @Override
    public String encodeWithContent(String... content) {
        if (content.length == 0) {
            encode();
        }
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                content[0] +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                content[1] +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }


    /**
     * Placeholder for encoding the Update packet.
     */
    @Override
    public String encode() {
        return null;
    }


    /**
     * Decodes the packet
     *
     * @param parent  client
     *                if client receives the packet, it updates the player who should play and the turn counter
     * @param message contains the clientname and turnnumber
     */
    @Override
    public void decode(Object parent, String message) {
        if (message.startsWith("UpdateTurn" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
            message = message.replace("UpdateTurn" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        }
        String player =  message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
        String turn =  message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];
        if (parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            Platform.runLater(() ->obj.getClient().getLobbyInClient().setPlayerOnPlay(player));
            Platform.runLater(() ->obj.getClient().getLobbyInClient().setTurnCounter(turn));
        }
    }
}
