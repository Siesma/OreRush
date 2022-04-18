package game.datastructures;

import game.helper.FileHelper;
import game.packet.AbstractPacket;
import game.server.ServerConstants;

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

  private int playerID;

  @Override
  public void setID(int id) {
    this.playerID = id;
  }

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
   *
   * This function applies the new information.
   * This function implies that the wanted Action is valid.
   */
  public void setAction(RobotAction robotAction, int x, int y, Object optionalInventoryChange) {
    setPosition(x, y);
    if (optionalInventoryChange == null) {
      return;
    }
    if (!(optionalInventoryChange instanceof GameObject)) {
      return;
    }
    switch (robotAction) {
      case Dig:
      case RequestTrap:
      case RequestRadar:
        loadInventory((GameObject) optionalInventoryChange);
        break;
    }
  }

  public void setAction (RobotAction robotAction, int[] xy, Object optionalInventoryChange) {
    this.setAction(robotAction, xy[0], xy[1], optionalInventoryChange);
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
    coordinate[0] = xCoordinate;
    coordinate[1] = yCoordinate;
    return coordinate;
  }

  /**
   * @return The object currently stored in the robots inventory
   */
  public GameObject getInventory() {
    return inventory;
  }

  /**
   * @return The encoded string that holds all the information of the robot
   */
  public String encodeToString() {
    if(this.inventory == null) {
      return "Robot:" + this.playerID;
    }
    String encodedRobot = "robot:" + this.playerID + ":" + inventory.encodeToString();
    return encodedRobot;
  }


  @Override
  public void fillGameObjectWithData(String... data) {
    if (data.length == 1) {
      data = AbstractPacket.splitMessageBySpacer(data[0], String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER));
    }
    setPosition(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
    try {
      String[] encodedGameObjectArray = new String[data.length - 3];
      for (int i = 3; i < data.length; i++) {
        encodedGameObjectArray[i] = data[i];
      }
      GameObject inventoryObject = parseInventoryObjectFromString(encodedGameObjectArray);

      loadInventory(inventoryObject);
    } catch (Exception e) {
      //TODO: Figure out what to do in this case
    }
  }

  private GameObject parseInventoryObjectFromString(String[] encodedGameObjectArray) throws Exception {
    // Same algorithm but having new possible items is just as easy as with the packets!
    // it is also easier to maintain and less code in general
    Object obj = (new FileHelper()).createInstanceOfClass("");
    if (!(obj instanceof GameObject)) {
      return null;
    }
    GameObject gameObject = (GameObject) obj;
    gameObject.fillGameObjectWithData(AbstractPacket.removeFirstElement(encodedGameObjectArray));
    return gameObject;
  }
}