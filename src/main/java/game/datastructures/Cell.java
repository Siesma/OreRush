package game.datastructures;

import java.util.ArrayList;

public class Cell {

    private final int x;
    private final int y;

    private final ArrayList<GameObject> placedObjects;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        placedObjects = new ArrayList<>();
    }

  /**
   *
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
   *
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
   * @param object that is being placed onto the map
   */
    public void place(GameObject object) {
        placedObjects.add(object);
    }

    public ArrayList<GameObject> getPlacedObjects() {
        return placedObjects;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (GameObject gameObject : placedObjects) {
            out.append(gameObject.toString());
        }
        return this.x + ":" + this.y + ":" + out;
    }

    public void remove(GameObject gameObject) {
        this.placedObjects.remove(gameObject);
    }
}
