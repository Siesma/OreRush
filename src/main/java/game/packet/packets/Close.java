package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * class representing the Close packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * The Close packet is used to inform the server a client is disconnecting
 */
public class Close extends AbstractPacket {
  /**
   * Constructor for the close packet
   */
  public Close() {
    super("", new String[]{"^.*$", // player name
            }, "");
  }

  public static final Logger logger = LogManager.getLogger(Server.class);

  /**
   * creates a Close packet
   * @param content name of player that is disconnecting
   * @return encoded close packet
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
   * Generates appropriate response depending on the parent
   * @param parent either server or client
   *               if server: the client sending the packet is removed from the Server,
   *               and the other players are informed
   *               if client: the Gui is updated removing the client from the client list
   * @param message the name of the client that is disconnecting
   */
  @Override
  public void decode(Object parent, String message) {
    if (message.startsWith("Close" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
      message = message.replace("Close" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    }
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.setConnectedToServer(false);
      obj.removeThreadFromServer();
      logger.info(obj.getPlayerName() + " was disconnected from the server");
      for (ClientThread clientThread:Server.getClientThreads()) {

        (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Close()).encodeWithContent(message));

      }
    }
    if(parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      if (obj.getClient().getNickname().equals(message)) {
        obj.getClient().shutDownClient();
      } else {
        obj.getClient().removeClient(message);
      }
    }
  }
}
