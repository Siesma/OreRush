package game.datastructures;

/**
 * This Class represents an ore.
 * IT holds the following information:
 * Type / Value
 * X, Y Coordinates
 */
public class Ore implements GameObject {

  /**
   * The OreType (and value) associated with the Ore
   */
  private OreType oreType;
  /**
   * The X coordinate of the Ore
   */
  private int xCoordinate;
  /**
   * The Y coordinate of the Ore
   */
  private int yCoordinate;
  /**
   * The amount of time this ore can be dug on
   */
  private int amount = 1;
  /**
   * The owner of the Ore (unused but here due to interface requirements)
   */
  private String owner;

  /**
   * Default constructor to set the owner of this type as an empty string.
   */
  public Ore() {
    this.owner = "";
  }

  /**
   * Setter for the ID
   * @param id the new ID
   */
  @Override
  public void setID(int id) {
    this.oreType = OreType.values()[id % OreType.values().length];
  }

  /**
   * Setter for the amount
   * @param amount new amount
   */
  public void setAmount(int amount) {
    this.amount = amount;
  }

  /**
   * Setter for the position of the Ore
   * @param x The x coordinate the GameObject should move to
   * @param y The y coordinate the GameObject should move to
   */
  @Override
  public void setPosition(int x, int y) {
    this.xCoordinate = x;
    this.yCoordinate = y;
  }

  /**
   * Getter for the position
   * @return array containing the X (at: 0) and Y (at: 1) coordinates
   */
  @Override
  public int[] getPosition() {
    int[] coordinate = new int[2];
    coordinate[0] = xCoordinate;
    coordinate[1] = yCoordinate;
    return coordinate;
  }

  /**
   * Encodes the Ore Object into a string
   * @return the encoded Ore object
   */
  @Override
  public String encodeToString() {
    String s = "Ore:" + this.oreType.ordinal() + ":" + this.owner;
    return s;
  }

  /**
   * Getter for the Ore Type
   * @return the Ore Type of the Ore Object
   */
  public OreType getOreType() {
    return this.oreType;
  }

  /**
   * Sets the owner of this dataType
   *
   * @param nameOfOwner the name of the player creating this gameObject.
   */
  @Override
  public void setOwner(String nameOfOwner) {
    this.owner = nameOfOwner;
  }

  /**
   * @return the name of the owner of this gameobject
   */
  @Override
  public String getOwner() {
    return owner;
  }

}
