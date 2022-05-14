package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ServerConstants;

/**
 * class representing the Awake packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * The AddBot paket is used to inform the players that a NPC has been added to the game.
 */
public class AddBot extends AbstractPacket {
  public AddBot() {
    super("",  new String[]{"^(?i)addbot$"}, "");
  }

  @Override
  public String encodeWithContent(String... content) {
    return encode();
  }

  /**
   * Creates the message that results in an AddRBot packet.
   * This means "Start" AddBot "End"
   * where "Start" is the default start char and "End" is the default end char.
   */
  @Override
  public String encode() {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      "AddBot" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }


  @Override
  public void decode(Object parent, String message) {
    //TODO
  }
}
