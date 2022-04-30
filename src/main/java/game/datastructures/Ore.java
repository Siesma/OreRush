package game.datastructures;

/**
 * This Class represents an ore.
 * IT holds the following information:
 * Type / Value
 * X, Y Coordinates
 */
public class Ore implements GameObject {

  private OreType oreType;
  private int xCoordinate;
  private int yCoordinate;
  private int amount = 1;
  private String owner;

  /**
   * Default constructor to set the owner of this type as an empty string.
   */
  public Ore() {
    this.owner = "";
  }

  @Override
  public void setID(int id) {
    this.oreType = OreType.values()[id % OreType.values().length];
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  @Override
  public void setPosition(int x, int y) {
    this.xCoordinate = x;
    this.yCoordinate = y;
  }

  @Override
  public int[] getPosition() {
    int[] coordinate = new int[2];
    coordinate[0] = xCoordinate;
    coordinate[1] = yCoordinate;
    return coordinate;
  }

  @Override
  public String encodeToString() {
    String s = "Ore:" + this.oreType.ordinal();
    return s;
  }

  public OreType getOreType() {
    return this.oreType;
  }

  @Override
  public void fillGameObjectWithData(String... data) {
    oreType = OreType.valueOf(data[1]);
    xCoordinate = Integer.parseInt(data[2]);
    yCoordinate = Integer.parseInt(data[3]);
    amount = Integer.parseInt(data[4]);
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
