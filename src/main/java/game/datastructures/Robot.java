package game.datastructures;

import java.util.Vector;

/**
 * This Class represents a robot.
 * IT holds the following information:
 * X, Y Coordinates
 * Inventory
 */
public class Robot {
    private int xCoordinate;
    private int yCoordinate;
    private Object inventory;



    public void setPosition(int x, int y) {
        xCoordinate = x;
        yCoordinate = y;
    }
    public void loadInventory(Object objectToLoad) {
        inventory = objectToLoad;
    }

    public int[] getPosition() {
        int[] coordinate = new int[2];
        coordinate[0] = yCoordinate;
        coordinate[1] = xCoordinate;
        return coordinate;
    }
    public Object getInventory() {
        return inventory;
    }
}
