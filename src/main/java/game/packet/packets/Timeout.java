package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class Timeout extends AbstractPacket {


  public Timeout() {
    super("the timeout packet consists of no user input parts.", new String[]{
      "^.*$", // the server that is sending the timeout
      "^(?i)timeout$" // Timeout - case-insensitive
    }, "The user timed out!");
  }



  @Override
  public String encodeWithContent(String... content) {
    return null;
  }

  @Override
  public String encode() {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      "Timeout!" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  @Override
  public void decode(Object parent, String message) {
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.removeThreadFromServer();
    }
  }
}
