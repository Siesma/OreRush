package game.datastructures;

/**
 * This Class represents a trap.
 * IT holds the following information:
 * X, Y Coordinates
 * Player who laid the trap
 */
public class Trap implements GameObject {
  private int xCoordinate;
  private int yCoordinate;
  private int playerID;

  private String owner;

  /**
   * Default constructor to set the owner of this type as an empty string.
   */
  public Trap () {
    this.owner = "";
  }

  @Override
  public void setID(int id) {
    this.playerID = id;
  }
  /**
   * This method sets the traps position based on a given x,y value.
   * This should only be used when a trap is first placed
   *
   * @param x The x coordinate the trap should move to
   * @param y The y coordinate the trap should move to
   */
  public void setPosition(int x, int y) {
    xCoordinate = x;
    yCoordinate = y;
  }


  /**
   * @return An array [x,y] of the current coordinates of the trap.
   */
  public int[] getPosition() {
    int[] coordinate = new int[2];
    coordinate[0] = xCoordinate;
    coordinate[1] = yCoordinate;
    return coordinate;
  }

  /**
   * This method sets the playerID (of the player who placed the trap)
   *
   * @param playerID the int that should be set as the playerID
   */
  public void setPlayerID(int playerID) {
    this.playerID = playerID;
  }

  /**
   * @return The ID of the player who set the trap.
   */
  public int getPlayerID() {
    return playerID;
  }


  @Override
  public String encodeToString() {
    String s = "Trap:" + playerID;
    return s;
  }


  @Override
  public String toString() {
    return "Trap:" + this.playerID;
  }

  @Override
  public void fillGameObjectWithData(String... data) {
    setPosition(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
    setPlayerID(Integer.parseInt(data[3]));
  }

  /**
   * Sets the owner of this dataType
   * @param nameOfOwner the name of the player creating this gameObject.
   */
  @Override
  public void setOwner(String nameOfOwner) {
    this.owner = nameOfOwner;
  }

  /**
   *
   * @return the name of the owner of this gameobject
   */
  @Override
  public String getOwner () {
    return owner;
  }

}
