package game.packet.packets;

import game.client.Client;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class CheatEndGame extends AbstractPacket {
  /**
   * Constructor for the CheatEndGame packet
   */
  public CheatEndGame() {
    super("", new String[]{"^(?i)CheatEndGame"}, "");
  }
  /**
   * not used
   * @param content not used
   * @return null
   */
  @Override
  public String encodeWithContent(String... content) {
    return null;
  }

  /**
   * Encodes the cheatEndGame paacket according to the network protocol
   * @return
   */
  @Override
  public String encode() {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      "CheatEndGame" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Decodes the endgame packet, and ends the game of a lobby
   *
   * @param parent should be a clientThread since the packet is sent by a client to the server,
   * @param message not used, only to validate the message
   */
  @Override
  public void decode(Object parent, String message) {
    if(parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.getConnectedLobby().endGame();
    }
  }
}
