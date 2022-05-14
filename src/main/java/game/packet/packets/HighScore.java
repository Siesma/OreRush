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
 * class representing the highscore packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * The high score packet is used by a client to ask for the high score list
 */

public class HighScore extends AbstractPacket {
    public HighScore() {
        super("", new String[]{"^(?i)high$"}, "");
    }

    /**
     * This function will create a HighScore packet
     * @param content the highscore list
     * @return the formatted Nickname packet
     */
    @Override
    public String encodeWithContent(String... content) {
        if (content.length == 0) {
            encode();
        }
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                "High" +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * This function is not used
     */
    @Override
    public String encode() {
       return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
               this.name +
               (char) ServerConstants.DEFAULT_PACKET_SPACER +
               "High" +
               (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * Decodes the packet
     * @param parent server or client
     *               if server receives the packet sends the client a high score list
     *
     * @param message contains the lobbyname and clientname
     */
    @Override
    public void decode(Object parent, String message) {



        if (parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
        String highScoreString = obj.getServer().getHighScore();
        (new PacketHandler(this)).pushMessage(obj.getOutputStream(), (new Chat()).encodeWithContent(highScoreString));


        }

    }
}
