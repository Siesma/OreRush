package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

import java.util.Scanner;

public class Nickname extends AbstractPacket {
  public Nickname() {
    super("", new String[]{"^.*$"}, "");
  }

  /**
   * This function will create a Nickname where the input is already predetermined.
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
   * This function will create a Nickname where the input is not predetermined.
   */
  @Override
  public String encode() {
    System.out.println("What do you want your new name to be?");
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      promptUserForInput(new Scanner(System.in)) +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Informs the server and the client about the incoming nickname change.
   * Will remove redundant packet-declaration stuff.
   */
  @Override
  public void decode(Object parent, String message) {
    System.out.println(message);
    if (parent instanceof ClientThread) {
      if (message.startsWith("Nickname" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
        message = message.replace("Nickname" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
      }
      ClientThread obj = (ClientThread) parent;
      obj.changePlayerName(message);
      System.out.println("Username is now: " + ((ClientThread) parent).getPlayerName());
    }
  }
}
