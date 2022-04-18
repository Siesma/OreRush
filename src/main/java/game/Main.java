package game;


import game.client.Client;
import game.datastructures.GameMap;
import game.datastructures.Nothing;
import game.datastructures.Trap;
import game.gui.ClientApp;
import game.packet.packets.Update;
import game.server.Server;
import javafx.application.Application;

import java.io.BufferedWriter;
import java.io.FileWriter;


public class Main {
  public static String hostAddress;
  public static String port;
  public static String name;

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Error: Missing arguments.");
      System.out.println("To launch the program following command line parameters are expected:");
      System.out.println("client <hostAddress>:<port> [<username>] | server <port>");
      System.exit(1);
    } else if (args.length > 3) {
      System.out.println("Error: Too many arguments.");
      System.out.println("To launch the program following command line parameters are expected:");
      System.out.println("client <hostAddress>:<port> [<username>] | server <port>");
      System.out.println("The username can't contain spaces.");
      System.exit(1);
    }
    try {
      if (args[0].equals("server")) {
        int port = Integer.parseInt(args[1]);
        Server server = new Server();
        server.run(port);
      } else if (args[0].equals("client")) {
        hostAddress = args[1].split(":")[0];
        port = args[1].split(":")[1];
        if (args.length == 3) {
          name = args[2];
        } else {
          name = System.getProperty("user.name");
        }

        Application.launch(ClientApp.class);

      } else {
        System.out.println("Error: Wrong first argument.");
        System.out.println("The first argument is expected to be \"server\" or \"client\"");
        for(String s : args) {
          System.out.println(s);
        }
        System.exit(1);
      }
    } catch (Exception e) {
      System.out.println("Error: Wrong arguments.");
      System.out.println("To launch the program following command line parameters are expected:");
      System.out.println("client <hostAddress>:<port> [<username>] | server <port>");
      e.printStackTrace();
    }
  }
}
