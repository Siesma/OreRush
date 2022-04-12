package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class Join extends AbstractPacket {

  public Join() {
    super("The init packet consists of one user input part. $\"Name\"", new String[]{
      ".*", //Name
    }, "A default initialization response!");
  }

  /**
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encodeWithContent(String... content) {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
            this.name +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            content[0] +
            (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Encodes the unfinished Join packet.
   * Does not work properly and may not be used, so far at least.
   */
  @Override
  public String encode() {
    return null;
  }

  /**
   * Placeholder for if the join packet has to be decoded.
   */
  @Override
  public void decode(Object parent, String message) {
    if (parent instanceof ClientThread) {
      if (message.startsWith("Join" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
        message = message.replace("Join" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
      }
      ClientThread obj = (ClientThread) parent;
      obj.changePlayerName(message);
      obj.pushServerMessageToAllClients( message + " has joined the server.");

    }
  }
}
