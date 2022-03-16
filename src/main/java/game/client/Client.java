package game.client;

import game.packet.PacketType;
import game.server.ServerConstants;

import java.io.*;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class Client {

  public void run(String[] args) {
    String host = "localhost";
    int port;
    if (args.length == 0) {
      port = ServerConstants.DEFAULT_PORT;
    } else {
      port = Integer.parseInt(args[0]);
    }
    try {
      Socket sock = new Socket(host, port);
      InputStream in = sock.getInputStream();
      OutputStream out = sock.getOutputStream();
      ContentThread th = new ContentThread(in);
      Thread iT = new Thread(th);
      iT.start();
      BufferedReader conin = new BufferedReader(new InputStreamReader(System.in));
      String line = " ";
      while (true) {
        line = conin.readLine();
        // TODO: Create a function that interptes the incoming "line" according to the packets own functions
        PacketType interpretedPacket = PacketType.close;
        if (interpretedPacket == PacketType.close) {
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


  public static String createPacketMessage() {
    Scanner sc = new Scanner(System.in);
    System.out.println("What kind of packet do you want to send?");
    System.out.println("Down follows a list of possible packets. You can also write the corresponding number in front");
    for (int i = 0; i < PacketType.values().length; i++) {
      System.out.println("\t" + (i + 1) + ". ->" + PacketType.values()[i].name());
    }
    PacketType selected;
    while (true) {
      String entered = sc.nextLine();
      try {
        if (entered.matches("^[1-9]+$")) {
          selected = PacketType.values()[Integer.parseInt(entered) - 1];
        } else {
          selected = PacketType.valueOf(entered);
        }
        break;
      } catch (Exception e) {
        System.out.println("No packet exists thats named \"" + entered + "\", try again!");
      }
    }
    System.out.println("Please enter the needed information to fulfill the " + selected.name() + "-Packet.");
    String help = selected.getHelp();
//    help.replaceAll("\\$NUM_ROBOTS", "4");
    System.out.println(help);
    String[] neededTypes = new String[help.split("\\$").length - 1];
    for (int i = 0; i < neededTypes.length; i++) {
      neededTypes[i] = sc.nextLine();
    }
    StringBuilder out = new StringBuilder();
    for (String s : neededTypes) {
      out.append(s).append((char) ServerConstants.DEFAULT_PACKET_SPACER);
    }
    return out.toString();
  }

}
