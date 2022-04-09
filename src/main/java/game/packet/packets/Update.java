package game.packet.packets;

import game.packet.AbstractPacket;

public class Update extends AbstractPacket {


  public Update() {
    super("", new String[]{
      "^.*$", // Player name + resolve IP
      "^([1-9]+(->((\\{(?i)robot\\([1-9]+\\)\\})?)((\\{(?i)ore\\([1-9]+,[1-9]+\\)\\})?)((\\{(?i)trap\\([1-9]+\\)\\})?)((\\{(?i)radar\\([1-9]+\\)\\})?))?)$", // Sending coordinate in the format (x+y*width) and occupying Objects
      "^([1-9]+),([1-9]+),([1-9]+)$",
      "^(((?i)robot\\(([1-9]+,\\([1-9]+,[1-9]+\\))((,(?i)trap|(?i)radar|(?i)detector)?)((,(?i)ore:\\([1-9]+,[1-9]+\\))?)\\)),)+?((?i)robot\\(([1-9]+,\\([1-9]+,[1-9]+\\))((,(?i)trap|(?i)radar|(?i)detector)?)((,(?i)ore:\\([1-9]+,[1-9]+\\))?)\\))$",
      "^[1-9]+$",
      "^((([1-9]+:[1-9]+),)+)?([1-9]+:[1-9]+)$"
    }, "Updating the user about the board!");
  }


  /**
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encodeWithContent(String... content) {
    return encode();
  }


  /**
   * Placeholder for encoding the Update packet.
   */
  @Override
  public String encode() {
    return "";
  }

  /**
   * Placeholder for decoding the Update packet.
   */
  @Override
  public void decode(Object parent, String message) {

  }
}