package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Close extends AbstractPacket {


  public Close() {
    super("", new String[]{"^(?i)close$"}, "");
  }

  public static final Logger logger = LogManager.getLogger(Server.class);
  /**
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encodeWithContent(String... content) {
    return encode();
  }

  /**
   * Creates the message that results in a Close packet.
   * This means "Start" Close "End"
   * where "Start" is the default start char and "End" is the default end char.
   */
  @Override
  public String encode() {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
            this.name +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            "Close" +
            (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Closes the connection
   */
  @Override
  public void decode(Object parent, String message) {
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.setConnectedToServer(false);
      obj.removeThreadFromServer();
      logger.info(obj.getPlayerName() + " was disconnected from the server");
    }
    if(parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      obj.getClient().shutDownClient();
    }
  }
}
