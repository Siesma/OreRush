package game;

import game.datastructures.*;
import game.packet.packets.Update;
import game.server.ServerSettings;

public class Test {

  public static void main(String[] args) {
//    Update update = new Update();
//    ServerSettings serverSettings = (new ServerSettings());
//    int x, y;
//    x = serverSettings.getMapWidth();
//    y = serverSettings.getMapHeight();
//    String playerName = "player";
//    GameMap map = new GameMap(serverSettings);
//    for (int i = 0; i < x; i++) {
//      for (int j = 0; j < y; j++) {
//        if (Math.random() > 0.0) {
//          Trap trap = new Trap();
//          trap.setID(i + j % 5);
//          trap.setOwner("playerName");
//          map.placeObjectOnMap(trap, i, j);
//        }
//      }
//    }
//    map.spawnOreInMap();
//
//    Radar radar = new Radar();
//    radar.setID(1);
//    radar.setOwner(playerName);
//
//    map.placeObjectOnMap(radar, 3, 4);
//    GameMap newMap = map.getIndividualGameMapForPlayer(playerName);
//
//    GameMap.printMapToConsole(map);
//    System.out.println("---");
//    GameMap.printMapToConsole(newMap);



    GameMap gameMap = new GameMap(new ServerSettings());

    gameMap.spawnOreInMap();

//    System.out.println((new Update()).encodeWithContent(gameMap.cellStrings()));

    GameMap test = GameMap.getMapFromString("\u0002Update\u001F0,0_Nothing:0:Server\u001F0,1_Nothing:0:Server\u001F0,2_Nothing:0:Server\u001F0,2_Ore:0:Server\u001F0,3_Nothing:0:Server\u001F0,4_Nothing:0:Server\u001F1,0_Nothing:0:Server\u001F1,1_Nothing:0:Server\u001F1,1_Ore:0:Server\u001F1,1_Ore:0:Server\u001F1,2_Nothing:0:Server\u001F1,3_Nothing:0:Server\u001F1,3_Ore:0:Server\u001F1,4_Nothing:0:Server\u001F2,0_Nothing:0:Server\u001F2,1_Nothing:0:Server\u001F2,1_Ore:0:Server\u001F2,2_Nothing:0:Server\u001F2,3_Nothing:0:Server\u001F2,3_Ore:1:Server\u001F2,3_Ore:1:Server\u001F2,4_Nothing:0:Server\u001F3,0_Nothing:0:Server\u001F3,1_Nothing:0:Server\u001F3,2_Nothing:0:Server\u001F3,2_Ore:1:Server\u001F3,3_Nothing:0:Server\u001F3,3_Ore:1:Server\u001F3,4_Nothing:0:Server\u001F4,0_Nothing:0:Server\u001F4,1_Nothing:0:Server\u001F4,1_Ore:3:Server\u001F4,2_Nothing:0:Server\u001F4,2_Ore:3:Server\u001F4,3_Nothing:0:Server\u001F4,3_Ore:3:Server\u001F4,4_Nothing:0:Server\u0003\n");

    GameMap.printMapToConsole(gameMap);
    GameMap.printMapToConsole(test);

  }

  public static int[] getNextMove(Robot r, int[] destination, ServerSettings serverSettings) {
    int xDif = Math.abs(r.getPosition()[0] - destination[0]);
    int yDif = Math.abs(r.getPosition()[1] - destination[1]);
    int xMoves = Math.min(serverSettings.getMaxAllowedMoves(), xDif);
    int yMoves = Math.min(serverSettings.getMaxAllowedMoves() - xMoves, yDif);
    System.out.println(xMoves);
    System.out.println(yMoves);
    return new int[]{r.getPosition()[0] + xMoves, r.getPosition()[1] + yMoves};
  }

}
