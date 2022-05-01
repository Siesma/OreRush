package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;
/**
 * class representing the success packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * This Packet is used by the ping and pong threads to confirm that a ping or pong has arrived.
 */
public class Success extends AbstractPacket {
  public Success() {
    super("", new String[] {"^(?i)Success$"}, "");
  }

  /**
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encodeWithContent(String... content) {
    return encode();
  }

  /**
   * Creates the message that results in a Success packet.
   * This means "Start" Success "End"
   * where "Start" is the default start char and "End" is the default end char.
   */
  @Override
  public String encode() {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      "Success" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }


  /**
   * Will let the respective parent know that the ping was successful.
   */
  @Override
  public void decode(Object parent, String message) {
    if(parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      obj.getClient().setPongReceived(true);
    }
    if(parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.setPingReceived(true);
    }
  }
}
