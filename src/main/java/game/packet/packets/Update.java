package game.packet.packets;

import game.client.InputStreamThread;
import game.datastructures.GameMap;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class Update extends AbstractPacket {
    public Update() {
        super("", new String[]{
                "^[0-9]+,[0-9]+_(Nothing|Robot|Trap|Radar|Ore):[0-9]+(:(Nothing|Trap|Radar|Ore):[0-9]+)?$"
        }, "Updating the user about the board!");
    }


    /**
     * Will encode incoming data "as is".
     * Normal behaviour should create something like this:
     * "CellX","CellY" "SPACER" "ObjectOnCell_1:InformationForObject_1:..."SPACER"ObjectOnCell_2:InformationForObject_2:..." + "SPACER" to repeat
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
     * Placeholder for encoding the Update packet.
     */
    @Override
    public String encode() {
        return encodeWithContent();
    }


    /**
     * Placeholder for decoding the Update packet.
     */
    @Override
    public void decode(Object parent, String message) {

    /*
    Packet structure would be:
    - Cell
      - x_position
      - y_position
    - ObjectsOnCell
      - ObjectOnCell_1
      - ObjectOnCell_2
      ...


      @OjectsOnCell ->
      Robot(team_id:inventory) // inventory is the same definition as @ObjectsOnCell!
      Radar(team_id)
      Trap(team_id)
      Ore(oreType:oreAmount)
     */
        if (message.startsWith("Update" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
            message = message.replace("Update" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
        }
        if (parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            obj.setCurrentGameMap(GameMap.getMapFromString(message));
        }

        if (parent instanceof InputStreamThread) {
            InputStreamThread obj = (InputStreamThread) parent;
            obj.getClient().getLobbyInClient().updateGameMap(message);

        }
    }
}
