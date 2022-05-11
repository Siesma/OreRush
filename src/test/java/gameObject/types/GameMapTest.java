package gameObject.types;

import game.datastructures.Cell;
import game.datastructures.GameMap;
import game.packet.AbstractPacket;
import game.packet.packets.Update;
import game.server.ServerConstants;
import game.server.ServerSettings;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameMapTest extends TestCase {

  @Test
  public void testStringParse() {

    ServerSettings serverSettings = new ServerSettings();

    GameMap gameMapA = new GameMap(serverSettings);
    gameMapA.spawnOreInMap();

    Update update = new Update();
    String message = update.encodeWithContent(gameMapA.cellStrings());
    message = AbstractPacket.replaceIndicatorChars(message);
    if (message.startsWith("Update" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
      message = message.replace("Update" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    }
    GameMap gameMapB = GameMap.getMapFromString(message, serverSettings);
    Assertions.assertEquals(gameMapA.getGameMapSize()[0], gameMapB.getGameMapSize()[0]);
    Assertions.assertEquals(gameMapA.getGameMapSize()[1], gameMapB.getGameMapSize()[1]);
    for (int i = 0; i < gameMapA.getGameMapSize()[0]; i++) {
      for (int j = 0; j < gameMapA.getGameMapSize()[1]; j++) {
        Cell cellObjectsA = gameMapA.getCellArray()[i][j];
        Cell cellObjectsB = gameMapB.getCellArray()[i][j];
        if (cellObjectsA.trapOnCell() != null) {
          Assertions.assertEquals(cellObjectsA.trapOnCell().getClass(), cellObjectsB.trapOnCell().getClass());
        }
        if (cellObjectsA.oreOnCell() != null) {
          Assertions.assertEquals(cellObjectsA.oreOnCell().getClass(), cellObjectsB.oreOnCell().getClass());
        }
        if (cellObjectsA.robotsOnCell() != null) {
          Assertions.assertEquals(cellObjectsA.robotsOnCell().toString(), cellObjectsB.robotsOnCell().toString());
        }
        if (cellObjectsA.radarOnCell() != null) {
          Assertions.assertEquals(cellObjectsA.radarOnCell().getClass(), cellObjectsB.radarOnCell().getClass());
        }
      }
    }

  }

}
