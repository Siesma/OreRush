package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;
import game.server.ServerSettings;

import java.lang.reflect.Method;
import java.util.Locale;

public class ServerSettingsPacket extends AbstractPacket {
  /**
   * Constructor for the ServerSettings Packet
   */
  public ServerSettingsPacket() {
    super("help", new String[]{
      "^((numberOfRobots|mapWidth|mapHeight|numberOfRounds|oreDensity|maxAllowedMoves|radarDistance|ores|maxClusterSize|oreThreshold):[0-9]+(.[0-9]+)?)+$"
    }, "response");
  }

  /**
   * Encodes the serve settings properly accordingly to the network protocol
   * @param content of the packet, strings of the information that is to be sent
   * @return a properly formatted string
   */
  @Override
  public String encodeWithContent(String... content) {
    StringBuilder out = new StringBuilder();
    out.append((char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE);
    out.append(this.name);
    for (String s : content) {
      out.append((char) ServerConstants.DEFAULT_PACKET_SPACER);
      out.append(s);
    }
    out.append((char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE);
    return out.toString();
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
   * Decodes the server settings packet
   *
   * @param parent can be a clientThread if the packet is sent by a client to the server,
   *               in this case the server applies the settings to the game
   *               or a client if the server sent the packet to the client,
   *               in this case the client knows the parameters of the map
   * @param message string containing the information about a game setting formatted accordingly to the network protocol,
   */
  @Override
  public void decode(Object parent, String message) {
    if (message.startsWith(this.name + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
      message = message.replace(this.name + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    }
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      for (String s : AbstractPacket.splitMessageBySpacer(message)) {
        try {
          String name = s.split(":")[0];
          Number val = Double.parseDouble(s.split(":")[1]);
          ServerSettings settings = obj.getConnectedLobby().getServerSettings();
          settings.setValue(name, val);
          try {
            settings.setValue(name, val);
          } catch (Exception e) {
            logger.fatal("The variable \"" + name + "\" does not exist", e);
          }
        } catch (Exception e) {
          logger.error("Some error occurred tried to read the slider variables for the serversettings.", e);
        }
      }
      obj.updateLobbyAboutSettingChange(message);
    }

    if (parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      for (String s : AbstractPacket.splitMessageBySpacer(message)) {
        try {
          String name = s.split(":")[0];
          Number val = Double.parseDouble(s.split(":")[1]);
          ServerSettings settings = obj.getClient().getLobbyInClient().getServerSettings();
          settings.setValue(name, val);
          try {
            settings.setValue(name, val);
          } catch (Exception e) {
            logger.fatal("The variable \"" + name + "\" does not exist", e);
          }
        } catch (Exception e) {
          logger.error("Some error occurred tried to read the slider variables for the serversettings.", e);
        }
      }
    }

  }

}
