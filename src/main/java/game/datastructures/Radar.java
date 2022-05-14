package game.datastructures;

/**
 * This Class represents a radar.
 * IT holds the following information:
 * X, Y Coordinates
 * Player who placed the radar
 */
public class Radar implements GameObject {
    private int xCoordinate;
    private int yCoordinate;
    private int playerID;

    private String owner;

  /**
   * Default constructor to set the owner of this type as an empty string.
   */
  public Radar () {
    this.owner = "";
  }

    @Override
    public void setID(int id) {
        this.playerID = id;
    }

    /**
     * This method sets the radar position based on a given x,y value.
     * This should only be used when a radar is first placed
     *
     * @param x The x coordinate the radar should move to
     * @param y The y coordinate the radar should move to
     */
    public void setPosition(int x, int y) {
        xCoordinate = x;
        yCoordinate = y;
    }

    /**
     * @return An array [x,y] of the current coordinates of the radar.
     */
    public int[] getPosition() {
        int[] coordinate = new int[2];
        coordinate[0] = xCoordinate;
        coordinate[1] = yCoordinate;
        return coordinate;
    }

    /**
     * @return The ID of the player who set the radar.
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * This method sets the playerID (of the player who placed the radar)
     *
     * @param playerID the int that should be set as the playerID
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    @Override
    public String encodeToString() {
        String s = "Radar:" + playerID + ":" + this.owner;
        return s;
    }

    @Override
    public String toString() {
        return "Radar:" + this.playerID;
    }

    /**
     * @return the name of the owner of this gameobject
     */
    @Override
    public String getOwner() {
        return owner;
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

}
