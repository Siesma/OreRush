package game.packet;

import game.helper.FileHelper;
import game.helper.MapType;
import game.server.Server;
import game.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public abstract class AbstractPacket {

  protected final String name;
  private final String help;
  private final String[] parts;
  private final String response;
  public static final Logger logger = LogManager.getLogger(Server.class);

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
   * @param name the name of the packet
   * @return returns a new instance of the wanted packet given by the relative name.
   */
  public static AbstractPacket getPacketByName(String name) {
    return (AbstractPacket) (new FileHelper()).createNewInstanceFromName(MapType.Packets, replaceIndicatorChars(name));
  }

  public static AbstractPacket getPacketByMessage(String message) {
    return getPacketByName(splitMessageBySpacer(replaceIndicatorChars(message), String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0]);
  }

  /**
   * Validates a given input by matching it against the packet parts.
   * The parts are a predefined blueprint in how to produce a message for a given packet.
   * @param input the encoded packet
   * @return is this encoded packet valid?
   */
  public boolean validate(String input) {
    input = replaceIndicatorChars(input);
    String[] parts = splitMessageBySpacer(input, String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER));
    Set<String> packetHashMapKeySet = (new FileHelper()).getObjectMap().get(MapType.Packets.getHashName()).keySet();
    ArrayList<String> possiblePackets = new ArrayList<>();

    packetHashMapKeySet.stream().forEach(possiblePackets::add);

    StringBuilder appended = new StringBuilder();
    possiblePackets.forEach(e -> appended.append("|((?i)").append(e).append(")"));
    String matching = appended.subSequence(1, appended.length()).toString();
    if (parts[0].matches("^" + matching + "$")) {
      parts = removeFirstElement(parts);
    }
    for (int i = 0; i < parts.length; i++) {
      if (!parts[i].matches(this.getParts()[i % this.getParts().length])) {
        logger.debug(parts[i] + " Was not properly encoded!!!");
        return false;
      }
    }
    return true;
  }

  /**
   * Prompts a user to enter a String.
   * @param sc the scanner that is waiting for a user input
   * @return the string the user has entered
   */
  protected static String promptUserForInput(Scanner sc) {
    return sc.nextLine();
  }


  /**
   * Cuts of characters that are only used for indicating a packet. Those are the
   * DEFAULT_PACKET_STARTING_MESSAGE and the DEFAULT_PACKET_ENDING_MESSAGE which are respectively
   * ASCII value 2 and ASCII value 3.
   * @param message the string that should be manipulated
   * @return the manipulated string
   */
  public static String replaceIndicatorChars(String message) {
    message = message.replace(String.valueOf((char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE), "");
    message = message.replace(String.valueOf((char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE), "");
    return message;
  }

  /**
   * Splits a message in its components. Those components are seperated using DEFAULT_PACKET_SPACERs.
   * The DEFAULT_PACKET_SPACER is ASCII value 31.
   * @param message some message that should be split into pieces
   * @return the message split into pieces as an array
   */
  public static String[] splitMessageBySpacer(String message) {
    return message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER));
  }

  /**
   * Splits the message into pieces, based on a given spacerCharacter.
   *
   * @param message the string that should be split up
   * @param spacerCharacter the character that indicates where the message should be split at.
   * @return the array, containing the pieces of the split message.
   */
  public static String[] splitMessageBySpacer(String message, String spacerCharacter) {
    return message.split(spacerCharacter);
  }

  /**
   * Helperfunction that removes the first element of a given array and returns the remaining subset.
   * @param in some input array
   * @return a new string array with the first element of the input array removed
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
