package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;

public class JoinLobby extends AbstractPacket {

    public JoinLobby() {
        super("", new String[] { "^.*$", // lobby name
                        "^.*$" } // player name
                , "");
    }

    /**
     * Creates a JoinLobby packet with
     * @param content the lobbyname and the player name
     * @return the new packet
     */

    @Override
    public String encodeWithContent(String... content) {
        if (content.length == 0) {
            encode();
        }
        String lobbyName = content[0];
        String playerName = content[1];
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                lobbyName +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                playerName +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    @Override
    public String encode() {
        return null;
    }

    /**
     * Decodes the packet
     * @param parent server or client
     *               if server receives the packet the updates the lobby with the new client
     *               if client receives the packet, the GUI is updated
     * @param message contains the lobbyname and clientname
     */

    @Override
    public void decode(Object parent, String message) {
        if (message.startsWith("JoinLobby" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
            message = message.replace("JoinLobby" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        }
        String lobbyName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
        String clientName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];
        if (parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            obj.getServer().addClientToLobby(obj, lobbyName);
            for (ClientThread clientThread : Server.getClientThreads()) {
                (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new JoinLobby()).encodeWithContent(lobbyName, clientName));
            }
            obj.setConnectedLobby(obj.getServer().getLobbyByName(lobbyName));
            obj.getConnectedLobby().printMapForEveryone();
            for (int i = 0; i < obj.getConnectedLobby().getServerSettings().getNumberOfRobots(); i++) {
                obj.addRobot();
            }
        }
        if (parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            obj.getClient().addClientToLobby(clientName, lobbyName);
        }
    }
}
