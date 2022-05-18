package game.packet.packets;

import game.client.Client;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

/**
 * This packet is used for a cheat code
 * It allows to set the score of a player in an illegitimate way
 */
public class CheatSetScore extends AbstractPacket {
  /**
   * Constructor for the CheatSetScore packet
   */
  public CheatSetScore() {
    super("", new String[]{"^[0-9]+$"}, "");
  }

  /**
   * Creates a properly encoded packet defined in the network protocol
   * @param content of the packet, strings of the information that is to be sent
   *                value of the new score
   * @return  properly encoded packet
   */
  @Override
  public String encodeWithContent(String... content) {
    String msg = content[0];
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      msg +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Not used.
   * @return null
   */
  @Override
  public String encode() {
    return null;
  }

  /**
   * decodes the packet and sets the score of a player to a certain value
   *
   * @param parent should be clientThread since the packet is sent by a client to the server
   *
   * @param message string containing the new score to be set formatted accordingly to the network protocol
   */
  @Override
  public void decode(Object parent, String message) {
    if (message.startsWith(this.name + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
      message = message.replace(this.name + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    }
    if(parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      int val = Integer.parseInt(message);
      obj.setPlayerScore(val);
    }
  }
}
