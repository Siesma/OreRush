package game;

import game.datastructures.*;
import game.packet.packets.Update;
import game.server.ServerSettings;

public class  Test {

  public static void main(String[] args) {
    Update update = new Update();
    int x, y;
    x = 10;
    y = 10;
    String playerName = "player";
    GameMap map = new GameMap((new ServerSettings("")));
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        if (Math.random() > 0.8) {
          Trap trap = new Trap();
          trap.setID(i + j % 5);
//          trap.setOwner(playerName);
          map.placeObjectOnMap(trap, i, j);
        }
      }
    }
    map.spawnOreInMap();

    Radar radar = new Radar();
    radar.setID(1);
    radar.setOwner(playerName);

    map.placeObjectOnMap(radar, 5, 5);

    GameMap newMap = map.getIndividualGameMapForPlayer(playerName);

    GameMap.printMapToConsole(map);
    System.out.println("---");
    GameMap.printMapToConsole(newMap);

  }
  public static int[] getNextMove (Robot r, int[] destination, ServerSettings serverSettings) {
    int xDif = Math.abs(r.getPosition()[0] - destination[0]);
    int yDif = Math.abs(r.getPosition()[1] - destination[1]);
    int xMoves = Math.min(serverSettings.getMaxAllowedMoves(), xDif);
    int yMoves = Math.min(serverSettings.getMaxAllowedMoves() - xMoves, yDif);
    System.out.println(xMoves);
    System.out.println(yMoves);
    return new int[] {r.getPosition()[0] + xMoves, r.getPosition()[1] + yMoves};
  }

}
