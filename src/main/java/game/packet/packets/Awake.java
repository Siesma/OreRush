package game.packet.packets;

import game.client.InputStreamThread;
import game.client.PongThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.PingThread;
import game.server.ServerConstants;

public class Awake extends AbstractPacket {


  public Awake() {
    super("", new String[]{"^(?i)awake$"}, "Awake?");
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
      "Awake" +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  @Override
  public void decode(Object parent, String message) {
    if (parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      try {
        (new PacketHandler(obj)).pushMessage(obj.getClient().getOutputStream(), message);
      } catch (Exception e) {
        System.out.println("Client-server connection lost");
      }
    }
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.confirmPong();
    }
    if (parent instanceof PongThread) {
      PongThread obj = (PongThread) parent;

    }
  }


}
