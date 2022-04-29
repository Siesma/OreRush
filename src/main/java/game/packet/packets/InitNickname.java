package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ServerConstants;
import javafx.application.Platform;

public class InitNickname extends AbstractPacket {
    public InitNickname() {
        super("", new String[]{"^.*$", //new name
        }, "");
    }

    /**
     * This function will create a InitNickname packet
     *
     * @param content the new name
     * @return the formatted InitNickname packet
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
        return encodeWithContent();
    }

    /**
     * Decodes the packet
     *
     * @param parent  client updates its nickname
     * @param message contains the clientname
     */
    @Override
    public void decode(Object parent, String message) {

        message = message.replace("InitNickname" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");

        if (parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            String finalMessage = message;
            Platform.runLater(() -> obj.getClient().nicknameProperty().setValue(finalMessage));
        }
    }
}
