package game.packet.packets;

import game.datastructures.*;
import game.helper.FileHelper;
import game.helper.MapType;
import game.helper.MathHelper;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class Move extends AbstractPacket {


    public Move() {
        super("", new String[] {
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
            if(obj.getConnectedLobby().turnOfPlayer() != obj.getConnectedLobby().getIDOfClient(obj)) {
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
                int[] result = obj.getConnectedLobby().getNextMove(rob, new int[] { x, y });
                if (MathHelper.absoluteCellDistance(rob.getPosition(), result) > 1) {
                    action = RobotAction.Move;
                }
                if (action == RobotAction.RequestRadar || action == RobotAction.RequestTrap) {
                    if (result[0] != 0) {
                        action = RobotAction.Move;
                    }
                }
                obj.getRobots().get(id).setID(id);
                // sets the unique owner of the robot to the StringName of the corresponding clientthread.
                obj.getRobots().get(id).setOwner(obj.getPlayerName());
                // checks if the robot is dead, and if so will not continue performing the action.
                if(obj.getRobots().get(id).isDead()) {
                    continue;
                }
                obj.getRobots().get(id).setRobotAction(action);
                // sets a default value for the inventory.
                // this is mainly so that no NullPointerExceptions are prevented.
                if(obj.getRobots().get(id).getInventory() == null) {
                    obj.getRobots().get(id).loadInventory(new Nothing());
                }
                // sets the object from the original position to the new position.
                obj.getConnectedLobby().getGameMap().replaceObject(obj.getRobots().get(id), result);
                // if an action is being used that involves the inventory it will try to update it.
                if(action == RobotAction.Dig || action == RobotAction.RequestRadar || action == RobotAction.RequestTrap) {
                    logger.debug("The robot tries to update their inventory.");
                    logger.debug(obj.getRobots().get(id).getInventory().toString());
                }

                obj.getRobots().get(id).setAction(action, result, gameObject);
                // checks if the score is supposed to be increased.
                // also reverts the inventory to "Nothing" if the ore is counted towards the score.
                if(obj.getRobots().get(id).getInventory() instanceof Ore) {
                    if(obj.getRobots().get(id).getPosition()[0] == 0) {
                        obj.setPlayerScore(obj.getPlayerScore() + ((Ore) obj.getRobots().get(id).getInventory()).getOreType().getValue());
                        obj.getRobots().get(id).loadInventory(new Nothing());
                    }
                }
            }
            // sends the new move update to the other clients.
            obj.getConnectedLobby().updateMove();
            obj.updatePlayersAboutMapChanges();
        }
    }
}
