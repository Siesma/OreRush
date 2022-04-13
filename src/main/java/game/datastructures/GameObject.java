package game.datastructures;

public abstract interface GameObject {



    /**
     * This method sets the GameObject position based on a given x,y value.
     *
     * @param x The x coordinate the GameObject should move to
     * @param y The y coordinate the GameObject should move to
     */
    public void setPosition(int x, int y);

    /**
     * @return An array [x,y] of the current coordinates of the GameObject.
     */
    public int[] getPosition();

    /**
     * This function should encode all the information of a GameObject into a string to allow data
     * transfer via a package.
     * @return The encoded string that holds all the information of the robot
     */
    public String encodeToString();

    /**
     * This function will fill the GameObjects information based on a string from a packet
     * @param data the received string corresponding to the GameObject
     */
    public void fillGameObjectWithData (String... data);
}
