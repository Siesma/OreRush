package game.packet.packets;

import game.client.InputStreamThread;
import game.client.LobbyInClient;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;
import javafx.application.Platform;

import java.util.Scanner;
/**
 * class representing the NICKNAME packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * This Packet is used by the server to inform all clients of the win of a player and update the GUI accordingly
 */
public class Winner extends AbstractPacket {

    public Winner() {
        super("", new String[]{"^.*$", //lobby name
                "^.*$", // winner name
                "^.*$" // score
        }, "");
    }

    /**
     * This function will create a Winner packet
     * @param content the lobby where the game finished, the name of the winner and their score
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
                content[0] +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                content[1] +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                content[2] +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * This function is not used
     */
    @Override
    public String encode() {
        return null;
    }

    /**
     * Decodes the packet
     * @param parent if a client receives the packet, the start menu GUI lobby list is updated
     * @param message contains the lobbyname, clientname and score of the winner
     */
    @Override
    public void decode(Object parent, String message) {

        message = message.replace("Winner" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        String lobbyName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
        String winnerName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];
        String score = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[2];

        if (parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            for (LobbyInClient lobbyInClient : obj.getClient().getLobbyData()) {
                if (lobbyInClient.getLobbyName().equals(lobbyName)) {
                    Platform.runLater(() -> {
                        lobbyInClient.setStatus("finished");
                        lobbyInClient.setPlayers(winnerName.toUpperCase() + " (" + score + ")");
                    });
                }
            }
            try {
                if (obj.getClient().getLobbyInClient().getLobbyName().equals(lobbyName)){
                    Platform.runLater(() -> {
                        obj.getClient().getLobbyInClient().setWinner(winnerName);
                    });

                }
            } catch (Exception e) {
                logger.debug("not in lobby");
            }
        }
    }
}
