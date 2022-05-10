package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;
import game.server.ServerSettings;

import java.lang.reflect.Method;
import java.util.Locale;

public class ServerSettingsPacket extends AbstractPacket {


  public ServerSettingsPacket() {
    super("help", new String[]{
      "^((numberOfRobots|mapWidth|mapHeight|numberOfRounds|oreDensity|maxAllowedMoves|radarDistance|ores|maxClusterSize|oreThreshold):[0-9]+(.[0-9]+)?)+$"
    }, "response");
  }

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

  @Override
  public String encode() {
    return null;
  }

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
    }
  }

}
