package game.packet.packets;

import game.datastructures.GameMap;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Lobby;
import game.server.ServerConstants;

public class StartGame extends AbstractPacket {
    public StartGame() {
        super("", new String[]{"^.*$", //lobby
        }, "");
    }

    /**
     * This function will create a Start Game packet
     *
     * @param content the lobby name where the game shall start
     * @return the formatted StartGame packet
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
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * This function is not used
     */
    @Override
    public String encode() {
        return null;
    }

    // TODO: add player turn etc...

    /**
     * Decodes the packet
     *
     * @param parent  server or client
     *                if server receives the packet it starts the game and sends the map to clients in the lobby
     *                <p>
     *                if client receives the packet, the map is updated
     * @param message contains the lobbyname and clientname
     */
    @Override
    public void decode(Object parent, String message) {
        message = message.replace("StartGame" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");

        if (parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            for (Lobby lobby : obj.getServer().getLobbyArrayList()) {
                if (lobby.getLobbyName().equals(message)) {
                    for (ClientThread clientThread : lobby.getListOfClients()) {
                        (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(),
                                (new Update()).encodeWithContent(lobby.getGameMap().cellStrings()));
                                        // TODO .getIndividualGameMapForPlayer(clientThread.getPlayerName())));
                    }

                }

            }

        }

    }
}