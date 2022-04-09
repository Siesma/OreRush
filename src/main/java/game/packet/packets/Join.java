package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ServerConstants;

public class Join extends AbstractPacket {

  public Join() {
    super("The init packet consists of one user input part. $\"Name\"", new String[]{
      ".*", //Name
      "^([1-9]{2,3}.){3}:([1-9]{3,5})$"  //Resolve IP
    }, "A default initialization response!");
  }

  /**
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encodeWithContent(String... content) {
    return encode();
  }

  /**
   * Encodes the unfinished Join packet.
   * Does not work properly and may not be used, so far at least.
   */
  @Override
  public String encode() {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      "Join" +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      "127.0.0.1:1038" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Placeholder for if the join packet has to be decoded.
   */
  @Override
  public void decode(Object parent, String message) {

  }
}
