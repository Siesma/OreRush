package game.packet.packets;

import game.client.InputStreamThread;
import game.client.PongThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.PingThread;
import game.server.ServerConstants;

/**
 * class representing the Awake packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * THe Awake paket is used by the Ping and Pong threads, to check the connection
 */
public class Awake extends AbstractPacket {


  public Awake() {
    super("", new String[]{"^(?i)awake$"}, "Awake?");
  }


  /**
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encodeWithContent(String... content) {
    return encode();
  }

  /**
   * Creates the message that results in an Awake packet.
   * This means "Start" Awake "End"
   * where "Start" is the default start char and "End" is the default end char.
   */
  @Override
  public String encode() {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      "Awake" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Decodes the packet.
   * This will handle all the possible objects that call them and what the respective
   * result should be.
   */
  @Override
  public void decode(Object parent, String message) {
    if (parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      obj.confirmPingFromServer();
      obj.getClient().setPongReceived(true);
    }
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.confirmPong();
      obj.setPingReceived(true);
    }
  }


}
