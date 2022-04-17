package game;

import game.datastructures.Cell;
import game.datastructures.GameMap;
import game.datastructures.Nothing;
import game.datastructures.Trap;
import game.packet.packets.Update;
import game.server.ServerSettings;

public class Test {

  public static void main(String[] args) {
    Update update = new Update();
    int x, y;
    x = 10;
    y = 11;
    GameMap map = new GameMap(x, y, (new ServerSettings("")));
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        if (Math.random() > 0.8) {
          Trap trap = new Trap();
          trap.setID(i + j % 5);
          map.placeObjectOnMap(trap, i, j);
        } else {
          Nothing nothing = new Nothing();
          nothing.setID(i + j % 5);
          map.placeObjectOnMap(nothing, i, j);
        }
      }
    }
    map.spawnOreInMap();
    map.printOreMapToConsole();
    String out = update.encodeWithContent(map.cellStrings());
    System.out.println(out);
  }

}
