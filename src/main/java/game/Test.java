package game;

import game.datastructures.*;
import game.packet.packets.Update;
import game.server.ServerSettings;

public class Test {

  public static void main(String[] args) {
    Update update = new Update();
    int x, y;
    x = 20;
    y = 10;
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

    Robot r = new Robot();
    r.setPosition(0, 5);
    r.setID(1);

    map.placeObjectOnMap(r, r.getPosition()[0], r.getPosition()[1]);
    map.printOreMapToConsole();
    System.out.println("---");
    map.removeObjectFromMap(r, r.getPosition()[0], r.getPosition()[1]);
    int[] wantedDestination = {4, 9};
    int[] receivedDestination = getNextMove(r, wantedDestination, new ServerSettings(""));
    r.setAction(RobotAction.Move, receivedDestination[0], receivedDestination[1], null);
    map.placeObjectOnMap(r, r.getPosition()[0], r.getPosition()[1]);
    map.printOreMapToConsole();
    System.out.println(r.getPosition()[0]);
    System.out.println(r.getPosition()[1]);
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
