package game.packet.packets;

import game.datastructures.GameObject;
import game.datastructures.Robot;
import game.datastructures.RobotAction;
import game.helper.FileHelper;
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
            String[] data = splitMessageBySpacer(message);
            for (String s : data) {
                String[] split = s.split(":");
                int id = Integer.parseInt(split[0]);
                RobotAction action = RobotAction.valueOf(split[1]);
                int x = Integer.parseInt(split[2]);
                int y = Integer.parseInt(split[3]);
                Object object = null;
                if (split.length > 4) {
                    try {
                        object = (new FileHelper()).createInstanceOfClass("game.datastructures" + split[4]);
                    } catch (Exception e) {
                        System.out.println("An unidentified object!");
                        System.out.println("Ignoring this element!");
                    }
                }
                if (!(object instanceof GameObject)) {
                    object = null;
                }
                GameObject gameObject = (GameObject) object;
                Robot rob = obj.getRobots().get(id);
                int[] result = obj.getConnectedLobby().getNextMove(rob, new int[] { x, y });
                if (obj.getConnectedLobby().distanceFromPosition(rob.getPosition(), result) > 1) {
                    action = RobotAction.Move;
                }
                if (action == RobotAction.RequestRadar || action == RobotAction.RequestTrap) {
                    if (result[0] != 0) {
                        action = RobotAction.Move;
                    }
                }
                obj.getRobots().get(id).setID(id);
                obj.getRobots().get(id).setRobotAction(action);
                obj.getConnectedLobby().getGameMap().replaceObject(obj.getRobots().get(id), result);
                obj.getRobots().get(id).setAction(action, result, gameObject);
                if(action == RobotAction.Dig || action == RobotAction.RequestRadar || action == RobotAction.RequestTrap) {
                    System.out.println("The robot tries to update their inventory.");
                    System.out.println(obj.getRobots().get(id).getInventory().toString());
                }
                if(obj.getRobots().get(id).getPosition()[0] == 0) {
                    //TODO: Increase score!
                }
            }
            obj.getConnectedLobby().updateMove();
        }
    }
}
