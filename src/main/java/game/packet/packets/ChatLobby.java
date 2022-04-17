package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

import java.util.Scanner;

public class ChatLobby extends AbstractPacket {


    public ChatLobby() {
        super("", new String[]{"^.*$", // lobbyName
                        "^.*$"} // message
                , "");
    }


    /**
     * This function will create a chat message where the input is already predetermined.
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
     * Creates the message that results in an Awake packet.
     * This means "Start" MESSAGE "End"
     * where "Start" is the default start char and "End" is the default end char.
     * MESSAGE stands for the message that the user has typed in
     */
    @Override
    public String encode() {
        System.out.println("Chat-lobby-message:");
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    /**
     * Decodes the message and will handle the message correctly by sending it to the server or clients
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
                e.printStackTrace();
            }
        }
        if(parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;

            obj.getClient().getLobbyInClient().
                    setLastChatMessage(message + "\n");
        }
    }
}
