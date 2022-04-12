package game.datastructures;

/**
 * This Class represents a trap.
 * IT holds the following information:
 * X, Y Coordinates
 * Player who laid the trap
 */
public class Trap implements GameObject {

  @Override
  public String encodeToString() {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  @Override
  public void parseGameObjectFromString(String encodedGameObject) {

  }

  @Override
  public void fillGameObjectWithData(String... data) {

  }
}
