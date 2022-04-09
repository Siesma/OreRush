package game.datastructures;

import game.server.ServerConstants;

public abstract interface GameObject {

    /**
     * This function should encode all the information of a GameObject into a string to allow data
     * transfer via a package.
     * @return The encoded string that holds all the information of the robot
     */
    public String encodeToString();

    public void parseGameObjectFromString(String encodedGameObject);
}
