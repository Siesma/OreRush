package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

import java.util.Scanner;
/**
 * class representing the ChatLobby packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * The ChatLobby packet is used to send a chat message to all clients connected in the same lobby
 */
public class ChatLobby extends AbstractPacket {


    public ChatLobby() {
        super("", new String[]{"^.*$", // lobbyName
                        "^.*$"} // message
                , "");
    }


    /**
     * This function will create a chat message and lobby where the input is already predetermined.
     */
    @Override
    public String encodeWithContent(String... content) {
        if (content.length == 0) {
            encode();
        }
        String lobbyName = content[0];
        String msg = content[1];
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                lobbyName +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                msg +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }


    /**
     * Creates the message package with CLI input.
     * This means "Start" LOBBYNAME MESSAGE "End"
     * where "Start" is the default start char and "End" is the default end char.
     * LOBBYNAME stands for the lobby that should receive the message.
     * MESSAGE stands for the message that the user has typed in
     */
    @Override
    public String encode() {
        logger.info("Chat-lobby-message:");
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * Decodes the message and will handle the message correctly by sending it to the clients of a lobby if the server receives the packet
     * or adds the message in the lobby GUI if the client receives the packet.
     */
    @Override
    public void decode(Object parent, String message) {

        message = message.replace("ChatLobby" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        String lobbyName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
        message = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];
        if(parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            try {
                obj.pushChatMessageToALobby(lobbyName,message);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if(parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;

            obj.getClient().getLobbyInClient().
                    setLastChatMessage(message + "\n");
        }
    }
}
