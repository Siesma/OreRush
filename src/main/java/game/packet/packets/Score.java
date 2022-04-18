package game.packet.packets;

import game.client.InputStreamThread;
import game.gui.Player;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;

import java.util.Scanner;

public class Score extends AbstractPacket {
    public Score() {
        super("", new String[]{"^.*$", // playerName
                "^.*$" //new score
        }, "");
    }

    /**
     * This function will create a score packet where the input is already predetermined.
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


    @Override
    public String encode() {

        return null;
    }

    /**
     * Informs clients that a player has changed score.
     */
    @Override
    public void decode(Object parent, String message) {

        message = message.replace("Score" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        String playerName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
        message = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];


        if (parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            for (Player player : obj.getClient().getLobbyInClient().getPlayerData()) {
                if (player.getNickname().equals(playerName)) {
                    player.setScore(message);
                }
            }
        }
    }
}
