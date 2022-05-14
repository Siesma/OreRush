package game.datastructures;

/**
 * This GameObject is used to represent that there are no other Gameojects on the field.
 * Used to avoid null pointers.
 */
public class Nothing implements GameObject {

    /**
     * X Coordinate of the Nothing Object
     */
    private int x;
    /**
     * Y Coordinate of the Nothing Object
     */
    private int y;
    /**
     * The ID of the Nothing Object
     */
    private int id;
    /**
     * The "owner" of the Nothing Object. Necessary due to interface requirements but not used
     */
    private String owner;

    /**
     * The constructor of the Nothing Object
     */
    public Nothing () {
        this.owner = "";
    }

    /**
     * Setter for the ID
     * @param id the new ID
     */
    @Override
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Setter for the coordinates
     * @param x The x coordinate the GameObject should move to
     * @param y The y coordinate the GameObject should move to
     */
    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for the coordinates
     * @return array containing the X (at: 0) and Y (at: 1) coordinates
     */
    @Override
    public int[] getPosition() {
        return new int[]{x, y};
    }

    /**
     * Encodes the Nothing Object into a string
     * @return the encoded nothing object
     */
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

    /**
     * Getter for the owner
     * @return the name of the owner-
     */
    @Override
    public String getOwner() {
    return owner;
  }


}
