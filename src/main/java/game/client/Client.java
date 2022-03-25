package game.client;

import game.packet.PacketType;
import game.server.ServerConstants;

import java.io.*;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Client {

  public void run(String hostAddress, int port, String name) {
    try {
      Socket sock = new Socket(hostAddress, port);
      InputStream in = sock.getInputStream();
      OutputStream out = sock.getOutputStream();
      ContentThread th = new ContentThread(in);
      Thread iT = new Thread(th);
      iT.start();
      BufferedReader conin = new BufferedReader(new InputStreamReader(System.in));
      String line;
      while (true) {
        line = conin.readLine();
        // TODO: Create a function that interptes the incoming "line" according to the packets own functions
//        PacketType interpretedPacket = PacketType.close;
//        if (interpretedPacket == PacketType.close) {
//          break;
//        }
        // TODO: delete, testing as long as close packet not functionnal
        if (line.equals("quit")) {
          break;
        }


        out.write(ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE);
        out.write(createPacketMessage().getBytes());
        out.write(ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE);

      }
      System.out.println("terminating ..");
      in.close();
      out.close();
      sock.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String promptUserForInput()
  {
    Scanner sc = new Scanner(System.in);
    return sc.nextLine();
  }


  public static String createPacketMessage() {
    System.out.println("What kind of packet do you want to send?");
    System.out.println("Down follows a list of possible packets.");

    System.out.println("request");
    System.out.println("timeout");
    System.out.println("success");
    System.out.println("awake");
    System.out.println("close");
    System.out.println("update");
    System.out.println("move");
    System.out.println("chat");
    System.out.println("settings");

    while (true) {
      String entered = promptUserForInput();
      PacketType newPacket = new PacketType();
      try {
        System.out.println(generatePacket(entered));
        break;
      } catch (Exception e) {
        System.out.println("No packet exists thats named \"" + entered + "\", try again!");
      }
    }
    //System.out.println("Please enter the needed information to fulfill the " + selected.name() + "-Packet.");
    return "Packet generated";
  }

  private static String generatePacket (String entered) throws Exception {
    switch (entered) {
      case "request":
        return "reqst";
      case "timeout":
        return "timeo";
      case "success":
        return "succs";
      case "awake":
        return "awake";
      case "close":
        return "close";
      case "update":
        return "updte";
      case "move":
        return "pmove";
      case "chat":
        return "pchat";
      case "settings":
        return "settn";
      default:
        throw new Exception();
    }
  }
  /*
  This function asks the user to type their message and saves the player key and message to newChatPackage
   */
  private static void getChatPacketContent (PacketType newChatPackage) {
    while (true) {

      newChatPackage.content[0] = 1; //TODO: Make this use the player key

      String entered = promptUserForInput();
      newChatPackage.content[1] = entered;
      try {
        System.out.println(generatePacket(entered));
        break;
      } catch (Exception e) {
        System.out.println("No packet exists thats named \"" + entered + "\", try again!");
      }
    }
  }
}
