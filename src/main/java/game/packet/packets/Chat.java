package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;
import javafx.application.Platform;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
/**
 * class representing the Chat packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * The Chat packet is used to send a message to all clients in start menus
 */
public class Chat extends AbstractPacket {

  /**
   * Constructor for the Chat packet
   */
  public Chat() {
    super("", new String[]{"^(.|\n)*$"}, "");
  }


  /**
   * This function will create a chat message where the input is already predetermined.
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


  /**
   * Creates the packet with a message.
   * This means "Start" MESSAGE "End"
   * where "Start" is the default start char and "End" is the default end char.
   * MESSAGE stands for the message that the user has typed in
   */
  @Override
  public String encode() {
    logger.info("Chat-message:");
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      AbstractPacket.promptUserForInput(new Scanner(System.in)) +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Decodes the message and will handle the message correctly by sending it to the other clients if the server receives the packet
   * or adds the message in the lobby chat GUI if a client receives it.
   */
  @Override
  public void decode(Object parent, String message) {
    if (message.startsWith("Chat" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
      message = message.replace("Chat" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    }
    if(parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      try {
        obj.pushChatMessageToAllClients(message);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
    if(parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      String finalMessage = message;
      Platform.runLater(() -> {
        obj.getClient().lastChatMessageProperty().setValue(finalMessage + "\n");
      });

    }
  }
}
