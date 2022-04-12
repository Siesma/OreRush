package game.packet;

import game.helper.FileHelper;
import game.server.ServerConstants;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public abstract class AbstractPacket {

  protected final String name;
  private final String help;
  private final String[] parts;
  private final String response;

  public AbstractPacket(String help, String[] parts, String response) {
    String[] temp = this.getClass().toString().split("\\.");
    this.name = temp[temp.length - 1];
    this.help = help;
    this.parts = parts;
    this.response = response;
  }

  /**
   * Returns a new instance of the class with the specified name in the folder "packet.packets".
   * This is used to not have to hardcode any allowed packets.
   */
  public static AbstractPacket getPacketByName(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    Object obj = (new FileHelper()).createInstanceOfClass("game.packet.packets." + replaceIndicatorChars(name));
    return obj == null ? null : obj instanceof AbstractPacket ? (AbstractPacket) obj : null;
  }

  public static AbstractPacket getPacketByMessage(String message) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    return getPacketByName(splitMessageBySpacer(replaceIndicatorChars(message))[0]);
  }

  /**
   * Validates a given input by matching it against the packet parts.
   * The parts are a predefined blueprint in how to produce a message for a given packet.
   */
  protected boolean validate(String input) {
    input = replaceIndicatorChars(input);
    String[] parts = splitMessageBySpacer(input);
    String[] possiblePackets = Objects.requireNonNull(new File(System.getProperty("user.dir") + "/src/main/java/game/packet/packets").list());
    StringBuilder appended = new StringBuilder();
    Arrays.asList(possiblePackets).forEach(e -> appended.append("|((?i)").append(e.split(".java")[0]).append(")"));
    String matching = appended.subSequence(1, appended.length()).toString();
    if (parts[0].matches("^" + matching + "$")) {
      parts = removeFirstElement(parts);
    }
    if (parts.length != this.getParts().length) {
      return false;
    }
    for (int i = 0; i < parts.length; i++) {
      if (!parts[i].matches(this.getParts()[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Prompts a user to enter a String.
   */
  protected static String promptUserForInput(Scanner sc) {
    return sc.nextLine();
  }


  /**
   * Cuts of characters that are only used for indicating a packet. Those are the
   * DEFAULT_PACKET_STARTING_MESSAGE and the DEFAULT_PACKET_ENDING_MESSAGE which are respectively
   * ASCII value 2 and ASCII value 3.
   */
  protected static String replaceIndicatorChars(String message) {
    message = message.replace(String.valueOf((char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE), "");
    message = message.replace(String.valueOf((char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE), "");
    return message;
  }

  /**
   * Splits a message in its components. Those components are seperated using DEFAULT_PACKET_SPACERs.
   * The DEFAULT_PACKET_SPACER is ASCII value 31.
   */
  public static String[] splitMessageBySpacer(String message) {
    return message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER));
  }

  /**
   * Helperfunction that removes the first element of a given array and returns the remaining subset.
   */
  public static String[] removeFirstElement(String[] in) {
    String[] out = new String[in.length - 1];
    for (int i = 1; i < in.length; i++) {
      out[i - 1] = in[i];
    }
    return out;
  }

  public abstract String encodeWithContent(String... content);

  public abstract String encode();

  public abstract void decode(Object parent, String message);

  public String getHelp() {
    return help;
  }

  public String[] getParts() {
    return parts;
  }

  public String getResponse() {
    return response;
  }
}
