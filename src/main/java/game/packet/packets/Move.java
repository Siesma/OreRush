package game.packet.packets;

import game.packet.AbstractPacket;

public class Move extends AbstractPacket {


  public Move() {
    super("the move packet consists of one user input part that is repeated $NUM_ROBOTS times. $\"Robot_Move\"", new String[]{
      "^.*$", // Player name + resolve IP
      "^((([1-9]+)->(MOVE\\{[1-9]+,[1-9]+\\}|DIG\\{[1-9]+,[1-9]+\\}|REQUEST\\{[1-3]\\}),)+)?" +
        "(([1-9]+)->(MOVE\\{[1-9]+,[1-9]+\\}|DIG\\{[1-9]+,[1-9]+\\}|REQUEST\\{[1-3]\\}))$" // Zug für jeden Roboter
    }, "Making a move!");
  }


  @Override
  public String encodeWithContent(String... content) {
    return null;
  }


  @Override
  public String encode() {
    return "";
  }

  @Override
  public void decode(Object parent, String message) {

  }
}
