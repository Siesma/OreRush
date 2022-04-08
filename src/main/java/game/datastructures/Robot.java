package game.datastructures;

import java.util.Vector;

/**
 * This Class represents a robot.
 * IT holds the following information:
 * X, Y Coordinates
 * Inventory
 */
public class Robot implements GameObject {
    private int xCoordinate;
    private int yCoordinate;
    private GameObject inventory;


    /**
     * This method sets the robots position based on a given x,y value.
     *
     * @param x The x coordinate the robot should move to
     * @param y The y coordinate the robot should move to
     */
    public void setPosition(int x, int y) {
        xCoordinate = x;
        yCoordinate = y;
    }

    /**
     * This will put the given object into the inventory of the robot
     *
     * @param objectToLoad The object that should be loaded into the inventory of the robot
     */
    public void loadInventory(GameObject objectToLoad) {
        inventory = objectToLoad;
    }

    /**
     * @return An array [x,y] of the current coordinates of the robot.
     */
    public int[] getPosition() {
        int[] coordinate = new int[2];
        coordinate[0] = yCoordinate;
        coordinate[1] = xCoordinate;
        return coordinate;
    }

    /**
     * @return The object currently stored in the robots inventory
     */
    public GameObject getInventory() {
        return inventory;
    }

    /**
     *
     * @return The encoded string that holds all the information of the robot
     */
    public String encodeToString()
    {
        String encodedRobot = "robot:"+xCoordinate+":"+yCoordinate+":"+inventory.encodeToString();
        return encodedRobot;
    }
    public void parseGameObjectFromString(String encodedGameObject){
        //TODO: Implementation
    }
}
