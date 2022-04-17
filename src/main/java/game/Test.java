package game;

import game.datastructures.GameMap;
import game.datastructures.Nothing;
import game.datastructures.Trap;
import game.packet.packets.Update;

public class Test {

  public static void main(String[] args) {
    Update update = new Update();
    GameMap map = new GameMap(10, 10, 0.5f);
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
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
    String out = update.encodeWithContent(map.cellStrings());
    System.out.println(out);
  }

}
