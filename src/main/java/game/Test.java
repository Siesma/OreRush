package game;

import game.datastructures.*;
import game.packet.packets.Update;
import game.server.ServerSettings;

import java.lang.reflect.Field;

public class Test {

  public static void main(String[] args) {

    ServerSettings serverSettings = new ServerSettings();


    GameMap map = new GameMap(serverSettings);

    GameMap.printMapToConsole(map);


    System.out.println("---");


    serverSettings.setValue("mapWidth", 5.0);
    map = new GameMap(serverSettings);
    GameMap.printMapToConsole(map);


  }

}
