package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.server.ServerConstants;

public class Move extends AbstractPacket {


  public Move() {
    super("the move packet consists of one user input part that is repeated $NUM_ROBOTS times. $\"Robot_Move\"", new String[]{
      "^.*$", // Player name + resolve IP
      "^((([1-9]+)->(MOVE\\{[1-9]+,[1-9]+\\}|DIG\\{[1-9]+,[1-9]+\\}|REQUEST\\{[1-3]\\}),)+)?" +
        "(([1-9]+)->(MOVE\\{[1-9]+,[1-9]+\\}|DIG\\{[1-9]+,[1-9]+\\}|REQUEST\\{[1-3]\\}))$" // Zug für jeden Roboter
    }, "Making a move!");
  }


  /**
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encodeWithContent(String... content) {
    return encode();
  }


  @Override
  public String encode() {
    return "";
  }

  @Override
  public void decode(Object parent, String message) {
    /*
    Packet structure would be:
    - Robot
      - index_of_robot
    - Position
      - new_robot_position
     */
    if (parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      String[] data = AbstractPacket.splitMessageBySpacer(message, String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER)); // Important: Position has to be cropped!
      obj.getClient().getRobots().get(Integer.parseInt(data[1])).
        setPosition(Integer.parseInt(data[1]), Integer.parseInt(data[1]));
    }
  }
}
