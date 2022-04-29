package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Lobby;
import game.server.Server;
import game.server.ServerConstants;

public class CreateLobby extends AbstractPacket {

    public CreateLobby() {
        super("", new String[]{"^.*$"} // new lobby name
                , "");
    }

    /**
     * creates the createLobby packet with
     * @param content the new lobby name
     * @return the properly formatted packet
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

    @Override
    public String encode() {
        return null;
    }

    /**
     * Decodes the packet depending on the parent
     * @param parent can be server or client
     *               the server creates a new lobby and informs the other clients of the lobby by sending a createLobby packet.
     *               the client updates the GUI with a new lobby
     * @param message name of the new lobby
     */

    @Override
    public void decode(Object parent, String message) {
        if (message.startsWith("CreateLobby" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
            message = message.replace("CreateLobby" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        }
        if(parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            obj.getServer().addLobby(new Lobby(message,obj.getServer()));
            for(ClientThread clientThread : Server.getClientThreads()) {
                (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new CreateLobby()).encodeWithContent(message));
            }

        }
        if(parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            obj.getClient().addLobby(message);
            obj.getClient().addLobbyInClient(message);
        }
    }
}
