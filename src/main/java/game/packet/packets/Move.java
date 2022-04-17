package game.packet.packets;

import game.client.InputStreamThread;
import game.datastructures.GameObject;
import game.datastructures.RobotAction;
import game.helper.FileHelper;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class Move extends AbstractPacket {


  public Move() {
    super("", new String[]{
            "^[0-9]+:(Move|Dig|Request|Wait):[0-9]+:[0-9]+((:(Ore|Trap|Radar))?)$"
    }, "Making a move!");
  }


  /**
   * The content is supposed to be:
   * Robot_ID:MOVE:X:Y:OptionalInventoryChange
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


  @Override
  public String encode() {
    return encodeWithContent();
  }

  @Override
  public void decode(Object parent, String message) {
    if (message.startsWith(this.name + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
      message = message.replace(this.name + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    }
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      String[] data = splitMessageBySpacer(message);
      for (String s : data) {
        String[] split = s.split(":");
        int id = Integer.parseInt(split[0]);
        RobotAction action = RobotAction.valueOf(split[1]);
        int x = Integer.parseInt(split[2]);
        int y = Integer.parseInt(split[3]);
        Object object;
        try {
          object = (new FileHelper()).createInstanceOfClass("src/main/java/game/datastructures/" + split[4]);
        } catch (Exception e) {
          System.out.println("An unidentified object!");
          System.out.println("Ignoring this element!");
          continue;
        }
        if(!(object instanceof GameObject)) {
          object = null;
        }
        GameObject gameObject = (GameObject) object;
        obj.getRobots().get(id).setAction(action, x, y, gameObject);
      }
    }
  }
}
