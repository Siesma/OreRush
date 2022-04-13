package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

import java.util.Scanner;

public class Whisper extends AbstractPacket{

    public Whisper() {
        super("", new String[]{"^.*$", //receiver name
                "^.*$"// message
        }, "");
    }

    /**
     * This function will create a whisper message where the input is already predetermined.
     */
    @Override
    public String encodeWithContent(String... content) {
        if (content.length < 1) {
            encode();
        }
        String name = content[0];
        String msg = content[1];
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                msg +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    @Override
    public String encode() {
        System.out.println("whisper-message:");
        return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
                this.name +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_SPACER +
                AbstractPacket.promptUserForInput(new Scanner(System.in)) +
                (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
    }

    @Override
    public void decode(Object parent, String message) {
        message = message.replace("Whisper" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        String name = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
        message = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];

        if(parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            try {
                obj.pushWhisperToAClient(obj.getPlayerName(), message);
                obj.pushWhisperToAClient(name, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            // TODO make specific chat ui for whisper messages
            obj.getClient().setLastChatMessage(name + ": " + message + "\n");
        }
    }
}
