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
    coordinate[0] = yCoordinate;
    coordinate[1] = xCoordinate;
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
    String s = "trap:" + xCoordinate + ":" + yCoordinate + ":" + playerID;
    return s;
  }

  @Override
  public void fillGameObjectWithData(String... data) {
    setPosition(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
    setPlayerID(Integer.parseInt(data[3]));
  }
}
