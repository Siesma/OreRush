package game.packet.packets;

import game.datastructures.*;
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
            if(obj.getConnectedLobby().getTurnCounter() % obj.getConnectedLobby().getListOfClients().size() != obj.getConnectedLobby().getIDOfClient(obj)) {
                System.out.println("Player " + obj.getPlayerName() + " tried to make a turn but its not their turn.");
                System.out.println("Ignoring this turn attempt.");
                return;
            }
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
                        object = (new FileHelper()).createInstanceOfClass("game.datastructures." + split[4]);
                    } catch (Exception e) {
                        logger.error("An unidentified object!");
                        logger.error("Ignoring this element!");
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
                if(obj.getRobots().get(id).isDead()) {
                    continue;
                }
                obj.getRobots().get(id).setRobotAction(action);
                if(obj.getRobots().get(id).getInventory() == null) {
                    obj.getRobots().get(id).loadInventory(new Nothing());
                }
                obj.getConnectedLobby().getGameMap().replaceObject(obj.getRobots().get(id), result);
                if(action == RobotAction.Dig || action == RobotAction.RequestRadar || action == RobotAction.RequestTrap) {
                    logger.debug("The robot tries to update their inventory.");
                    logger.debug(obj.getRobots().get(id).getInventory().toString());
                }
                obj.getRobots().get(id).setAction(action, result, gameObject);
                if(obj.getRobots().get(id).getInventory() instanceof Ore) {
                    if(obj.getRobots().get(id).getPosition()[0] == 0) {
                        obj.setPlayerScore(obj.getPlayerScore() + 1);
                        obj.getRobots().get(id).loadInventory(new Nothing());
                    }
                }
            }
            obj.getConnectedLobby().updateMove();
            obj.updatePlayersAboutMapChanges();
        }
    }
}
