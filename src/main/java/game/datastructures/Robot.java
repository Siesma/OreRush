package game.datastructures;

import game.server.ServerConstants;

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
    public void parseGameObjectFromString(String encodedRobot){
        String[] encodedRobotArray = encodedRobot.split(String.valueOf((char) ':'));
        setPosition(Integer.parseInt(encodedRobotArray[1]), Integer.parseInt(encodedRobotArray[2]));
        try {
            String[] encodedGameObjectArray = new String[encodedRobotArray.length-3];
            for (int i = 3; i<encodedRobotArray.length; i++){
                encodedGameObjectArray[i] = encodedRobotArray[i];
            }
            GameObject inventoryObject = parseInventoryObjectFromString(encodedGameObjectArray);

            loadInventory(inventoryObject);
        }
        catch (Exception e){
           //TODO: Figure out what to do in this case
        }
    }
    private GameObject parseInventoryObjectFromString(String[] encodedGameObjectArray) throws Exception{
        GameObject gameObject;
        switch (encodedGameObjectArray[0]) {
            case "ore":
                gameObject = new Ore();
                break;
            case "radar":
                gameObject = new Radar();
                break;
            case "trap":
                gameObject = new Trap();
                break;
            default:
                throw new Exception();
        }
        String encodedGameObject = encodedGameObjectArray[0];
        for (int i = 1; i<encodedGameObjectArray.length; i++){
            encodedGameObject = encodedGameObject + ":" +encodedGameObjectArray[i];
        }
        gameObject.parseGameObjectFromString(encodedGameObject);
        return gameObject;
    }
}
