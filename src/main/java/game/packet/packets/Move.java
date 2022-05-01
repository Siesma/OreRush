package game.packet.packets;

import game.datastructures.*;
import game.helper.FileHelper;
import game.helper.MapType;
import game.helper.MathHelper;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;
/**
 * class representing the Move packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * This Packet is used by a client to indicate the robot action he wants to perform during his turn
 */
public class Move extends AbstractPacket {


  public Move() {
    super("", new String[]{
            "^[0-9]+:(Move|Dig|RequestRadar|RequestTrap|Wait):[0-9]+:[0-9]+((:(Ore|Trap|Radar))?)$"
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
      // check if the player that tries to a turn is the one who can make a turn.
      if (obj.getConnectedLobby().turnOfPlayer() != obj.getConnectedLobby().getIDOfClient(obj)) {
        System.out.println("Player " + obj.getPlayerName() + " tried to make a turn but its not their turn.");
        System.out.println("Ignoring this turn attempt.");
        return;
      }
      // splits the incoming singular information into an array.
      String[] data = splitMessageBySpacer(message);
      for (String s : data) {
        // splits the current information in its parts.
        // id:action:x:y:optionalInventory.
        String[] split = s.split(":");
        int id = Integer.parseInt(split[0]);
        RobotAction action = RobotAction.valueOf(split[1]);
        int x = Integer.parseInt(split[2]);
        int y = Integer.parseInt(split[3]);
        Object object = null;
        // if there is an optionalInventory it will be tried to make a new instance of it.
        if (split.length > 4) {
          try {
            object = (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, split[4]);
//                        object = (new FileHelper()).createInstanceOfClass("game.datastructures." + split[4]);
          } catch (Exception e) {
            // this should never happen as this means that the object is valid but the file for it does not exist.
            logger.error("An unidentified object!");
            logger.error("Ignoring this element!");
          }
        }
        // this basically means that the file is corrupt (because it implies
        // that a valid object is no GameObject which should not be possible) or that there was no optionalInventory.
        if (!(object instanceof GameObject)) {
          object = null;
        }
        GameObject gameObject = (GameObject) object;
        // gets the robot associated with this information.
        // also calculates the next valid move and sets its action to what is possible.
        // if the distance is too big for a valid "dig" or "request" action it will revert it to a move action.
        Robot rob = obj.getRobots().get(id);
        int[] result = MathHelper.getNextMove(rob.getPosition(), new int[]{x, y}, obj.getConnectedLobby().getServerSettings());
        if (MathHelper.absoluteCellDistance(rob.getPosition(), result) > 1) {
          action = RobotAction.Move;
        }
        if (action == RobotAction.RequestRadar || action == RobotAction.RequestTrap) {
          if (result[0] != 0) {
            action = RobotAction.Move;
          }
        }

        rob.setID(id);
        // sets the unique owner of the robot to the StringName of the corresponding clientthread.
        rob.setOwner(obj.getPlayerName());
        // checks if the robot is dead, and if so will not continue performing the action.
        if (rob.isDead()) {
          continue;
        }
        rob.setRobotAction(action);
        // sets a default value for the inventory.
        // this is mainly so that no NullPointerExceptions are prevented.
        if (rob.getInventory() == null) {
          rob.loadInventory(new Nothing());
        }
        // sets the object from the original position to the new position.
        obj.getConnectedLobby().getGameMap().replaceObject(rob, result);
        // if an action is being used that involves the inventory it will try to update it.
        if (action == RobotAction.Dig || action == RobotAction.RequestRadar || action == RobotAction.RequestTrap) {
          logger.debug("The robot tries to update their inventory.");
          logger.debug(rob.getInventory().toString());
        }

        rob.setAction(action, result, gameObject);
        // checks if the score is supposed to be increased.
        // also reverts the inventory to "Nothing" if the ore is counted towards the score.
        if (rob.getInventory() instanceof Ore) {
          if (rob.getPosition()[0] == 0) {
            obj.setPlayerScore(obj.getPlayerScore() + ((Ore) rob.getInventory()).getOreType().getValue());
            rob.loadInventory(new Nothing());
          }
        }
      }
      // sends the new move update to the other clients.
      obj.getConnectedLobby().updateMove();
      obj.updatePlayersAboutMapChanges();
    }
  }
}
