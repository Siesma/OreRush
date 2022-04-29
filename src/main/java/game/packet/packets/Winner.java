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
            for (LobbyInClient lobbyInClient:obj.getClient().getLobbyData()) {
                if (lobbyInClient.getLobbyName().equals(lobbyName)) {
                    lobbyInClient.setStatus("finished");
                    lobbyInClient.setPlayers(winnerName + " (" + score +")");
                }
            }
        }
    }
}
