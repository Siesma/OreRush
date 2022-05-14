package game.datastructures;

/**
 * This GameObject is used to represent that there are no other Gameojects on the field.
 * Used to avoid null pointers.
 */
public class Nothing implements GameObject {

    /**
     * X Coordinate of the Cell
     */
    private int x;
    /**
     * Y Coordinate of the Cell
     */
    private int y;
    /**
     *
     */
    private int id;
    private String owner;

  public Nothing () {
    this.owner = "";
  }
  
  /**
   * Default constructor to set the owner of this type as an empty string.
   */
    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int[] getPosition() {
        return new int[]{x, y};
    }

    @Override
    public String encodeToString() {
        return "Nothing:" + id + ":" + this.owner;
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

  @Override
  public String getOwner() {
    return owner;
  }


}
