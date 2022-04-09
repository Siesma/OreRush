package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;

public class Success extends AbstractPacket {
  public Success() {
    super("", new String[] {"^(?i)Success$"}, "");
  }

  @Override
  public String encodeWithContent(String... content) {
    return null;
  }

  @Override
  public String encode() {
    return null;
  }

  @Override
  public void decode(Object parent, String message) {
    System.out.println("There was a success packet somewhere");
    if(parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      obj.getClient().setPongReceived(true);
    }
  }
}
