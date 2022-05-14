package game.datastructures;

/**
 * Interface for all game objects, Ore, Robot, Radar, Trap.
 *
 */
public interface GameObject {

    void setID(int id);

    /**
     * This method sets the GameObject position based on a given x,y value.
     *
     * @param x The x coordinate the GameObject should move to
     * @param y The y coordinate the GameObject should move to
     */
    void setPosition(int x, int y);

    /**
     * @return An array [x,y] of the current coordinates of the GameObject.
     */
    int[] getPosition();

    /**
     * This function should encode all the information of a GameObject into a string to allow data
     * transfer via a package.
     *
     * @return The encoded string that holds all the information of the robot
     */
    String encodeToString();

    String getOwner();

    /**
     * Sets the Owner of a given Object on the Map. This is used to keep track of who the radar and or Trap belongs to.
     *
     * @param nameOfOwner the name of the player creating this gameObject.
     */
    void setOwner(String nameOfOwner);
}
