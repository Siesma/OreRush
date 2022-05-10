package game;

import game.datastructures.*;
import game.packet.packets.Update;
import game.server.ServerSettings;

import java.lang.reflect.Field;

public class Test {

  public static void main(String[] args) {

    ServerSettings serverSettings = new ServerSettings();

    serverSettings.setValue("mapWidth", 100);

    System.out.println(serverSettings.getMapWidth().getVal());
  }

}
