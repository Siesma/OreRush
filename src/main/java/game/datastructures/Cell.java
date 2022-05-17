package game.datastructures;

import java.util.ArrayList;

/**
 * This class represents one square of the gameMap. It contains it's position and a list of Objects that are on that cell
 */
public class Cell {

  /**
   * X Coordinate of the Cell
   */
  private final int x;
  /**
   * Y Coordinate of the Cell
   */
  private final int y;

  /**
   * List of the GameObject currently on the cell
   */
  private final ArrayList<GameObject> placedObjects;

  /**
   * Constructor of the Cell. Used in GameMap to instantiate new Cells.
   * @param x X Coordinate of the Cell
   * @param y Y Coordinate of the Cell
   */
  public Cell(int x, int y) {
    this.x = x;
    this.y = y;
    placedObjects = new ArrayList<>();
  }

  /**
   * @return a list of all the different Robots that are on this cell.
   */
  public ArrayList<Robot> robotsOnCell() {
    ArrayList<Robot> robots = new ArrayList<>();
    for (GameObject gameObject : placedObjects) {
      if (gameObject instanceof Robot) {
        robots.add((Robot) gameObject);
      }
    }
    if (robots.size() == 0) {
      return null;
    }
    return robots;
  }

  /**
   * This function should only find one radar anyway because if on a cell someone is digging it should remove every other radar.
   *
   * @return the current Radar that is present on this cell, will return null if no Radar is present.
   */
  public Radar radarOnCell() {
    Radar out = null;
    for (GameObject gameObject : placedObjects) {
      if (gameObject instanceof Radar) {
        out = (Radar) gameObject;
      }
    }
    return out;
  }


  /**
   * This function should only find one trap anyway because if someone is digging on a cell every trap is set off.
   *
   * @return the current Trap that is present on this cell, will return null if no Trap is present.
   */
  public Trap trapOnCell() {
    Trap out = null;
    for (GameObject gameObject : placedObjects) {
      if (gameObject instanceof Trap) {
        out = (Trap) gameObject;
      }
    }
    return out;
  }

  /**
   * @return a list of all the different Ores that are on the given cell. This can include two times the same ore.
   */
  public ArrayList<Ore> oreOnCell() {
    ArrayList<Ore> ores = new ArrayList<>();
    for (GameObject gameObject : placedObjects) {
      if (gameObject instanceof Ore) {
        ores.add((Ore) gameObject);
      }
    }
    if (ores.size() == 0) {
      return null;
    }
    return ores;
  }

  /**
   * This function should only be called upon initialization and not during the actual update mechanic!
   * This function adds the "object" (Paramter) onto this cell's ArrayList of GameObjects.
   *
   * @param object that is being placed onto the map
   */
  public void place(GameObject object) {
    placedObjects.add(object);
  }

  /**
   * Getter for the X Coordinate
   * @return the X Coordinate of the Cell
   */
  public int getX() {
    return x;
  }

  /**
   * Getter for the Y Coordinate
   * @return the Y Coordinate of the Cell
   */
  public int getY() {
    return y;
  }

  /**
   * Getter for the list of Game Objects in the Cell
   * @return the list of Game Objects in the Cell
   */
  public ArrayList<GameObject> getPlacedObjects() {
    return placedObjects;
  }

  /**
   * Encodes the cell and it's contents to a string.
   * @return all the necessary information about this cell in a Stringformat to be able to send and decode it everywhere and end up with the same object.
   */
  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    for (GameObject gameObject : placedObjects) {
      out.append(gameObject.encodeToString());
    }
    return this.x + ":" + this.y + ":" + out;
  }

  /**
   * removes a specific game object of the cell
   * @param gameObject game object that is to be removed
   */
  public void remove(GameObject gameObject) {
    this.placedObjects.remove(gameObject);
  }
}
