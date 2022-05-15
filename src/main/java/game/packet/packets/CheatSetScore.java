package game.packet.packets;

import game.client.Client;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class CheatSetScore extends AbstractPacket {
  public CheatSetScore() {
    super("", new String[]{"^(?i)CheatSetScore", "[0-9]+"}, "");
  }

  @Override
  public String encodeWithContent(String... content) {
    if(content.length != 1) {
      return encode();
    }
    String msg = content[0];
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      msg +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  @Override
  public String encode() {
    return null;
  }

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
