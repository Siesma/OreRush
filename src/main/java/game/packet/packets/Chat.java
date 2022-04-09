package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Chat extends AbstractPacket {


  public Chat() {
    super("", new String[]{"^.*$"}, "");
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
   * Creates the message that results in an Awake packet.
   * This means "Start" MESSAGE "End"
   * where "Start" is the default start char and "End" is the default end char.
   * MESSAGE stands for the message that the user has typed in
   */
  @Override
  public String encode() {
    System.out.println("Chat-message:");
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      AbstractPacket.promptUserForInput(new Scanner(System.in)) +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Decodes the message and will handle the message correctly by sending it to the server
   */
  @Override
  public void decode(Object parent, String message) {
    if(parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      try {
        obj.getOutputStream().write(message.getBytes());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
