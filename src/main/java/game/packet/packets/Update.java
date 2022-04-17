package game.packet.packets;

import game.datastructures.GameMap;
import game.datastructures.GameObject;
import game.datastructures.Ore;
import game.datastructures.Robot;
import game.helper.FileHelper;
import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

public class Update extends AbstractPacket {

  private final String match = "\\([1-9]+,[1-9]+\\)";

  public Update() {
    super("", new String[]{
      "^[0-9]+,[0-9]+" + "_" + "(Nothing|Robot|Trap|Radar|Ore):[0-9]+(:(Trap|Radar|Ore):[0-9]+)?$"
    }, "Updating the user about the board!");
  }


  /**
   * Will encode incoming data >as is<.
   * Normal behaviour should create something like this:
   * "CellX","CellY" "SPACER" "ObjectOnCell_1:InformationForObject_1:..."SPACER"ObjectOnCell_2:InformationForObject_2:..." + "SPACER" -> repeat
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
      String[] information = AbstractPacket.splitMessageBySpacer(message, "_");
      GameMap newMap = new GameMap(obj.getConnectedLobby().getServerSettings().getMapWidth(), obj.getConnectedLobby().getServerSettings().getMapHeight(), obj.getConnectedLobby().getServerSettings());
      int cellX = -1, cellY = -1;
      for (String s : information) {
        if (s.matches("^[0-9]+,[0-9]+$")) {
          cellX = Integer.parseInt(s.split(",")[0]);
          cellY = Integer.parseInt(s.split(",")[1]);
        } else {
          if (cellX == -1 || cellY == -1) {
            System.out.println("Somehow the cellID was not updated");
            continue; // should not be possible if the packet is valid, will be included just in case!
          }
          Object object;
          try {
            object = (new FileHelper()).createInstanceOfClass("src/main/java/game/datastructures/" + s.split(":")[0]);
          } catch (Exception e) {
            System.out.println("An unidentified object!");
            System.out.println("Ignoring this element!");
            continue;
          }
          if (!(object instanceof GameObject)) {
            System.out.println("Somehow the initial object trying to be passed is not a GameObject");
            continue;  // should not be possible if the packet is valid, will be included just in case!
          }
          GameObject gameObject = (GameObject) object;
          gameObject.setID(Integer.parseInt(s.split(":")[1]));
          if (gameObject instanceof Robot) {

            Object inv;
            try {
              inv = (new FileHelper()).createInstanceOfClass("src/main/java/game/datastructures/" + s.split(":")[2]);
            } catch (Exception e) {
              System.out.println("An unidentified object!");
              System.out.println("Ignoring this element!");
              continue;
            }
            if (!(inv instanceof GameObject)) {
              System.out.println("Somehow the inventory of the Robot is not a GameObject");
              continue;  // should not be possible if the packet is valid, will be included just in case!
            }

            GameObject inventory = (GameObject) inv;
            inventory.setID(Integer.parseInt(s.split(":")[3]));
            ((Robot) gameObject).loadInventory(inventory);
          }

          newMap.placeObjectOnMap(gameObject, cellX, cellY);
        }
      }
      obj.setCurrentGameMap(newMap);
    }
  }
}
