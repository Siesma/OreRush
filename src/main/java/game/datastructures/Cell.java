package game.datastructures;

import java.util.ArrayList;

public class Cell {

  private int x;
  private int y;

  private ArrayList<GameObject> placedObjects;

  public Cell(int x, int y) {
    this.x = x;
    this.y = y;
    placedObjects = new ArrayList<>();
  }

  public ArrayList<Robot> robotsOnCell() {
    ArrayList<Robot> robots = new ArrayList<>();
    for (GameObject gameObject : placedObjects) {
      if (gameObject instanceof Robot) {
        robots.add((Robot) gameObject);
      }
    }
    if(robots.size() == 0) {
      return null;
    }
    return robots;
  }

  public Radar radarOnCell () {
    Radar out = null;
    for (GameObject gameObject : placedObjects) {
      if(gameObject instanceof Radar) {
        out = (Radar) gameObject;
      }
    }
    return out;
  }

  public Trap trapOnCell () {
    Trap out = null;
    for (GameObject gameObject : placedObjects) {
      if(gameObject instanceof Trap) {
        out = (Trap) gameObject;
      }
    }
    return out;
  }

  public ArrayList<Ore> oreOnCell () {
    ArrayList<Ore> ores = new ArrayList<>();
    for (GameObject gameObject : placedObjects) {
      if (gameObject instanceof Ore) {
        ores.add((Ore) gameObject);
      }
    }
    if(ores.size() == 0) {
      return null;
    }
    return ores;
  }

  public void place(GameObject object) {
    placedObjects.add(object);
  }

  public ArrayList<GameObject> getPlacedObjects() {
    return placedObjects;
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    for(GameObject gameObject : placedObjects) {
      out.append(gameObject.toString());
    }
    return this.x + ":" + this.y + ":" + out.toString();
  }

  public void remove(GameObject gameObject) {
    this.placedObjects.remove(gameObject);
  }
}
