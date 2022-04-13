package game.packet;

import game.server.ServerConstants;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Scanner;

public class PacketHandler {

  private Object parent;

  public PacketHandler(Object parent) {
    this.parent = parent;
  }

  /**
   * A function that prompts the user to specify which packet is meant to be sent next.
   * All the possible packets are gathered using the files that are in the respective folder.
   * This folder is "packet.packets"
   */
  public String createPacketMessage() {
    System.out.println("Please tell which packet you want to send");
    System.out.println("A list of possible packets are:");
    for (String s : (Objects.requireNonNull(new File(System.getProperty("user.dir") + "/src/main/java/game/packet/packets").list()))) {
      System.out.println("\t->" + s.split(".java")[0]);
    }
    Scanner sc = new Scanner(System.in);
    String entered = AbstractPacket.promptUserForInput(sc);
    AbstractPacket packet;
    try {
      packet = AbstractPacket.getPacketByName(entered);
    } catch (InstantiationException instantiationException) {
      System.out.println("The packet expected some parameters that were not given!");
      return null;
    } catch (ClassNotFoundException classNotFoundException) {
      System.out.println("The specified packet does not exist!");
      return null;
    } catch (IllegalAccessException illegalAccessException) {
      System.out.println("You are not allowed to access the specified file!");
      return null;
    }
    if (packet == null) {
      return "";
    }
    return packet.encode();
  }


  /**
   * A function that pushes a given input string and its according values to the server or client.
   * If a packet is attempted to be created, but it is not succeeding it will return nothing.
   */
  public void pushMessage(OutputStream out, String message) {
    if (message.equals("")) {
      return;
    }
    AbstractPacket packet;
    try {
      packet = AbstractPacket.getPacketByName(AbstractPacket.splitMessageBySpacer(message, String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0]);
    } catch (InstantiationException instantiationException) {
      System.out.println("The packet expected some parameters that were not given!");
      return;
    } catch (ClassNotFoundException classNotFoundException) {
      System.out.println("The specified packet does not exist!");
      return;
    } catch (IllegalAccessException illegalAccessException) {
      System.out.println("You are not allowed to access the specified file!");
      return;
    }
    if (packet == null) {
      return;
    }
    if (!packet.validate(message)) {
      System.out.println("Tried to send: "+ message);
      System.out.println("The given packet contained garbage");
      return;
    }

    try {
      out.write(message.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * TODO: Create a function that creates a respective output from a given input.
   */
  public static String generateOutputFromInput(String input) {
    return "Generic output given the input \"" + input + "\"!";
  }

}
