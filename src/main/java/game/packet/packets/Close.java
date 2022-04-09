package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class Close extends AbstractPacket {


  public Close() {
    super("", new String[]{"^(?i)close$"}, "");
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
      "Close" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  @Override
  public void decode(Object parent, String message) {
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.setConnectedToServer(false);
      obj.removeThreadFromServer();
      System.out.println(obj.getPlayerName() + " was disconnected from the server");
    }
  }
}
