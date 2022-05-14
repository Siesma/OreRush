package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

/**
 * This Packet is not yet used
 */
public class Timeout extends AbstractPacket {


    public Timeout() {
        super("the timeout packet consists of no user input parts.", new String[]{
                "^.*$", // the server that is sending the timeout
                "^(?i)timeout$" // Timeout - case-insensitive
        }, "The user timed out!");
    }


    /**
     * Placeholder in case the packet will have non-normal use cases.
     */
    @Override
    public String encodeWithContent(String... content) {
        return encode();
    }

    /**
     * Creates the message that results in a Timeout packet.
     * This means "Start" Timeout "End"
     * where "Start" is the default start char and "End" is the default end char.
     */
    @Override
    public String encode() {
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                "Timeout!" +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * Removes the client if it has timed out.
     */
    @Override
    public void decode(Object parent, String message) {
        if (parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            obj.removeThreadFromServer();
        }
    }
}
