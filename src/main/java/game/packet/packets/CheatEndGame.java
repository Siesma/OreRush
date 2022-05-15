package game.packet.packets;

import game.client.Client;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class CheatEndGame extends AbstractPacket {
  public CheatEndGame() {
    super("", new String[]{"^(?i)CheatEndGame"}, "");
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
      "CheatEndGame" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  @Override
  public void decode(Object parent, String message) {
    if(parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.getConnectedLobby().endGame();
    }
  }
}
